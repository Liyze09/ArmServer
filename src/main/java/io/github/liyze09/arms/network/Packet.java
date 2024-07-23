package io.github.liyze09.arms.network;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

import static io.github.liyze09.arms.network.VarInt.readVarInt;
import static io.github.liyze09.arms.util.BytesUtil.readBytes;

public record Packet(int length, int id, byte[] data) {
    public static @NotNull Packet parsePacket(ByteBuffer input) {
        int length = readVarInt(input);
        var data = readBytes(input, length);
        var packID = readVarInt(ByteBuffer.wrap(data));
        return new Packet(length, packID, data);
    }
}