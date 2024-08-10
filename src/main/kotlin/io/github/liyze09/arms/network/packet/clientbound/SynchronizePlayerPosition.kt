package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.common.TeleportBody
import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils.writeVarInt
import io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder
import io.github.liyze09.arms.network.packet.Packet

object SynchronizePlayerPosition : ClientBoundPacketEncoder<TeleportBody> {
    override fun encode(msg: TeleportBody, connection: Connection): Packet {
        connection.teleporting = msg.id
        val buf = connection.ctx.alloc().ioBuffer()
        buf.writeDouble(msg.x)
            .writeDouble(msg.y)
            .writeDouble(msg.z)
            .writeFloat(msg.yaw)
            .writeFloat(msg.pitch)
            .writeByte(0) // flags
            .writeVarInt(msg.id)
        return Packet.of(0x40, buf)
    }
}