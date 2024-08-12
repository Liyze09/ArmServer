package net.minecraftarm.world.gen

import net.minecraftarm.world.Chunk

interface WorldgenProvider {
    fun genChunk(x: Int, z: Int, chunk: Chunk): Chunk
}
