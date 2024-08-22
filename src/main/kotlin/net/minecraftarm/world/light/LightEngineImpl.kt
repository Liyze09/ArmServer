package net.minecraftarm.world.light

import net.minecraftarm.world.Chunk
import net.minecraftarm.world.Dimension

object LightEngineImpl : LightEngine {
    override fun updateLight(dimension: Dimension) {
        dimension.getChunks().forEach { chunk ->
            chunk.upgradeChunk()
            repeat(16) { x ->
                repeat(16) { z ->
                    val y = chunk.getWorldSurface(x, z).toInt()
                    chunk.updateSkyLight(x, y, z, dimension.dimensionType.ambientLight.toInt())
                }
            }
        }
    }

    override fun updateLight(chunk: Chunk) {
        chunk.upgradeChunk()
        if (chunk.dimension.dimensionType.hasSkylight) {
            repeat(16) { x ->
                repeat(16) { z ->
                    val y = chunk.getWorldSurface(x, z).toInt()
                    if (y <= chunk.minY) {
                        return
                    }
                    chunk.updateSkyLight(x, y, z, 15)
                }
            }
        }
    }

}