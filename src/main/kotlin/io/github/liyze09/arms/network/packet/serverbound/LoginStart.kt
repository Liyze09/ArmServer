package io.github.liyze09.arms.network.packet.serverbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils
import io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder
import io.github.liyze09.arms.network.packet.clientbound.LoginSuccess
import io.github.liyze09.arms.network.packet.clientbound.LoginSuccess.LoginSuccessBody
import io.netty.buffer.ByteBuf

class LoginStart : ServerBoundPacketDecoder {
    override fun decode(buf: ByteBuf, connection: Connection) {
        val username = PackUtils.readString(buf)
        val uuid = Connection.UUID(buf.readLong(), buf.readLong())
        connection.name = username
        connection.uUID = uuid
        PackUtils.sendPacket(
            connection,
            LoginSuccessBody(uuid, username),
            LoginSuccess
        )
    }
}
