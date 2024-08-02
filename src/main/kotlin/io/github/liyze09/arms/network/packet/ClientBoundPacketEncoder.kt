package io.github.liyze09.arms.network.packet

import io.github.liyze09.arms.network.Connection
import java.io.IOException

fun interface ClientBoundPacketEncoder<T> {
    @Throws(IOException::class)
    fun encode(msg: T, connection: Connection): Packet
}
