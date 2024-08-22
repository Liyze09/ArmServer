package net.minecraftarm.world

import io.github.liyze09.arms.network.PackUtils.writeVarInt
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import net.minecraftarm.common.*
import net.minecraftarm.nbt.NbtCompound
import net.minecraftarm.nbt.NbtLongArray
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.math.ceil
import kotlin.math.log2

class Chunk(
    val x: Int,
    val z: Int,
    val dimension: Dimension,
    val minY: Int,
    val height: Int,
) {
    init {
        if (height % 16 != 0) {
            throw IllegalArgumentException("Height must be a multiple of 16")
        }
    }

    val maxY = minY + height
    private val heightLock = ReentrantReadWriteLock()
    private val childChunks = Array(height / 16) { this.ChildChunk() }
    private var proto = true
    internal fun sections() = childChunks
    fun isProto() = proto
    fun getBlockStateIDByChunkPosition(x: Int, y: Int, z: Int): Int {
        if (x < 0 || x > 15 || z < 0 || z > 15 || y < minY || y > maxY) {
            throw IllegalArgumentException("Position out of bounds")
        }
        return childChunks[(y - minY) / 16].getBlockState(x, y % 16, z)
    }

    fun setBlockStateIDByChunkPosition(x: Int, y: Int, z: Int, id: Int) {
        if (x < 0 || x > 15 || z < 0 || z > 15 || y < minY || y > maxY) {
            throw IllegalArgumentException("Position out of bounds")
        }
        childChunks[(y - minY) / 16]./*Cache will be invalidated there →*/setBlockState(x, y % 16, z, id)
    }

    private val worldSurface = ShortArray(256)
    private val motionBlocking = ShortArray(256)
    fun getWorldSurface() = worldSurface
    fun getMotionBlocking() = motionBlocking
    fun getWorldSurface(x: Int, z: Int): Short {
        try {
            heightLock.readLock().lock()
            return worldSurface[to8bitXZ(x, z)]
        } finally {
            heightLock.readLock().unlock()
        }
    }

    fun setWorldSurface(x: Int, z: Int, height: Short) {
        invalidateCache()
        try {
            heightLock.writeLock().lock()
            worldSurface[to8bitXZ(x, z)] = height
        } finally {
            heightLock.writeLock().unlock()
        }
    }

    fun getMotionBlocking(x: Int, z: Int): Short {
        try {
            heightLock.readLock().lock()
            return motionBlocking[to8bitXZ(x, z)]
        } finally {
            heightLock.readLock().unlock()
        }
    }

    fun setMotionBlocking(x: Int, z: Int, height: Short) {
        invalidateCache()
        try {
            heightLock.writeLock().lock()
            motionBlocking[to8bitXZ(x, z)] = height
        } finally {
            heightLock.writeLock().unlock()
        }
    }

    fun updateBlockLight(x: Int, y: Int, z: Int, light: Int) {
        invalidateCache()
        childChunks[y / 16].setBlockLight(x, y % 16, z, light)
    }

    fun updateSkyLight(x: Int, y: Int, z: Int, light: Int) {
        invalidateCache()
        childChunks[y / 16].setSkyLight(x, y % 16, z, light)
    }

    internal inner class ChildChunk {
        var biome = IntArray(64)
        val blocks = IntArray(4096)
        private var blockCount = 0
        val blockLight = ByteArray(2048)
        val skyLight = ByteArray(2048) { dimension.dimensionType.ambientLight.toInt().toByte() }
        private val lock = ReentrantReadWriteLock()
        fun getBlockCount() = blockCount
        fun getBiome(x: Int, y: Int, z: Int): Int {
            return biome[to6bitYZX(x, y, z)]
        }

        fun setBiome(x: Int, y: Int, z: Int, biome: Int) {
            invalidateCache()
            this.biome[to6bitYZX(x, y, z)] = biome
        }
        fun getBlockState(x: Int, y: Int, z: Int): Int {
            try {
                lock.readLock().lock()
                return blocks[toYZX(x, y, z)]
            } finally {
                lock.readLock().unlock()
            }
        }
        fun setBlockState(x: Int, y: Int, z: Int, state: Int) {
            invalidateCache()
            val yzx = toYZX(x, y, z)
            try {
                lock.writeLock().lock()
                val before = blocks[yzx]
                if (before == 0 && state != 0) blockCount++
                else if (state == 0 && before != 0) blockCount--
                blocks[yzx] = state
            } finally {
                lock.writeLock().unlock()
            }
        }

        fun getBlockLight(x: Int, y: Int, z: Int): Int {
            try {
                lock.readLock().lock()
                val yzx = toYZX(x, y, z)
                return (blockLight[yzx shr 1].toInt() shr (4 * (yzx and 1))) and 0xF
            } finally {
                lock.readLock().unlock()
            }
        }

        fun setBlockLight(x: Int, y: Int, z: Int, light: Int) {
            invalidateCache()
            try {
                lock.writeLock().lock()
                val yzx = toYZX(x, y, z)
                blockLight[yzx shr 1] = (if (yzx and 1 == 0) {
                    (blockLight[yzx shr 1].toInt() and 0xF0) or (light and 0xF)
                } else {
                    (blockLight[yzx shr 1].toInt() and 0xF) or ((light and 0xF) shl 4)
                }).toByte()
            } finally {
                lock.writeLock().unlock()
            }
        }

        fun getSkyLight(x: Int, y: Int, z: Int): Int {
            try {
                val yzx = toYZX(x, y, z)
                lock.readLock().lock()
                return (skyLight[yzx shr 1].toInt() shr (4 * (yzx and 1))) and 0xF
            } finally {
                lock.readLock().unlock()
            }

        }

        fun setSkyLight(x: Int, y: Int, z: Int, light: Int) {
            invalidateCache()
            try {
                val yzx = toYZX(x, y, z)
                lock.writeLock().lock()
                skyLight[yzx shr 1] = (if (yzx and 1 == 0) {
                    (skyLight[yzx shr 1].toInt() and 0xF0) or (light and 0xF)
                } else {
                    (skyLight[yzx shr 1].toInt() and 0xF) or ((light and 0xF) shl 4)
                }).toByte()
            } finally {
                lock.writeLock().unlock()
            }
        }

        fun writeToBuffer(sectionsBuf: ByteBuf) {
            sectionsBuf.writeShort(this.getBlockCount())
            val palette = mutableSetOf<Int>()
            for (id in this.blocks) {
                palette.add(id)
            }
            val bitPerEntry = when {
                palette.size <= 2 -> 1
                palette.size <= 4 -> 2
                palette.size <= 8 -> 3
                palette.size <= 16 -> 4
                palette.size <= 32 -> 5
                palette.size <= 64 -> 6
                palette.size <= 128 -> 7
                palette.size <= 256 -> 8
                else -> 15
            }
            val array: IntArray?
            if (bitPerEntry == 15) {
                array = this.blocks
            } else {
                sectionsBuf.writeVarInt(bitPerEntry)
                sectionsBuf.writeVarInt(palette.size)
                for (id in palette) {
                    sectionsBuf.writeVarInt(id)
                }
                array = IntArray(4096)
                for ((index, id) in this.blocks.withIndex()) {
                    array[index] = id
                }
            }
            writeData(bitPerEntry, array, sectionsBuf)

            sectionsBuf.writeVarInt(6)
            writeData(6, this.biome, sectionsBuf)
        }
    }

    val bpe: Int = (ceil(log2(height.toFloat())) + 1).toInt()
    val u = 64 / bpe
    val mask = (1L shl (bpe - 1)) - 1L

    private fun writeToBuffer(buf: ByteBuf) {
        val motionBlocking = LongArray(height / u)
        val worldSurface = LongArray(height / u)
        this.worldSurface.forEachIndexed { index, value ->
            val p = from8bitXZ(index)
            val i = p.first + p.second * 16
            worldSurface[i / u] = worldSurface[i / u] and (mask.inv() shl (i % u))
            worldSurface[i / u] = worldSurface[i / u] or ((value.toLong() - minY) and mask shl (i % u))
        }
        this.motionBlocking.forEachIndexed { index, value ->
            val k = from8bitXZ(index)
            val i = k.first + k.second * 16
            motionBlocking[i / u] = motionBlocking[i / u] and (mask.inv() shl (i % u))
            motionBlocking[i / u] = motionBlocking[i / u] or ((value.toLong() - minY) and mask shl (i % u))
        }
        // Heightmap
        NbtCompound(
            "MOTION_BLOCKING" to NbtLongArray(motionBlocking),
            "WORLD_SURFACE" to NbtLongArray(worldSurface)
        ).encodeAsRoot(buf)
        val sectionsBuf = ByteBufAllocator.DEFAULT.heapBuffer()
        this.childChunks.forEach { chunk ->
            chunk.writeToBuffer(sectionsBuf)
        }
        buf.writeVarInt(sectionsBuf.writerIndex())
        buf.writeBytes(sectionsBuf)
        sectionsBuf.release()

        buf.writeVarInt(0) // TODO Block Entities

        // Light
        val skyLightMask = BitSet(childChunks.size + 2)
        val blockLightMask = BitSet(childChunks.size + 2)
        val emptySkyLightMask = BitSet(childChunks.size + 2)
        val emptyBlockLightMask = BitSet(childChunks.size + 2)

        skyLightMask[0] = false
        blockLightMask[0] = false
        emptySkyLightMask[0] = true
        emptyBlockLightMask[0] = true
        skyLightMask[childChunks.size + 1] = false
        blockLightMask[childChunks.size + 1] = false
        emptySkyLightMask[childChunks.size + 1] = true
        emptyBlockLightMask[childChunks.size + 1] = true

        val skyLightBuf = ByteBufAllocator.DEFAULT.heapBuffer()
        val blockLightBuf = ByteBufAllocator.DEFAULT.heapBuffer()

        for (i in childChunks.indices) {
            val chunk = childChunks[i]

            var hasNonZeroSkyLight = false
            var hasNonZeroBlockLight = false

            for (lightValue in chunk.skyLight) {
                if (lightValue != 0.toByte()) {
                    hasNonZeroSkyLight = true
                    break
                }
            }

            for (lightValue in chunk.blockLight) {
                if (lightValue != 0.toByte()) {
                    hasNonZeroBlockLight = true
                    break
                }
            }

            skyLightMask[i] = hasNonZeroSkyLight
            blockLightMask[i] = hasNonZeroBlockLight
            emptySkyLightMask[i] = !hasNonZeroSkyLight
            emptyBlockLightMask[i] = !hasNonZeroBlockLight

            if (hasNonZeroSkyLight) {
                skyLightBuf.writeVarInt(2048)
                skyLightBuf.writeBytes(chunk.skyLight)
            }

            if (hasNonZeroBlockLight) {
                blockLightBuf.writeVarInt(2048)
                blockLightBuf.writeBytes(chunk.blockLight)
            }
        }
        skyLightMask.writeToBuffer(buf)
        blockLightMask.writeToBuffer(buf)
        emptySkyLightMask.writeToBuffer(buf)
        emptyBlockLightMask.writeToBuffer(buf)

        buf.writeVarInt(skyLightMask.cardinality())
        buf.writeBytes(skyLightBuf)
        buf.writeVarInt(blockLightMask.cardinality())
        buf.writeBytes(blockLightBuf)
        skyLightBuf.release()
        blockLightBuf.release()
    }

    fun upgradeChunk() {
        if (!proto) return
        dimension.worldgen.genChunk(x, z, this)
        dimension.worldgen.updateHeightMap(this)
        this.proto = true
    }

    @Volatile
    private var cache: ByteBuf? = null
    private val cacheLock = ReentrantReadWriteLock()

    @Volatile
    private var cacheLength = 0
    fun getCachedBytes(): ByteBuf {
        try {
            cacheLock.readLock().lock()
            if (cache != null) return cache!!.slice(0, cacheLength)
            val buf = ByteBufAllocator.DEFAULT.heapBuffer()
            writeToBuffer(buf)
            cacheLength = buf.writerIndex()
            cache = buf
            return buf
        } finally {
            cacheLock.readLock().unlock()
        }
    }

    fun invalidateCache() {
        try {
            cacheLock.writeLock().lock()
            cache?.release()
            cache = null
        } finally {
            cacheLock.writeLock().unlock()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Chunk) return false
        return x == other.x && z == other.z && dimension.name == other.dimension.name
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + z
        result = 31 * result + dimension.hashCode()
        return result
    }

    companion object {
        fun writeData(bitPerEntry: Int, array: IntArray, sectionsBuf: ByteBuf) {
            val dataBuf = ByteBufAllocator.DEFAULT.heapBuffer(bitPerEntry * 4096)
            val data = LongArray(4096 / (64 / bitPerEntry))
            array.forEachIndexed { index, id ->
                data[index / (64 / bitPerEntry)] =
                    data[index / (64 / bitPerEntry)] or (id.toLong() shl (index % (64 / bitPerEntry) * bitPerEntry))
            }
            for (i in data.indices) {
                dataBuf.writeLong(data[i])
            }
            sectionsBuf.writeVarInt(data.size)
            sectionsBuf.writeBytes(dataBuf)
            dataBuf.release()
        }
    }
}

