package net.minecraftarm.world

import net.minecraftarm.api.event.EventRelater
import net.minecraftarm.common.Identifier
import net.minecraftarm.common.Position
import net.minecraftarm.entity.Entity
import net.minecraftarm.registry.DimensionType
import net.minecraftarm.registry.block.BlockState
import net.minecraftarm.registry.block.blockStatesByProtocolId
import net.minecraftarm.registry.block.idByBlockState
import net.minecraftarm.world.gen.WorldgenProvider
import java.util.concurrent.ConcurrentHashMap

abstract class Dimension(val worldgen: WorldgenProvider) {
    abstract val dimensionType: DimensionType
    abstract val name: Identifier
    private val chunkMap = ConcurrentHashMap<Position, Chunk>(512)
    val entityMap = ConcurrentHashMap<Position, Entity>(128)
    fun getChunk(position: Position): Chunk {
        return chunkMap.getOrPut(position) {
            // TODO Load save
            worldgen.getChunk(position.x, position.z)
        }
    }

    fun updateBlockState(position: Position, state: BlockState) {
        val chunk = getChunk(Position(position.x shr 4, 0, position.z shr 4))
        EventRelater.broadcastEvent("BLOCK_UPDATE", Pair(position, state))
        chunk.setBlockStateIDByChunkPosition(
            position.x and 15,
            position.y,
            position.z and 15,
            idByBlockState[state] ?: throw IllegalArgumentException("BlockState $state not found")
        )
    }

    fun getBlockState(position: Position): BlockState {
        val chunk = getChunk(Position(position.x shr 4, 0, position.z shr 4))
        return blockStatesByProtocolId[chunk.getBlockStateIDByChunkPosition(
            position.x and 15,
            position.y,
            position.z and 15
        )] ?: throw IllegalArgumentException("BlockState not found")
    }

    abstract fun getProtocolId(): Int
}