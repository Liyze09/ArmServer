package io.github.liyze09.arms.network.packet.serverbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils.readIdentifier
import io.github.liyze09.arms.network.PluginChannel
import io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder
import io.netty.buffer.ByteBuf

class PluginMessage : ServerBoundPacketDecoder {
    override fun decode(buf: ByteBuf, connection: Connection) =
        PluginChannel.broadcast(buf.readIdentifier(), buf.readBytes(buf.readableBytes()))

}