package net.minecraftarm.world

import net.minecraftarm.api.event.EventRelater
import net.minecraftarm.common.BlockPosition
import net.minecraftarm.common.Identifier
import net.minecraftarm.common.toXZ
import net.minecraftarm.entity.Entity
import net.minecraftarm.registry.DimensionType
import net.minecraftarm.registry.block.BlockState
import net.minecraftarm.registry.block.blockStatesByProtocolId
import net.minecraftarm.registry.block.idByBlockState
import net.minecraftarm.world.gen.WorldgenProvider
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.log2

abstract class Dimension(val worldgen: WorldgenProvider) {
    abstract val dimensionType: DimensionType
    abstract val name: Identifier
    private val chunkMap = ConcurrentHashMap<Long, Chunk>(512)
    val entities = ConcurrentHashMap<Int, Entity>(128)
    private val cachedInt by lazy { 256 / (64 / log2(dimensionType.height.toFloat())).toInt() + 1 }
    fun getChunk(x: Int, z: Int): Chunk {
        return chunkMap.getOrPut(toXZ(x, z)) {
            val chunk = Chunk(
                x,
                z,
                dimensionType.height,
                dimensionType.minY,
                cachedInt
            )
            // TODO Load save
            worldgen.genChunk(x, z, chunk)
            worldgen.updateHeightMap(chunk)
        }
    }

    fun unloadChunk(position: BlockPosition) {
        // TODO Save save
        chunkMap.remove(toXZ(position.x, position.z))
    }

    internal fun updateBlockState(position: BlockPosition, state: BlockState) {
        val chunk = getChunk(position.x shr 4, position.z shr 4)
        EventRelater.broadcastEvent("BLOCK_UPDATE", Pair(position, state))
        chunk.setBlockStateIDByChunkPosition(
            position.x and 15,
            position.y,
            position.z and 15,
            idByBlockState[state] ?: throw IllegalArgumentException("BlockState $state not found")
        )
    }

    internal fun updateBlockState(position: BlockPosition, state: Int) {
        val chunk = getChunk(position.x shr 4, position.z shr 4)
        EventRelater.broadcastEvent("BLOCK_UPDATE", Pair(position, state))
        chunk.setBlockStateIDByChunkPosition(
            position.x and 15,
            position.y,
            position.z and 15,
            state
        )
    }

    fun getBlockState(position: BlockPosition): BlockState {
        val chunk = getChunk(position.x shr 4, position.z shr 4)
        return blockStatesByProtocolId[chunk.getBlockStateIDByChunkPosition(
            position.x and 15,
            position.y,
            position.z and 15
        )] ?: throw IllegalArgumentException("BlockState not found")
    }

    abstract fun getProtocolId(): Int
}