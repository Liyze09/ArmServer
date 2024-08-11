package net.minecraftarm.world.gen

import net.minecraftarm.world.Chunk

interface WorldgenProvider {
    fun getChunk(x: Int, z: Int): Chunk
}
