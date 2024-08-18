package net.minecraftarm.world

import net.minecraftarm.common.toYZX
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
    private val childChunks = Array(height / 16) { ChildChunk() }
    internal fun sections() = childChunks
    internal fun getBlockStateIDByChunkPosition(x: Int, y: Int, z: Int): Int {
        if (x < 0 || x > 15 || z < 0 || z > 15 || y < minY || y > maxY) {
            throw IllegalArgumentException("Position out of bounds")
        }
        return childChunks[(y - minY) / 16].getBlockState(x, y % 16, z)
    }

    internal fun setBlockStateIDByChunkPosition(x: Int, y: Int, z: Int, id: Int) {
        if (x < 0 || x > 15 || z < 0 || z > 15 || y < minY || y > maxY) {
            throw IllegalArgumentException("Position out of bounds")
        }
        childChunks[(y - minY) / 16].setBlockState(x, y % 16, z, id)
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

    internal class ChildChunk {
        private val blocks = IntArray(4096)
        private var blockCount = 0
        private val blockLight = ByteArray(2048)
        private val skyLight = ByteArray(2048)
        private val lock = ReentrantReadWriteLock()
        fun getBlockCount() = blockCount
        fun getBlockArray() = blocks
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
    }
}

