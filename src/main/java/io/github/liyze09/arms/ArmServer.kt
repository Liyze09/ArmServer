package io.github.liyze09.arms;

import io.github.liyze09.arms.network.NettyInitialize;
import io.github.liyze09.arms.network.packet.PacketCodecManager;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.liyze09.arms.network.NettyInitialize.start;

public final class ArmServer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ArmServer");
    private static String[] arguments;
    private static Channel channel;
    public static void main(String[] args) {
        arguments = args;
        PacketCodecManager.getInstance().registerPackets();
        Thread.ofPlatform().name("Network-0").start(()->{
            try {
                channel = start().channel();
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                NettyInitialize.bossGroup.shutdownGracefully();
                NettyInitialize.workerGroup.shutdownGracefully();
                LOGGER.info("Server stopped");
            }
        });
        LOGGER.info("Server started on port {}", Configuration.getInstance().port);
    }

    public static void stop() {
        channel.close();
    }

    public static String[] getLaunchArguments() {
        return arguments.clone();
    }

    public static void shutdown() {
        LOGGER.info("Stopping!");
    }
}
