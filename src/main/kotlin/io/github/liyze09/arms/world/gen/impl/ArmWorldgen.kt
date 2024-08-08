package io.github.liyze09.arms.world.gen.impl

import io.github.liyze09.arms.world.Chunk
import io.github.liyze09.arms.world.gen.WorldgenProvider

object ArmWorldgen : WorldgenProvider {
    // TODO Normal Worldgen
    override fun getChunk(x: Int, z: Int): Chunk {
        val chunk = Chunk()
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