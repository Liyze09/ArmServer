package io.github.liyze09.arms.network.packet.serverbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.packet.clientbound.KnownPacks
import io.netty.buffer.ByteBuf

object LoginAcknowledged : io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder {
    override fun decode(buf: ByteBuf, connection: Connection) {
        connection.updateStatus(Connection.Status.CONFIGURATION)
        connection.sendPacket(null, io.github.liyze09.arms.network.packet.clientbound.FeatureFlags)
        connection.sendPacket(null, KnownPacks)
    }
}
