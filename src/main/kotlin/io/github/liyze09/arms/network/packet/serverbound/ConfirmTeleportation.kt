package io.github.liyze09.arms.network.packet.serverbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils.readVarInt
import io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder
import io.netty.buffer.ByteBuf

object ConfirmTeleportation : ServerBoundPacketDecoder {
    override fun decode(buf: ByteBuf, connection: Connection) {
        if (buf.readVarInt() == connection.teleporting) {
            connection.teleporting = -1
        }
        println("1")
    }
}