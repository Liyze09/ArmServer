package io.github.liyze09.arms.network;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class VarInt {
    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    public static int readVarInt(ByteBuffer input)  {
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

    public static void writeVarInt(int value, @NotNull ByteBuffer output) {
        while (true) {
            if ((value & ~SEGMENT_BITS) == 0) {
                output.put((byte) value);
                return;
            }

            output.put((byte) ((value & SEGMENT_BITS) | CONTINUE_BIT));

            value >>>= 7;
        }
    }

    public static class VarIntTooBigException extends RuntimeException {
    }
}
