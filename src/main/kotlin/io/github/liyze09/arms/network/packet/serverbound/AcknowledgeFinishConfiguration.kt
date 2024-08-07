package io.github.liyze09.arms.network.packet.serverbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.NetworkWorld
import io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder
import io.netty.buffer.ByteBuf

object AcknowledgeFinishConfiguration : ServerBoundPacketDecoder {
    override fun decode(buf: ByteBuf, connection: Connection) {
        connection.updateStatus(Connection.Status.PLAY)
        NetworkWorld.newPlayer(connection)
    }
}