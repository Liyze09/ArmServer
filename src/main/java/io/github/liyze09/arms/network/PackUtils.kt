package io.github.liyze09.arms.network;

import io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class PackUtils {
    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;
    public static boolean checkProtocolVersion(int protocolVersion, int minVersion) {
        return protocolVersion >= minVersion && protocolVersion < 0x40000000;
    }



    public static int getVarIntLength(int varInt) {
        if (varInt < 0) {
            return 5;
        } else if (varInt <= 127) {
            return 1;
        } else if (varInt <= 16383) {
            return 2;
        } else if (varInt <= 2097151) {
            return 3;
        } else if (varInt <= 268435455) {
            return 4;
        } else {
            return 5;
        }
    }
    public static <T> void sendPacket(@NotNull Connection connection, T msg, @NotNull ClientBoundPacketEncoder<T> encoder) {
        try {
            connection.ctx.writeAndFlush(encoder.encode(msg, connection));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeString(@NotNull String msg, ByteBuf buffer) {
        var bytes = msg.getBytes(StandardCharsets.UTF_8);
        writeVarInt(bytes.length, buffer);
        buffer.writeBytes(bytes);
    }

    @Contract("_ -> new")
    public static @NotNull String readString(ByteBuf input) {
        int length = readVarInt(input);

        byte[] data = new byte[length];
        input.readBytes(data);

        return new String(data, StandardCharsets.UTF_8);
    }

    public static int readVarInt(@NotNull ByteBuf input)  {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = input.readByte();
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

    public static void writeVarInt(int value, @NotNull ByteBuf out) {
        while (true) {
            if ((value & ~SEGMENT_BITS) == 0) {
                out.writeByte((byte) value);
                return;
            }

            out.writeByte((byte) ((value & SEGMENT_BITS) | CONTINUE_BIT));

            value >>>= 7;
        }
    }

    public static class VarIntTooBigException extends RuntimeException {
        public VarIntTooBigException() {
            super("VarInt is too big or invalid");
        }
    }
}
