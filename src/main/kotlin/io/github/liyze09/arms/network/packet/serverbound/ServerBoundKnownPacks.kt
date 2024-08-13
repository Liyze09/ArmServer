package io.github.liyze09.arms.network.packet.serverbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.packet.clientbound.FinishConfiguration
import io.netty.buffer.ByteBuf
import net.minecraftarm.registry.Registries

object ServerBoundKnownPacks : io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder {
    override fun decode(buf: ByteBuf, connection: Connection) {
        Registries.sendRegistryData(connection)
        connection.sendPacket(
            null,
            FinishConfiguration
        )
    }
}