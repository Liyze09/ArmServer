package io.github.liyze09.arms.network.packet.serverbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils.readIdentifier
import io.netty.buffer.ByteBuf

object PluginMessage : io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder {
    override fun decode(buf: ByteBuf, connection: Connection) =
        io.github.liyze09.arms.network.PluginChannel.broadcast(buf.readIdentifier(), buf.readBytes(buf.readableBytes()))
}