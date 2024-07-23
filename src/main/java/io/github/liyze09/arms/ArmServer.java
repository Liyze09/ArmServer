package io.github.liyze09.arms;

import io.github.liyze09.arms.network.MinecraftProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.socket.transport.AioQuickServer;

import java.io.IOException;

public final class ArmServer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ArmServer");
    public static final AioQuickServer server = new AioQuickServer(Configuration.getInstance().port,
            new MinecraftProtocol(),
            (session, packet) -> {

            });

    public static void main(String[] args) throws IOException {
        server.start();
    }
}
