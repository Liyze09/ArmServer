package io.github.liyze09.arms;

import io.github.liyze09.arms.network.packet.PacketCodecManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static io.github.liyze09.arms.network.NetworkHandler.server;

public final class ArmServer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ArmServer");
    private static String[] arguments;
    public static void main(String[] args) throws IOException {
        arguments = args;
        PacketCodecManager.getInstance().registerPackets();
        server.start();
        LOGGER.info("Server started on port {}", Configuration.getInstance().port);
    }

    public static String[] getLaunchArguments() {
        return arguments.clone();
    }

    public static void shutdown() {
        server.shutdown();
        LOGGER.info("Stopping!");
        System.exit(0);
    }
}
