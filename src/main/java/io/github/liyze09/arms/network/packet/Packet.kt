package io.github.liyze09.arms.network.packet;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static io.github.liyze09.arms.network.PackUtils.*;

public record Packet(int length, int id, ByteBuf data) {
    @Contract("_, _ -> new")
    public static @NotNull Packet of(int id, ByteBuf data) {
        return new Packet(data.readableBytes() + getVarIntLength(id), id, data);
    }
}
