package io.github.liyze09.arms.world.gen

import io.github.liyze09.arms.world.Chunk

interface WorldgenProvider {
    fun getChunk(x: Int, z: Int): Chunk
}
