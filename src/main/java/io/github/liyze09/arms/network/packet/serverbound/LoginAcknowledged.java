package io.github.liyze09.arms.network.packet.serverbound;

import io.github.liyze09.arms.network.Connection;
import io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public class LoginAcknowledged implements ServerBoundPacketDecoder {
    @Override
    public void decode(ByteBuf buf, @NotNull Connection connection) {
        connection.updateStatus(Connection.Status.CONFIGURATION);
    }
}
