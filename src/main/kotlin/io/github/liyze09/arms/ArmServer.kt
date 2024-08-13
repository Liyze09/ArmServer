package io.github.liyze09.arms

import io.github.liyze09.arms.network.NettyInitialize
import io.github.liyze09.arms.network.NettyInitialize.start
import io.github.liyze09.arms.network.packet.PacketCodecManager.registerPackets
import io.netty.channel.Channel
import net.minecraftarm.registry.Registries.vanillaRegistries
import net.minecraftarm.registry.block.blockRegistryInit
import net.minecraftarm.registry.entityRegistryInit
import net.minecraftarm.registry.item.itemRegistriesInit
import net.minecraftarm.world.World
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

object ArmServer {
    private val LOGGER: Logger = LoggerFactory.getLogger("ArmServer")
    const val MINECRAFT_VERSION = "1.21"
    private lateinit var arguments: Array<String>
    private lateinit var channel: Channel

    @JvmStatic
    fun main(args: Array<String>) {
        arguments = args
        registerPackets()
        vanillaRegistries()
        Thread.ofVirtual().name("Command").start {
            val scanner = Scanner(System.`in`)
            while (true) {
                val input = scanner.nextLine()
                // TODO: Command 24/08/03
                if (input.startsWith("stop", true)) {
                    shutdown()
                }
            }
        }
        blockRegistryInit()
        itemRegistriesInit()
        entityRegistryInit()
        Thread.ofPlatform().name("Network").start {
            try {
                channel = start().channel()
                LOGGER.info("Server started on port {}", Configuration.instance.port)
                channel.closeFuture().sync()
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            } finally {
                NettyInitialize.bossGroup.shutdownGracefully()
                NettyInitialize.workerGroup.shutdownGracefully()
                LOGGER.info("Server stopped")
            }
        }
    }

    val launchArguments: Array<String>
        get() = arguments.clone()

    fun shutdown() {
        LOGGER.info("Stopping!")
        channel.close()
        World.tickHandler.shutdown()
        World.tickThreadPool.shutdown()
    }
}
