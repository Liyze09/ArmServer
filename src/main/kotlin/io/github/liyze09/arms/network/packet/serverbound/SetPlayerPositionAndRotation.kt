package io.github.liyze09.arms.network.packet.serverbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.NetworkWorld
import io.github.liyze09.arms.network.PackUtils.readMCBoolean
import io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder
import io.netty.buffer.ByteBuf

object SetPlayerPositionAndRotation : ServerBoundPacketDecoder {
    override fun decode(buf: ByteBuf, connection: Connection) {
        val x = buf.readDouble()
        val y = buf.readDouble()
        val z = buf.readDouble()
        val yaw = buf.readFloat()
        val pitch = buf.readFloat()
        val onGround = buf.readMCBoolean()
        NetworkWorld.setPlayerPosition(connection, x, y, z)
        NetworkWorld.setPlayerRotation(connection, yaw, pitch)
        NetworkWorld.setPlayerOnGround(connection, onGround)
    }
}