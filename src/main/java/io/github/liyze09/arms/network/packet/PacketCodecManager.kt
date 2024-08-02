package io.github.liyze09.arms.network.packet;

import io.github.liyze09.arms.network.Connection;
import io.github.liyze09.arms.network.exception.IllegalPacketException;
import io.github.liyze09.arms.network.packet.serverbound.ClientInformation;
import io.github.liyze09.arms.network.packet.serverbound.LoginAcknowledged;
import io.github.liyze09.arms.network.packet.serverbound.LoginStart;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.liyze09.arms.network.Connection.Status.*;
import static io.github.liyze09.arms.network.NettyInitialize.MIN_PROTOCOL_VERSION;
import static io.github.liyze09.arms.network.PackUtils.*;

public final class PacketCodecManager {
    private static final PacketCodecManager INSTANCE = new PacketCodecManager();

    public static PacketCodecManager getInstance() {
        return INSTANCE;
    }
    public void registerServerBoundPacket(Connection.Status type, int id, ServerBoundPacketDecoder decoder) {
        getServerMap(type).put(id, decoder);
    }


    public ServerBoundPacketDecoder getServerDecoder(@NotNull Connection.Status type, int id) {
        if (type == HANDSHAKE) {
            return handshakeHandler;
        }
        return getServerMap(type).get(id);
    }
    private final Map<Integer, ServerBoundPacketDecoder> serverLogin= new ConcurrentHashMap<>();
    private final Map<Integer, ServerBoundPacketDecoder> serverConfiguration= new ConcurrentHashMap<>();
    private final Map<Integer, ServerBoundPacketDecoder> serverPlay= new ConcurrentHashMap<>();
    private final Map<Integer, ServerBoundPacketDecoder> serverStatus= new ConcurrentHashMap<>();

    @Contract(pure = true)
    private Map<Integer, ServerBoundPacketDecoder> getServerMap(@NotNull Connection.Status type) {
        return switch (type) {
            case LOGIN -> serverLogin;
            case CONFIGURATION -> serverConfiguration;
            case PLAY -> serverPlay;
            case STATUS -> serverStatus;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public void registerPackets() {
        registerServerBoundPacket(LOGIN, 0x00, new LoginStart());
        registerServerBoundPacket(LOGIN, 0x03, new LoginAcknowledged());
        registerServerBoundPacket(CONFIGURATION, 0x00, new ClientInformation());
    }

    public static final ServerBoundPacketDecoder handshakeHandler = (buf, connection) -> {
        var protocolVersion = readVarInt(buf);
        if (!checkProtocolVersion(protocolVersion, MIN_PROTOCOL_VERSION)) {
            throw new IllegalPacketException("Invalid protocol version: " + protocolVersion);
        }
        connection.setProtocolVersion(protocolVersion); // Protocol Version
        readString(buf); // Server Address (unused)
        buf.readShort(); // Server Port (unused)
        switch (readVarInt(buf)) {
            case 2 -> connection.updateStatus(Connection.Status.LOGIN);
            case 1 -> connection.updateStatus(Connection.Status.STATUS);
            default -> throw new IllegalPacketException("Invalid next state: " + readVarInt(buf));
        } // Next State
    };
}
