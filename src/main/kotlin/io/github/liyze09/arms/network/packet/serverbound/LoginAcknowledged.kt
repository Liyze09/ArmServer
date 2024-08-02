package io.github.liyze09.arms.network.packet.serverbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder
import io.netty.buffer.ByteBuf

class LoginAcknowledged : ServerBoundPacketDecoder {
    override fun decode(buf: ByteBuf, connection: Connection) {
        connection.updateStatus(Connection.Status.CONFIGURATION)
    }
}
