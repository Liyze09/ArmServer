package io.github.liyze09.arms.network.packet

import io.github.liyze09.arms.network.Connection
import io.netty.buffer.ByteBuf

fun interface ServerBoundPacketDecoder {
    fun decode(buf: ByteBuf, connection: Connection)
}
