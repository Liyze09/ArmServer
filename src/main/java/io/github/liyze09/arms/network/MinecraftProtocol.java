package io.github.liyze09.arms.network;

import io.github.liyze09.arms.network.exception.IllegalPacketException;
import io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;

import static io.github.liyze09.arms.network.NetworkHandler.MIN_PROTOCOL_VERSION;
import static io.github.liyze09.arms.network.PackUtils.*;
import static io.github.liyze09.arms.util.BytesUtil.readBytes;

public class MinecraftProtocol implements Protocol<MinecraftProtocol.Packet> {
    @Override
    public Packet decode(ByteBuffer byteBuffer, AioSession aioSession) {
        var packet = Packet.parsePacket(byteBuffer);
        if (packet.id() != 0) {
            return packet;
        }
        var connection = Connection.getInstance(aioSession);
        if (connection.getStatus() != Connection.Status.HANDSHAKE) {
            return packet;
        }
        handshakeHandler.decode(ByteBuffer.wrap(packet.data()), connection);
        return Packet.parsePacket(byteBuffer);
    }

    public record Packet(int length, int id, byte[] data) {
        public static @NotNull MinecraftProtocol.Packet parsePacket(ByteBuffer input) {
            int length = readVarInt(input);
            var data = ByteBuffer.wrap(readBytes(input, length));
            var packID = readVarInt(data);
            data.compact();
            return new Packet(length, packID, data.array());
        }

        @Contract("_, _ -> new")
        public static MinecraftProtocol.@NotNull Packet of(int id, byte @NotNull [] data) {
            return new MinecraftProtocol.Packet(data.length + getVarIntLength(id), id, data);
        }
    }

    public static final ServerBoundPacketDecoder handshakeHandler = (buf, connection) -> {
        var protocolVersion = readVarInt(buf);
        if (!checkProtocolVersion(protocolVersion, MIN_PROTOCOL_VERSION)) {
            throw new IllegalPacketException("Invalid protocol version: " + protocolVersion);
        }
        connection.setProtocolVersion(protocolVersion); // Protocol Version
        readString(buf); // Server Address (unused)
        buf.getShort(); // Server Port (unused)
        switch (readVarInt(buf)) {
            case 2 -> connection.updateStatus(Connection.Status.LOGIN);
            case 1 -> connection.updateStatus(Connection.Status.STATUS);
            default -> throw new IllegalPacketException("Invalid next state: " + readVarInt(buf));
        } // Next State
    };
}
