package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.ArmServer.MINECRAFT_VERSION
import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils.writeString
import io.github.liyze09.arms.network.PackUtils.writeVarInt
import io.github.liyze09.arms.network.packet.Packet

object ClientBoundKnownPacks : io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder<Any?> {
    override fun encode(msg: Any?, connection: Connection): Packet {
        val buf = connection.ctx.alloc().buffer()
        buf.writeVarInt(1)
            .writeString("minecraft")
            .writeString("core")
            .writeString(MINECRAFT_VERSION)
        return Packet.of(0x0E, buf)
    }
}