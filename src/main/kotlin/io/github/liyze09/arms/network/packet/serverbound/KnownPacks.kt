package io.github.liyze09.arms.network.packet.serverbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder
import io.github.liyze09.arms.network.packet.clientbound.FinishConfiguration
import io.github.liyze09.arms.registry.Registries
import io.netty.buffer.ByteBuf

object KnownPacks : ServerBoundPacketDecoder {
    override fun decode(buf: ByteBuf, connection: Connection) {
        Registries.sendRegistryData(connection)
        connection.sendPacket(
            null,
            FinishConfiguration
        )
    }
}