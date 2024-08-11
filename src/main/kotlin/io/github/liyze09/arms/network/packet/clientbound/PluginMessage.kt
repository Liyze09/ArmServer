package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils.writeString
import io.github.liyze09.arms.network.packet.Packet
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import net.minecraftarm.common.Identifier

class PluginMessage :
    io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder<PluginMessageBody> {
    override fun encode(
        msg: PluginMessageBody,
        connection: Connection
    ): Packet {
        val buf = ByteBufAllocator.DEFAULT.ioBuffer()
        buf.writeString(msg.channel.toString())
        buf.writeBytes(msg.data)
        return Packet.of(0x01, buf)
    }
}

@JvmRecord
data class PluginMessageBody(val channel: Identifier, val data: ByteBuf)
