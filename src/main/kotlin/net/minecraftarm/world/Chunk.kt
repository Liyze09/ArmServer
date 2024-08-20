package net.minecraftarm.world

import io.github.liyze09.arms.network.PackUtils.writeVarInt
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import net.minecraftarm.common.to6bitYZX
import net.minecraftarm.common.toYZX
import net.minecraftarm.common.writeToBuffer
import net.minecraftarm.nbt.NbtCompound
import net.minecraftarm.nbt.NbtLongArray
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.math.ceil
import kotlin.math.log2

class Chunk(
    val x: Int,
    val z: Int,
    height: Int,
    val minY: Int,
    arrayLength: Int = 256 / (64 / log2(height.toFloat())).toInt() + 1
) {
    init {
        if (height % 16 != 0) {
            throw IllegalArgumentException("Height must be a multiple of 16")
        }
    }

    val maxY = minY + height
    private val heightLock = ReentrantReadWriteLock()
    private val childChunks = Array(height / 16) { this.ChildChunk() }
    internal fun sections() = childChunks
    internal fun getBlockStateIDByChunkPosition(x: Int, y: Int, z: Int): Int {
        if (x < 0 || x > 15 || z < 0 || z > 15 || y < minY || y > maxY) {
            throw IllegalArgumentException("Position out of bounds")
        }
        return childChunks[(y - minY) / 16 - 1].getBlockState(x, y % 16, z)
    }

    internal fun setBlockStateIDByChunkPosition(x: Int, y: Int, z: Int, id: Int) {
        if (x < 0 || x > 15 || z < 0 || z > 15 || y < minY || y > maxY) {
            throw IllegalArgumentException("Position out of bounds")
        }
        childChunks[(y - minY) / 16 - 1].setBlockState(x, y % 16, z, id)
    }

    private val worldSurface = LongArray(arrayLength)
    private val motionBlocking = LongArray(arrayLength)
    private val b: Int = ceil(log2(height.toFloat())).toInt() // 计算对2的对数后向上取整
    private val u = 64 / b
    private val anv = ((1L shl b) - 1L)
    fun getWorldSurface() = worldSurface
    fun getMotionBlocking() = motionBlocking
    fun getWorldSurface(x: Int, z: Int): Long {
        try {
            val i = x + 16 * z
            heightLock.readLock().lock()
            return (worldSurface[i / u] shr (i % u)) and anv + minY
        } finally {
            heightLock.readLock().unlock()
        }
    }

    fun setWorldSurface(x: Int, z: Int, height: Long) {
        try {
            val i = x + 16 * z
            heightLock.writeLock().lock()
            var value = worldSurface[i / u]
            value = value or (height shl (i % u))
            value = value and (anv shl (i % u)).inv()
            worldSurface[i / u] = value
        } finally {
            heightLock.writeLock().unlock()
        }
    }

    fun getMotionBlocking(x: Int, z: Int): Long {
        try {
            val i = x + 16 * z
            heightLock.readLock().lock()
            return (motionBlocking[i / u] shr (i % u)) and anv + minY
        } finally {
            heightLock.readLock().unlock()
        }
    }

    fun setMotionBlocking(x: Int, z: Int, height: Long) {
        try {
            val i = x + 16 * z
            heightLock.writeLock().lock()
            var value = motionBlocking[i / u]
            value = value or (height shl (i % u))
            value = value and (anv shl (i % u)).inv()
            motionBlocking[i / u] = value
        } finally {
            heightLock.writeLock().unlock()
        }
    }

    internal inner class ChildChunk {
        var biome = IntArray(64)
        val blocks = IntArray(4096)
        private var blockCount = 0
        val blockLight = ByteArray(2048)
        val skyLight = ByteArray(2048)
        private val lock = ReentrantReadWriteLock()
        fun getBlockCount() = blockCount
        fun getBiome(x: Int, y: Int, z: Int): Int {
            return biome[to6bitYZX(x, y, z)]
        }

        fun setBiome(x: Int, y: Int, z: Int, biome: Int) {
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

    fun writeToBuffer(buf: ByteBuf) {
        NbtCompound(
            "MOTION_BLOCKING" to NbtLongArray(this.motionBlocking),
            "WORLD_SURFACE" to NbtLongArray(this.worldSurface)
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
                sectionsBuf.writeBytes(chunk.skyLight)
            }

            if (hasNonZeroBlockLight) {
                blockLightBuf.writeVarInt(2048)
                sectionsBuf.writeBytes(chunk.blockLight)
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

