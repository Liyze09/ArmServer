package io.github.liyze09.arms.network

import io.github.liyze09.arms.common.Identifier
import io.github.liyze09.arms.network.packet.clientbound.PluginMessage
import io.github.liyze09.arms.network.packet.clientbound.PluginMessageBody
import io.netty.buffer.ByteBuf
import java.util.concurrent.ConcurrentHashMap

object PluginChannel {
    private val handlers: MutableMap<Identifier, PluginChannelHandler> = ConcurrentHashMap()
    fun broadcast(identifier: Identifier, data: ByteBuf) {
        handlers[identifier]?.handle(data)
        data.release()
    }

    fun send(identifier: Identifier, data: ByteBuf, connection: Connection) {
        connection.sendPacket(PluginMessageBody(identifier, data), PluginMessage())
        data.release()
    }

    fun register(identifier: Identifier, handler: PluginChannelHandler) {
        handlers[identifier] = handler
    }
}

@FunctionalInterface
interface PluginChannelHandler {
    fun handle(data: ByteBuf)
}