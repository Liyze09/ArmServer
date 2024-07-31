package io.github.liyze09.arms.network.packet.clientbound;

import io.github.liyze09.arms.network.Connection;
import io.github.liyze09.arms.network.MinecraftProtocol;
import io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static io.github.liyze09.arms.network.PackUtils.*;

public class LoginSuccess implements ClientBoundPacketEncoder<LoginSuccess.LoginSuccessBody> {
    private static final LoginSuccess INSTANCE = new LoginSuccess();
    public static LoginSuccess getInstance() {
        return INSTANCE;
    }
    @Override
    public MinecraftProtocol.Packet encode(@NotNull LoginSuccessBody msg, @NotNull Connection connection) throws IOException {
        var out = new ByteArrayOutputStream();
        var buffer = new DataOutputStream(out);
        // UUID
        buffer.writeLong(msg.uuid.a());
        buffer.writeLong(msg.uuid.b());
        // Username
        var name = msg.username.getBytes();
        writeVarInt(name.length, buffer);
        buffer.write(name);

        writeVarInt(0, buffer);
        if (checkProtocolVersion(connection.getProtocolVersion(), 766)) {
            buffer.writeByte((byte) 0);
        }

        var bytes = out.toByteArray();
        return MinecraftProtocol.Packet.of(0x02, bytes);
    }
    public record LoginSuccessBody (
        Connection.UUID uuid,
        String username
    ){
    }
}
