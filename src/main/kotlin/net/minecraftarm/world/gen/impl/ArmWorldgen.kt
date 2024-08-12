package net.minecraftarm.world.gen.impl

import net.minecraftarm.world.Chunk
import net.minecraftarm.world.gen.WorldgenProvider

object ArmWorldgen : WorldgenProvider {
    // TODO Normal Worldgen
    override fun genChunk(x: Int, z: Int, chunk: Chunk): Chunk {
        repeat(16) { cx ->
            repeat(16) { cz ->
                chunk.setBlockStateIDByChunkPosition(
                    cx,
                    PerlinNoise.noise(
                        (16 * x + cx).toDouble(),
                        0.0,
                        (16 * z + cz).toDouble()
                    ).toInt(),
                    cz,
                    9
                )
            }
        }
        return chunk
    }
}