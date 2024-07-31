package io.github.liyze09.arms.network.packet.serverbound;

import io.github.liyze09.arms.network.Connection;
import io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class LoginAcknowledged implements ServerBoundPacketDecoder {
    @Override
    public void decode(ByteBuffer buf, @NotNull Connection connection) {
        connection.updateStatus(Connection.Status.CONFIGURATION);
    }
}
