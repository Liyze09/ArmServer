package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder
import io.github.liyze09.arms.network.packet.Packet

object EntityEvent : ClientBoundPacketEncoder<Pair<Int, Int>> {
    override fun encode(msg: Pair<Int, Int>, connection: Connection): Packet {
        val buf = connection.ctx.alloc().buffer()
        buf.writeInt(msg.first)
            .writeByte(msg.second)
        return Packet.of(0x1F, buf)
    }
}