package net.minecraftarm.world

import io.netty.util.collection.IntObjectHashMap
import io.netty.util.collection.LongObjectHashMap
import net.minecraftarm.api.event.EventRelater
import net.minecraftarm.common.BlockPosition
import net.minecraftarm.common.ConcurrentMap
import net.minecraftarm.common.Identifier
import net.minecraftarm.common.toXZ
import net.minecraftarm.entity.Entity
import net.minecraftarm.registry.DimensionType
import net.minecraftarm.registry.block.BlockState
import net.minecraftarm.registry.block.blockStatesByProtocolId
import net.minecraftarm.registry.block.idByBlockState
import net.minecraftarm.world.gen.WorldgenProvider

abstract class Dimension(val worldgen: WorldgenProvider) {
    abstract val dimensionType: DimensionType
    abstract val name: Identifier
    private val chunkMap = ConcurrentMap(LongObjectHashMap<Chunk>(512))
    val entities = ConcurrentMap(IntObjectHashMap<Entity>(128))

    fun getChunk(position: BlockPosition): Chunk {
        return chunkMap.getOrPut(toXZ(position.x, position.z)) {
            val chunk = Chunk(dimensionType.height, dimensionType.minY)
            // TODO Load save
            worldgen.genChunk(position.x, position.z, chunk)
        }
    }

    internal fun updateBlockState(position: BlockPosition, state: BlockState) {
        val chunk = getChunk(BlockPosition(position.x shr 4, 0, position.z shr 4))
        EventRelater.broadcastEvent("BLOCK_UPDATE", Pair(position, state))
        chunk.setBlockStateIDByChunkPosition(
            position.x and 15,
            position.y,
            position.z and 15,
            idByBlockState[state] ?: throw IllegalArgumentException("BlockState $state not found")
        )
    }

    internal fun updateBlockState(position: BlockPosition, state: Int) {
        val chunk = getChunk(BlockPosition(position.x shr 4, 0, position.z shr 4))
        EventRelater.broadcastEvent("BLOCK_UPDATE", Pair(position, state))
        chunk.setBlockStateIDByChunkPosition(
            position.x and 15,
            position.y,
            position.z and 15,
            state
        )
    }

    fun getBlockState(position: BlockPosition): BlockState {
        val chunk = getChunk(BlockPosition(position.x shr 4, 0, position.z shr 4))
        return blockStatesByProtocolId[chunk.getBlockStateIDByChunkPosition(
            position.x and 15,
            position.y,
            position.z and 15
        )] ?: throw IllegalArgumentException("BlockState not found")
    }

    abstract fun getProtocolId(): Int
}