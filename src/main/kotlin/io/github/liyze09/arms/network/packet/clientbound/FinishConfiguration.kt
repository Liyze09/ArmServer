package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.packet.Packet
import io.netty.buffer.EmptyByteBuf

object FinishConfiguration : io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder<Any?> {
    override fun encode(msg: Any?, connection: Connection): Packet {
        return Packet.of(0x03, EmptyByteBuf(connection.ctx.alloc()))
    }
}