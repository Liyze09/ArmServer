package io.github.liyze09.arms.network.packet;

import io.github.liyze09.arms.network.Connection;

import java.io.IOException;

@FunctionalInterface
public interface ClientBoundPacketEncoder<T> {
    Packet encode(T msg, Connection connection) throws IOException;
}
