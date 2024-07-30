package io.github.liyze09.arms.network;

import io.github.liyze09.arms.Configuration;
import io.github.liyze09.arms.network.packet.PacketCodecManager;
import io.github.liyze09.arms.network.packet.PacketCodecManager.PackType;
import org.smartboot.socket.transport.AioQuickServer;

import java.nio.ByteBuffer;

public class NetworkHandler {
    public static final int MIN_PROTOCOL_VERSION = 757;
    public static final AioQuickServer server = new AioQuickServer(Configuration.getInstance().port,
            new MinecraftProtocol(),
            (session, packet) -> {
                var connection = Connection.getInstance(session);
                connection.lock();
                switch (connection.getStatus()) {
                    case LOGIN -> PacketCodecManager
                            .getInstance()
                            .getServerDecoder(PackType.SERVER_LOGIN, packet.id())
                            .decode(ByteBuffer.wrap(packet.data()), connection);
                    case PLAY -> PacketCodecManager
                            .getInstance()
                            .getServerDecoder(PackType.SERVER_PLAY, packet.id())
                            .decode(ByteBuffer.wrap(packet.data()), connection);
                    case CONFIGURATION -> PacketCodecManager
                            .getInstance()
                            .getServerDecoder(PackType.SERVER_CONFIGURATION, packet.id())
                            .decode(ByteBuffer.wrap(packet.data()), connection);
                    case STATUS -> PacketCodecManager
                            .getInstance()
                            .getServerDecoder(PackType.SERVER_STATUS, packet.id())
                            .decode(ByteBuffer.wrap(packet.data()), connection);
                }
                connection.unlock();
            });

}
