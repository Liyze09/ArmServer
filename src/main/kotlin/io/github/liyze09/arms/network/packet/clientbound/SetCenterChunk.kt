package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils.writeVarInt
import io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder
import io.github.liyze09.arms.network.packet.Packet

object SetCenterChunk : ClientBoundPacketEncoder<Pair<Int, Int>> {
    override fun encode(msg: Pair<Int, Int>, connection: Connection): Packet {
        val buf = connection.ctx.alloc().buffer()
        buf.writeVarInt(msg.first)
        buf.writeVarInt(msg.second)
        return Packet.of(0x54, buf)
    }
}