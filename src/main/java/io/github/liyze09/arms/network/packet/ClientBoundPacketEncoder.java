package io.github.liyze09.arms.network.packet;

import io.github.liyze09.arms.network.Connection;
import io.github.liyze09.arms.network.MinecraftProtocol;

import java.io.IOException;

@FunctionalInterface
public interface ClientBoundPacketEncoder<T> {
    MinecraftProtocol.Packet encode(T msg, Connection connection) throws IOException;
}
