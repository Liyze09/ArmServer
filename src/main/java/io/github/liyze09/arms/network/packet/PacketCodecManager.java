package io.github.liyze09.arms.network.packet;

import io.github.liyze09.arms.network.packet.serverbound.LoginAcknowledged;
import io.github.liyze09.arms.network.packet.serverbound.LoginStart;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PacketCodecManager {
    private static final PacketCodecManager INSTANCE = new PacketCodecManager();

    public static PacketCodecManager getInstance() {
        return INSTANCE;
    }
    public void registerServerBoundPacket(PackType type, int id, ServerBoundPacketDecoder decoder) {
        getServerMap(type).put(id, decoder);
    }


    public ServerBoundPacketDecoder getServerDecoder(PackType type, int id) {
        return getServerMap(type).get(id);
    }
    private final Map<Integer, ServerBoundPacketDecoder> serverLogin= new ConcurrentHashMap<>();
    private final Map<Integer, ServerBoundPacketDecoder> serverConfiguration= new ConcurrentHashMap<>();
    private final Map<Integer, ServerBoundPacketDecoder> serverPlay= new ConcurrentHashMap<>();
    private final Map<Integer, ServerBoundPacketDecoder> serverStatus= new ConcurrentHashMap<>();

    @Contract(pure = true)
    private Map<Integer, ServerBoundPacketDecoder> getServerMap(@NotNull PackType type) {
        return switch (type) {
            case SERVER_LOGIN -> serverLogin;
            case SERVER_CONFIGURATION -> serverConfiguration;
            case SERVER_PLAY -> serverPlay;
            case SERVER_STATUS -> serverStatus;
        };
    }
    public enum PackType {
        SERVER_LOGIN,
        SERVER_CONFIGURATION,
        SERVER_PLAY,
        SERVER_STATUS
    }

    public void registerPackets() {
        registerServerBoundPacket(PackType.SERVER_LOGIN, 0, new LoginStart());
        registerServerBoundPacket(PackType.SERVER_LOGIN, 0x03, new LoginAcknowledged());
    }
}
