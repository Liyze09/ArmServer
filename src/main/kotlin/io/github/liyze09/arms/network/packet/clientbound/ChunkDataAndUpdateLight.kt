package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder
import io.github.liyze09.arms.network.packet.Packet
import net.minecraftarm.world.Chunk

object ChunkDataAndUpdateLight : ClientBoundPacketEncoder<Chunk> {
    override fun encode(msg: Chunk, connection: Connection): Packet {
        val buf = connection.ctx.alloc().buffer()
        buf.writeInt(msg.x)
        buf.writeInt(msg.z)
        msg.writeToBuffer(buf)
        return Packet.of(0x27, buf)
    }
}