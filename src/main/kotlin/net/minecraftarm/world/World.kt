package net.minecraftarm.world

import io.github.liyze09.arms.Configuration
import net.minecraftarm.common.BlockPosition
import net.minecraftarm.common.Identifier
import net.minecraftarm.common.toByteArray
import net.minecraftarm.common.toLong
import net.minecraftarm.registry.block.BlockAction
import net.minecraftarm.world.impl.Overworld
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.*

object World {
    val dimensions = mutableMapOf<Identifier, Dimension>()
    val seed = Configuration.instance.seed
    val hashedSeed: Long
    internal val tickHandler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    internal val tickThreadPool: ExecutorService =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    init {
        dimensions[Identifier("minecraft", "overworld")] = Overworld()
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(seed.toByteArray())
        hashedSeed = messageDigest.digest().take(8).toByteArray().toLong()
        tickHandler.scheduleAtFixedRate({
            tick()
        }, 20, 20, TimeUnit.MILLISECONDS)
    }

    fun tick() {
        exchangeBlockUpdatesQueue()
        val tasks = LinkedList<BlockUpdateTask>()
        val latch0 = CountDownLatch(getBlockUpdateQueueSize())
        while (true) {
            val blockUpdate = pollBlockUpdate() ?: break
            tickThreadPool.submit {
                val msg = blockUpdate.state.parent.beforeBlockActionApply(
                    blockUpdate.type,
                    blockUpdate.dimension,
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
                                { blockUpdate.dimension.updateBlockState(blockUpdate.position, 0) },
                                listOf(blockUpdate.position)
                            )
                        )
                    }

                    BlockAction.PLACE -> {
                        tasks.add(
                            BlockUpdateTask(
                                { blockUpdate.dimension.updateBlockState(blockUpdate.position, blockUpdate.state) },
                                listOf(blockUpdate.position)
                            )
                        )
                    }

                    else -> {}
                }
                latch0.countDown()
            }
        }
        latch0.await()
        val usingBlocks = ConcurrentLinkedQueue<BlockPosition>()
        val latch1 = CountDownLatch(tasks.size)
        out@ while (tasks.isNotEmpty()) {
            tickThreadPool.submit {
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
                latch1.countDown()
            }
        }
        latch1.await()
        exchangeBlockUpdatesQueue()
    }

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

    fun makeBlockUpdate(position: BlockPosition, dimension: Dimension, action: BlockAction) {
        blockUpdates.add(BlockUpdate(position, dimension.getBlockState(position), action, dimension))
    }

    fun getDimension(id: Identifier) = dimensions[id] ?: throw IllegalArgumentException("Dimension $id not found")

    fun getWorldSpawnPoint(): BlockPosition {
        // TODO
        return BlockPosition(0, 64, 0)
    }


}