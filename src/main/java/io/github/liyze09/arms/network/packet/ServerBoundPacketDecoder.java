package io.github.liyze09.arms.network.packet;

import io.github.liyze09.arms.network.Connection;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface ServerBoundPacketDecoder {
    void decode(ByteBuffer buf, Connection connection);
}
