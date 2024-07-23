package io.github.liyze09.arms.util;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public final class BytesUtil {
    public static byte@NotNull[] readBytes(ByteBuffer input, int length) {
        var data = new byte[length];
        for (int i = 0; i < length; i++) {
            data[i] = (input.get());
        }
        return data;
    }
}
