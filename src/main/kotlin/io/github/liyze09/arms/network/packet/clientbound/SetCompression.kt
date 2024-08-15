package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.GlobalConfiguration
import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils.writeVarInt
import io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder
import io.github.liyze09.arms.network.packet.Packet

object SetCompression : ClientBoundPacketEncoder<Any?> {
    override fun encode(msg: Any?, connection: Connection): Packet {
        val buf = connection.ctx.alloc().buffer()
        buf.writeVarInt(GlobalConfiguration.instance.compressThreshold)
        return Packet.of(0x03, buf)
    }
}