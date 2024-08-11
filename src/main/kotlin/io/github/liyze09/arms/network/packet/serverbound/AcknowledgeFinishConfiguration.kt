package io.github.liyze09.arms.network.packet.serverbound

import io.github.liyze09.arms.network.Connection
import io.netty.buffer.ByteBuf

object AcknowledgeFinishConfiguration : io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder {
    override fun decode(buf: ByteBuf, connection: Connection) {
        connection.updateStatus(Connection.Status.PLAY)
        io.github.liyze09.arms.network.NetworkWorld.newPlayer(connection)
    }
}