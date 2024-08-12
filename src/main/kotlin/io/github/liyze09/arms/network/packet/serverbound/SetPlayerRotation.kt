package io.github.liyze09.arms.network.packet.serverbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.NetworkWorld
import io.github.liyze09.arms.network.PackUtils.readMCBoolean
import io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder
import io.netty.buffer.ByteBuf

object SetPlayerRotation : ServerBoundPacketDecoder {
    override fun decode(buf: ByteBuf, connection: Connection) {
        val yaw = buf.readFloat()
        val pitch = buf.readFloat()
        val onGround = buf.readMCBoolean()
        NetworkWorld.setPlayerRotation(connection, yaw, pitch)
        NetworkWorld.setPlayerOnGround(connection, onGround)
    }
}