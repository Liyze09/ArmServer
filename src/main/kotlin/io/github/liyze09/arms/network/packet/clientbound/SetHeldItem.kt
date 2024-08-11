package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.packet.Packet

object SetHeldItem : io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder<Int> {
    override fun encode(msg: Int, connection: Connection): Packet {
        val buf = connection.ctx.alloc().buffer()
        buf.writeByte(msg)
        return Packet.of(0x53, buf)
    }
}