package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils.writeMCBoolean
import io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder
import io.github.liyze09.arms.network.packet.Packet

object ChangeDifficulty : ClientBoundPacketEncoder<Int> {
    override fun encode(msg: Int, connection: Connection): Packet {
        val buf = connection.ctx.alloc().buffer()
        val difficulty: Int = if (msg > 0) 3 else 0
        buf.writeByte(difficulty) // difficulty
        buf.writeMCBoolean(false) // is locked
        return Packet.of(0x0B, buf)
    }

}