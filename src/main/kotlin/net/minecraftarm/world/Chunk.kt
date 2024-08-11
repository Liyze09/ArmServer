package net.minecraftarm.world

import java.util.concurrent.locks.ReentrantReadWriteLock

class Chunk {
    private val lock = ReentrantReadWriteLock()
    private val blocks: Array<Array<IntArray>> = Array(16) { Array(16) { IntArray(512) { 0/*Air*/ } } }
    internal fun getBlockStateIDByChunkPosition(x: Int, y: Int, z: Int): Int {
        try {
            lock.readLock().lock()
            if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 511) {
                throw IllegalArgumentException("Position out of bounds")
            }
            return blocks[x][y][z + 128]
        } finally {
            lock.readLock().unlock()
        }
    }

    internal fun setBlockStateIDByChunkPosition(x: Int, y: Int, z: Int, id: Int) {
        try {
            lock.writeLock().lock()
            if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 511) {
                throw IllegalArgumentException("Position out of bounds")
            }
            blocks[x][y][z + 128] = id
        } finally {
            lock.writeLock().unlock()
        }
    }
}