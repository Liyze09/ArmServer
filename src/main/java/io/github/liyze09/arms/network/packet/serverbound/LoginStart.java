package io.github.liyze09.arms.network.packet.serverbound;

import io.github.liyze09.arms.network.Connection;
import io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder;
import io.github.liyze09.arms.network.packet.clientbound.LoginSuccess;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import static io.github.liyze09.arms.network.PackUtils.*;

public class LoginStart implements ServerBoundPacketDecoder {
    @Override
    public void decode(ByteBuf buf, @NotNull Connection connection) {
        var username = readString(buf);
        var uuid = new Connection.UUID(buf.readLong(), buf.readLong());
        connection.setName(username);
        connection.setUUID(uuid);
        sendPacket(connection,
                new LoginSuccess.LoginSuccessBody(uuid, username),
                LoginSuccess.getInstance()
        );
    }
}
