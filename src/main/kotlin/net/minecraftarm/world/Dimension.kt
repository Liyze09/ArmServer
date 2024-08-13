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
import java.util.concurrent.atomic.AtomicInteger

abstract class Dimension(val worldgen: WorldgenProvider) {
    abstract val dimensionType: DimensionType
    abstract val name: Identifier
    private val chunkMap = ConcurrentHashMap<BlockPosition, Chunk>(512)
    val entities = ConcurrentHashMap<Int, Entity>(128)
    private val blockUpdates: Queue<BlockUpdate> = ConcurrentLinkedQueue()
    private val blockUpdates2: Queue<BlockUpdate> = ConcurrentLinkedQueue()
    @Volatile
    private var useQueue2 = false

    private fun exchangeBlockUpdatesQueue() {
        useQueue2 = !useQueue2
    }

    fun applyBlockUpdate(blockUpdate: BlockUpdate) {
        if (useQueue2) {
            blockUpdates2.add(blockUpdate)
        } else {
            blockUpdates.add(blockUpdate)
        }
    }

    private fun applyInnerBlockUpdate(blockUpdate: BlockUpdate) {
        if (useQueue2) {
            blockUpdates.add(blockUpdate)
        } else {
            blockUpdates2.add(blockUpdate)
        }
    }

    private fun pollBlockUpdate(): BlockUpdate? {
        return if (useQueue2) blockUpdates.poll()
        else blockUpdates2.poll()
    }

    private fun getBlockUpdateQueueSize(): Int {
        return if (useQueue2) blockUpdates.size
        else blockUpdates2.size
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
        val count = AtomicInteger(getBlockUpdateQueueSize())
        while (true) {
            val blockUpdate = pollBlockUpdate() ?: break
            World.tickThreadPool.submit {
                val msg = blockUpdate.state.parent.beforeBlockActionApply(
                    blockUpdate.type,
                    this,
                    blockUpdate.position,
                    blockUpdate.state
                )
                tasks.add(BlockUpdateTask({
                    blockUpdate.state.parent.duringBlockActionApply(blockUpdate.type, msg)
                }, msg.influenceBlocks))
                msg.secondaryUpdates.forEach {
                    applyInnerBlockUpdate(it)
                }
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
                count.decrementAndGet()
            }
        }
        while (count.get() != 0) {
            Thread.onSpinWait()
        }
        val usingBlocks = ConcurrentLinkedQueue<BlockPosition>()
        count.set(usingBlocks.size)
        out@ while (tasks.isNotEmpty()) {
            World.tickThreadPool.submit {
                val task = tasks.element()
                for (it in task.influenceBlocks) {
                    if (!usingBlocks.contains(it)) {
                        usingBlocks.add(it)
                    } else {
                        tasks.add(task)
                        return@submit
                    }
                }
                task.task.run()
                Thread.yield()
                for (it in task.influenceBlocks) {
                    usingBlocks.remove(it)
                }
                count.decrementAndGet()
            }
        }
        while (count.get() != 0) {
            Thread.onSpinWait()
        }
        exchangeBlockUpdatesQueue()
    }

    abstract fun getProtocolId(): Int
}