package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils.writeString
import io.github.liyze09.arms.network.PackUtils.writeVarInt
import io.github.liyze09.arms.network.packet.Packet

object FeatureFlags : io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder<Any?> {
    override fun encode(msg: Any?, connection: Connection): Packet {
        val buf = connection.ctx.alloc().buffer()
        buf.writeVarInt(1)
        buf.writeString("minecraft:vanilla")
        return Packet.of(0x0C, buf)
    }
}