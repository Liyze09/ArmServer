package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils.writeMCBoolean
import io.github.liyze09.arms.network.PackUtils.writeVarInt
import io.github.liyze09.arms.network.packet.Packet
import net.minecraftarm.common.UUID

object LoginSuccess : io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder<LoginSuccessBody> {
    override fun encode(msg: LoginSuccessBody, connection: Connection): Packet {
        val buffer = connection.ctx.alloc().buffer()
        // UUID
        buffer.writeLong(msg.uuid.a)
        buffer.writeLong(msg.uuid.b)
        // Username
        val name = msg.username.toByteArray()
        buffer.writeVarInt(name.size)
        buffer.writeBytes(name)

        buffer.writeVarInt(0)

        buffer.writeMCBoolean(true)

        return Packet.of(0x02, buffer)
    }

}

@JvmRecord
data class LoginSuccessBody(
    val uuid: UUID,
    val username: String
)
