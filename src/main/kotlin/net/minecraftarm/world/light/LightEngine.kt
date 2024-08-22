package net.minecraftarm.world.light

import net.minecraftarm.world.Chunk
import net.minecraftarm.world.Dimension

interface LightEngine {
    fun updateLight(dimension: Dimension)
    fun updateLight(chunk: Chunk)

    companion object {
        fun getDefault(): LightEngine {
            return LightEngineImpl
        }
    }
}