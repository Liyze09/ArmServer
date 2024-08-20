package net.minecraftarm.world

import io.github.liyze09.arms.GlobalConfiguration
import net.minecraftarm.common.BlockPosition
import net.minecraftarm.common.Identifier
import net.minecraftarm.common.toByteArray
import net.minecraftarm.common.toLong
import net.minecraftarm.registry.block.BlockAction
import net.minecraftarm.world.impl.Overworld
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

object World {
    var mspt = 0
    val dimensions = mutableMapOf<Identifier, Dimension>()
    val seed = GlobalConfiguration.instance.seed
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
        val time0 = System.currentTimeMillis()
        val tasks = LinkedList<BlockUpdateTask>()
        tickThreadPool.submit {
            val size = plannedBlockUpdates.size
            repeat(size) {
                val update = plannedBlockUpdates.remove()
                if (update.first <= 1.toShort()) {
                    applyBlockUpdate(update.second)
                } else {
                    plannedBlockUpdates.add(Pair((update.first - 1).toShort(), update.second))
                }
            }
        }
        val count = AtomicInteger(getBlockUpdateQueueSize())
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
                    count.incrementAndGet()
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
                        postPlacement(blockUpdate)
                    }

                    BlockAction.PLACE -> {
                        tasks.add(
                            BlockUpdateTask(
                                { blockUpdate.dimension.updateBlockState(blockUpdate.position, blockUpdate.state) },
                                listOf(blockUpdate.position)
                            )
                        )
                        postPlacement(blockUpdate)
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
        val latch1 = CountDownLatch(tasks.size)
        out@ while (tasks.isNotEmpty()) {
            tickThreadPool.submit {
                val task = tasks.remove()
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
        mspt = (System.currentTimeMillis() - time0).toInt()
    }

    private val blockUpdates: Queue<BlockUpdate> = ConcurrentLinkedQueue()
    private val blockUpdates2: Queue<BlockUpdate> = ConcurrentLinkedQueue()
    private val plannedBlockUpdates: Queue<Pair<Short, BlockUpdate>> = ConcurrentLinkedQueue()

    @Volatile
    private var useQueue2 = false

    private fun exchangeBlockUpdatesQueue() {
        useQueue2 = !useQueue2
    }

    private fun postPlacement(blockUpdate: BlockUpdate) {
        BlockPosition(blockUpdate.position.x + 1, blockUpdate.position.y, blockUpdate.position.z).let {
            applyInnerBlockUpdate(
                BlockUpdate(
                    it,
                    blockUpdate.dimension.getBlockState(it),
                    BlockAction.POST_PLACEMENT,
                    blockUpdate.dimension
                )
            )
        }
        BlockPosition(blockUpdate.position.x - 1, blockUpdate.position.y, blockUpdate.position.z).let {
            applyInnerBlockUpdate(
                BlockUpdate(
                    it,
                    blockUpdate.dimension.getBlockState(it),
                    BlockAction.POST_PLACEMENT,
                    blockUpdate.dimension
                )
            )
        }
        BlockPosition(blockUpdate.position.x, blockUpdate.position.y + 1, blockUpdate.position.z).let {
            applyInnerBlockUpdate(
                BlockUpdate(
                    it,
                    blockUpdate.dimension.getBlockState(it),
                    BlockAction.POST_PLACEMENT,
                    blockUpdate.dimension
                )
            )
        }
        BlockPosition(blockUpdate.position.x, blockUpdate.position.y - 1, blockUpdate.position.z).let {
            applyInnerBlockUpdate(
                BlockUpdate(
                    it,
                    blockUpdate.dimension.getBlockState(it),
                    BlockAction.POST_PLACEMENT,
                    blockUpdate.dimension
                )
            )
        }
        BlockPosition(blockUpdate.position.x, blockUpdate.position.y, blockUpdate.position.z + 1).let {
            applyInnerBlockUpdate(
                BlockUpdate(
                    it,
                    blockUpdate.dimension.getBlockState(it),
                    BlockAction.POST_PLACEMENT,
                    blockUpdate.dimension
                )
            )
        }
        BlockPosition(blockUpdate.position.x, blockUpdate.position.y, blockUpdate.position.z - 1).let {
            applyInnerBlockUpdate(
                BlockUpdate(
                    it,
                    blockUpdate.dimension.getBlockState(it),
                    BlockAction.POST_PLACEMENT,
                    blockUpdate.dimension
                )
            )
        }
    }

    fun scheduleBlockUpdate(blockUpdate: BlockUpdate, delayTick: Int) {
        if (delayTick <= 1) {
            applyBlockUpdate(blockUpdate)
        } else {
            plannedBlockUpdates.add(Pair(delayTick.toShort(), blockUpdate))
        }
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

    fun getDimension(id: Identifier) = dimensions[id] ?: throw IllegalArgumentException("Dimension $id not found")

    fun getWorldSpawnPoint(): BlockPosition {
        // TODO
        return BlockPosition(0, 256, 0)
    }


}