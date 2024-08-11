package io.github.liyze09.arms.network.packet.serverbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.Connection.*
import io.github.liyze09.arms.network.PackUtils.readMCBoolean
import io.github.liyze09.arms.network.PackUtils.readString
import io.netty.buffer.ByteBuf

object ClientInformation : io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder {
    override fun decode(buf: ByteBuf, connection: Connection) {
        connection.updateLocale(buf.readString(16))
        connection.updateViewDistance(buf.readByte())
        connection.updateChatMode(ChatMode.entries.toTypedArray()[buf.readByte().toInt()])
        connection.updateChatColors(buf.readMCBoolean())
        val displayedSkinParts = buf.readByte()
        connection.updateDisplayedSkinParts(
            DisplayedSkinParts(
                (displayedSkinParts.toInt() and 0x01) != 0,
                (displayedSkinParts.toInt() and 0x02) != 0,
                (displayedSkinParts.toInt() and 0x04) != 0,
                (displayedSkinParts.toInt() and 0x08) != 0,
                (displayedSkinParts.toInt() and 0x10) != 0,
                (displayedSkinParts.toInt() and 0x20) != 0,
                (displayedSkinParts.toInt() and 0x40) != 0
            )
        )
        connection.updateMainHand(MainHand.entries.toTypedArray()[buf.readByte().toInt()])
        buf.readByte()
        connection.updateAllowServerListings(buf.readMCBoolean())
    }
}
