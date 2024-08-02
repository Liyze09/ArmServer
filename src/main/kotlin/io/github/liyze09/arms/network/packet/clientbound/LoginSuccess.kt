package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils
import io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder
import io.github.liyze09.arms.network.packet.Packet
import io.github.liyze09.arms.network.packet.clientbound.LoginSuccess.LoginSuccessBody

object LoginSuccess : ClientBoundPacketEncoder<LoginSuccessBody> {
    override fun encode(msg: LoginSuccessBody, connection: Connection): Packet {
        val buffer = connection.ctx.alloc().buffer()
        // UUID
        buffer.writeLong(msg.uuid.a)
        buffer.writeLong(msg.uuid.b)
        // Username
        val name = msg.username.toByteArray()
        PackUtils.writeVarInt(name.size, buffer)
        buffer.writeBytes(name)

        PackUtils.writeVarInt(0, buffer)
        if (PackUtils.checkProtocolVersion(connection.protocolVersion, 766)) {
            buffer.writeByte(0.toByte().toInt())
        }

        return Packet.of(0x02, buffer)
    }

    @JvmRecord
    data class LoginSuccessBody(
        val uuid: Connection.UUID,
        val username: String
    )
}
