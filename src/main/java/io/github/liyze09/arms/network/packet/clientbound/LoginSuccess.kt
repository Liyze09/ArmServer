package io.github.liyze09.arms.network.packet.clientbound;

import io.github.liyze09.arms.network.Connection;
import io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder;
import io.github.liyze09.arms.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

import static io.github.liyze09.arms.network.PackUtils.*;

public class LoginSuccess implements ClientBoundPacketEncoder<LoginSuccess.LoginSuccessBody> {
    private static final LoginSuccess INSTANCE = new LoginSuccess();
    public static LoginSuccess getInstance() {
        return INSTANCE;
    }
    @Override
    public Packet encode(@NotNull LoginSuccessBody msg, @NotNull Connection connection) {
        var buffer = connection.ctx.alloc().buffer();
        // UUID
        buffer.writeLong(msg.uuid.a());
        buffer.writeLong(msg.uuid.b());
        // Username
        var name = msg.username.getBytes();
        writeVarInt(name.length, buffer);
        buffer.writeBytes(name);

        writeVarInt(0, buffer);
        if (checkProtocolVersion(connection.getProtocolVersion(), 766)) {
            buffer.writeByte((byte) 0);
        }

        return Packet.of(0x02, buffer);
    }
    public record LoginSuccessBody (
        Connection.UUID uuid,
        String username
    ){
    }
}
