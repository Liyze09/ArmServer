package net.minecraftarm.world

import net.minecraftarm.api.event.EventRelater
import net.minecraftarm.common.BlockPosition
import net.minecraftarm.common.Identifier
import net.minecraftarm.entity.Entity
import net.minecraftarm.registry.DimensionType
import net.minecraftarm.registry.block.BlockAction
import net.minecraftarm.registry.block.BlockState
import net.minecraftarm.registry.block.blockStatesByProtocolId
import net.minecraftarm.registry.block.idByBlockState
import net.minecraftarm.world.gen.WorldgenProvider
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

abstract class Dimension(val worldgen: WorldgenProvider) {
    abstract val dimensionType: DimensionType
    abstract val name: Identifier
    private val chunkMap = ConcurrentHashMap<BlockPosition, Chunk>(512)
    val entities = ConcurrentHashMap<Int, Entity>(128)
    private val blockUpdates: Queue<BlockUpdate> = ConcurrentLinkedQueue()
    private val blockUpdates2: Queue<BlockUpdate> = ConcurrentLinkedQueue()
    val innerBlockUpdates: Queue<BlockUpdate> = ConcurrentLinkedQueue()

    @Volatile
    private var useQueue2 = false

    private fun exchangeBlockUpdatesQueue() {
        useQueue2 = !useQueue2
    }

    private fun applyBlockUpdate(blockUpdate: BlockUpdate) {
        if (useQueue2) {
            blockUpdates2.add(blockUpdate)
        } else {
            blockUpdates.add(blockUpdate)
        }
    }

    private fun pollBlockUpdate(): BlockUpdate? {
        return if (useQueue2) blockUpdates.poll()
        else blockUpdates2.poll()
    }

    fun getChunk(position: BlockPosition): Chunk {
        return chunkMap.getOrPut(position) {
            val chunk = Chunk(dimensionType.height, dimensionType.minY)
            // TODO Load save
            worldgen.genChunk(position.x, position.z, chunk)
        }
    }

    fun makeBlockUpdate(position: BlockPosition, action: BlockAction) {
        blockUpdates.add(BlockUpdate(position, getBlockState(position), action))
    }

    private fun updateBlockState(position: BlockPosition, state: BlockState) {
        val chunk = getChunk(BlockPosition(position.x shr 4, 0, position.z shr 4))
        EventRelater.broadcastEvent("BLOCK_UPDATE", Pair(position, state))
        chunk.setBlockStateIDByChunkPosition(
            position.x and 15,
            position.y,
            position.z and 15,
            idByBlockState[state] ?: throw IllegalArgumentException("BlockState $state not found")
        )
    }

    private fun updateBlockState(position: BlockPosition, state: Int) {
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

    fun tick() {
        exchangeBlockUpdatesQueue()
        val tasks = LinkedList<BlockUpdateTask>()
        while (true) {
            val blockUpdate = pollBlockUpdate() ?: break
            val msg = blockUpdate.state.parent.beforeBlockActionApply(
                blockUpdate.type,
                this,
                blockUpdate.position,
                blockUpdate.state
            )
            tasks.add(BlockUpdateTask({
                blockUpdate.state.parent.duringBlockActionApply(blockUpdate.type, msg.msg)
            }, msg.influenceBlocks))
            when (blockUpdate.type) {
                BlockAction.BREAK -> {
                    tasks.add(
                        BlockUpdateTask(
                            { updateBlockState(blockUpdate.position, 0) },
                            listOf(blockUpdate.position)
                        )
                    )
                }

                BlockAction.PLACE -> {
                    tasks.add(
                        BlockUpdateTask(
                            { updateBlockState(blockUpdate.position, blockUpdate.state) },
                            listOf(blockUpdate.position)
                        )
                    )
                }

                else -> {}
            }
        }
        val occupationBlocks = mutableListOf<BlockPosition>()

        tasks.forEach {
            TODO()
        }
        exchangeBlockUpdatesQueue()
    }

    abstract fun getProtocolId(): Int
}