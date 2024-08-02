package io.github.liyze09.arms.network.packet;

import io.github.liyze09.arms.network.Connection;
import io.netty.buffer.ByteBuf;

@FunctionalInterface
public interface ServerBoundPacketDecoder {
    void decode(ByteBuf buf, Connection connection);
}
