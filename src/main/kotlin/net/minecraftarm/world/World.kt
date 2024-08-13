package net.minecraftarm.world

import io.github.liyze09.arms.Configuration
import net.minecraftarm.common.BlockPosition
import net.minecraftarm.common.Identifier
import net.minecraftarm.common.toByteArray
import net.minecraftarm.common.toLong
import net.minecraftarm.world.impl.Overworld
import java.security.MessageDigest
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

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
            for (dimension in dimensions.values) {
                Thread.ofVirtual().start {
                    dimension.tick()
                }
            }
        }, 20, 20, TimeUnit.MILLISECONDS)
    }

    fun getDimension(id: Identifier) = dimensions[id] ?: throw IllegalArgumentException("Dimension $id not found")

    fun getWorldSpawnPoint(): BlockPosition {
        // TODO
        return BlockPosition(0, 64, 0)
    }


}