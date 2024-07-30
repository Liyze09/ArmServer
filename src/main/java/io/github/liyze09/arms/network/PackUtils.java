package io.github.liyze09.arms.network;

import io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.smartboot.socket.transport.WriteBuffer;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class PackUtils {
    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;
    public static boolean checkProtocolVersion(int protocolVersion, int minVersion) {
        return protocolVersion >= minVersion && protocolVersion < 0x40000000;
    }

    @Contract("_, _ -> new")
    public static MinecraftProtocol.@NotNull Packet generatePacket(int id, byte @NotNull [] data) {
        return new MinecraftProtocol.Packet(data.length+getVarIntLength(id), id, data);
    }

    public static int getVarIntLength(int varInt) {
        int length = 0;
        while ((varInt & 0xFFFFFF80) != 0) {
            varInt >>>= 7;
            length++;
        }
        return length + 1;
    }
    public static <T> void sendPacket(Connection connection, T msg, @NotNull ClientBoundPacketEncoder<T> encoder) {
        try {
            var packet = encoder.encode(msg, connection);
            var buffer = connection.session.writeBuffer();
            writeVarInt(packet.length(), buffer);
            writeVarInt(packet.id(), buffer);
            buffer.write(packet.data());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeString(@NotNull String msg, WriteBuffer buffer) throws IOException {
        var bytes = msg.getBytes(StandardCharsets.UTF_8);
        writeVarInt(bytes.length, buffer);
        buffer.write(bytes);
    }

    @Contract("_ -> new")
    public static @NotNull String readString(ByteBuffer input) {
        int length = readVarInt(input);

        byte[] data = new byte[length];
        input.get(data);

        return new String(data, StandardCharsets.UTF_8);
    }
    public static int readVarInt(@NotNull ByteBuffer input)  {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = input.get();
            value |= (currentByte & SEGMENT_BITS) << position;

            if ((currentByte & CONTINUE_BIT) == 0) break;

            position += 7;

            if (position >= 32) throw new VarIntTooBigException();
        }

        return value;
    }

    public static void writeVarInt(int value, @NotNull OutputStream out) throws IOException {
        var output = new DataOutputStream(out);
        while (true) {
            if ((value & ~SEGMENT_BITS) == 0) {
                output.writeByte((byte) value);
                return;
            }

            output.writeByte((byte) ((value & SEGMENT_BITS) | CONTINUE_BIT));

            value >>>= 7;
        }
    }

    public static class VarIntTooBigException extends RuntimeException {
        public VarIntTooBigException() {
            super("VarInt is too big or invalid");
        }
    }
}
