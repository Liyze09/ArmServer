package io.github.liyze09.arms.network.packet.serverbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils.readString
import io.github.liyze09.arms.network.packet.clientbound.LoginSuccess
import io.github.liyze09.arms.network.packet.clientbound.LoginSuccessBody
import io.github.liyze09.arms.network.packet.clientbound.SetCompression
import io.netty.buffer.ByteBuf
import net.minecraftarm.common.UUID

object LoginStart : io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder {
    override fun decode(buf: ByteBuf, connection: Connection) {
        val username = buf.readString(16)
        val uuid = UUID(buf.readLong(), buf.readLong())
        connection.setUsername(username)
        connection.setUUID(uuid)
        connection.sendPacket(
            null,
            SetCompression
        ).sendPacket(
            LoginSuccessBody(uuid, username),
            LoginSuccess
        )
    }
}
