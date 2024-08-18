package net.minecraftarm.world.gen

import net.minecraftarm.registry.block.blockStatesByProtocolId
import net.minecraftarm.world.Chunk
import net.minecraftarm.world.exception.NotFoundException

interface WorldgenProvider {
    fun genChunk(x: Int, z: Int, chunk: Chunk): Chunk
    fun updateHeightMap(chunk: Chunk): Chunk {
        for (x in 0..15)
            for (z in 0..15) {
                var highest = chunk.minY - 1
                var motionBlocking = chunk.minY - 1
                for (y in chunk.maxY downTo chunk.minY) {
                    val block = chunk.getBlockStateIDByChunkPosition(x, y, z)
                    if (block != 0) {
                        if (highest != chunk.minY - 1)
                            highest = y
                        if (blockStatesByProtocolId[block]?.parent?.blockSettings?.motionBlocking
                                ?: throw NotFoundException("Block state $block not found")
                        ) {
                            motionBlocking = y
                            break
                        }
                    }
                }
                chunk.setWorldSurface(x, z, highest.toLong())
                chunk.setMotionBlocking(x, z, motionBlocking.toLong())
            }
        return chunk
    }
}
