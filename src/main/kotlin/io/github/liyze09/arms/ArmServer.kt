package io.github.liyze09.arms

import io.github.liyze09.arms.network.NettyInitialize
import io.github.liyze09.arms.network.NettyInitialize.start
import io.github.liyze09.arms.network.packet.PacketCodecManager
import io.netty.channel.Channel
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ArmServer {
    val LOGGER: Logger = LoggerFactory.getLogger("ArmServer")
    private lateinit var arguments: Array<String>
    private lateinit var channel: Channel

    @JvmStatic
    fun main(args: Array<String>) {
        arguments = args
        PacketCodecManager.registerPackets()
        Thread.ofPlatform().name("Network-0").start {
            try {
                channel = start().channel()
                channel.closeFuture().sync()
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            } finally {
                NettyInitialize.bossGroup.shutdownGracefully()
                NettyInitialize.workerGroup.shutdownGracefully()
                LOGGER.info("Server stopped")
            }
        }
        LOGGER.info("Server started on port {}", Configuration.instance.port)
    }

    val launchArguments: Array<String>
        get() = arguments.clone()

    fun shutdown() {
        channel.close()
        LOGGER.info("Stopping!")
    }
}
