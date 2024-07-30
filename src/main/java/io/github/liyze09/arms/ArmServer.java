package io.github.liyze09.arms;

import io.github.liyze09.arms.network.packet.PacketCodecManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static io.github.liyze09.arms.network.NetworkHandler.server;

public final class ArmServer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ArmServer");
    public static void main(String[] args) throws IOException {
        PacketCodecManager.getInstance().registerPackets();
        server.start();
    }
}
