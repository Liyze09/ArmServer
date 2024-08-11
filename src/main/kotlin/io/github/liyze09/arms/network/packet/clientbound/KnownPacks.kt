package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.ArmServer.MINECRAFT_VERSION
import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils.writeString
import io.github.liyze09.arms.network.PackUtils.writeVarInt
import io.github.liyze09.arms.network.packet.Packet

object KnownPacks : io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder<Any?> {
    override fun encode(msg: Any?, connection: Connection): Packet {
        val buf = connection.ctx.alloc().buffer()
        buf.writeVarInt(1)
        buf.writeString("minecraft")
        buf.writeString("core")
        buf.writeString(MINECRAFT_VERSION)
        return Packet.of(0x0E, buf)
    }
}