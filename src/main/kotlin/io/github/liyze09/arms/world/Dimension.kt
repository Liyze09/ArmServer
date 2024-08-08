package io.github.liyze09.arms.world

import io.github.liyze09.arms.api.event.EventRelater
import io.github.liyze09.arms.common.Identifier
import io.github.liyze09.arms.common.Position
import io.github.liyze09.arms.entity.Entity
import io.github.liyze09.arms.registry.BlockState
import io.github.liyze09.arms.registry.blockStatesByProtocolId
import io.github.liyze09.arms.registry.idByBlockState
import io.github.liyze09.arms.world.gen.WorldgenProvider
import java.util.concurrent.ConcurrentHashMap

abstract class Dimension(val worldgen: WorldgenProvider) {
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