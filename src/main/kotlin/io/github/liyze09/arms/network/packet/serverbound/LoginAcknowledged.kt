package io.github.liyze09.arms.network.packet.serverbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder
import io.github.liyze09.arms.network.packet.clientbound.FeatureFlags
import io.github.liyze09.arms.network.packet.clientbound.KnownPacks
import io.netty.buffer.ByteBuf

object LoginAcknowledged : ServerBoundPacketDecoder {
    override fun decode(buf: ByteBuf, connection: Connection) {
        connection.updateStatus(Connection.Status.CONFIGURATION)
        connection.sendPacket(null, FeatureFlags)
        connection.sendPacket(null, KnownPacks)
    }
}
