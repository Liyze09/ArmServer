package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils.writeMCBoolean
import io.github.liyze09.arms.network.PackUtils.writeString
import io.github.liyze09.arms.network.PackUtils.writeVarInt
import io.github.liyze09.arms.network.packet.Packet
import net.minecraftarm.common.Identifier
import net.minecraftarm.nbt.NbtCompound

object RegistryDataPacket : io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder<RegistryData> {
    override fun encode(msg: RegistryData, connection: Connection): Packet {
        val buf = connection.ctx.alloc().buffer()
        buf.writeString(msg.id.toString())
        buf.writeVarInt(msg.entries.size)
        msg.entries.forEach { (id, nbt) ->
            buf.writeString(id.toString())
            if (nbt != null) {
                buf.writeMCBoolean(true)
                nbt.encodeAsRoot(buf)
            } else {
                buf.writeMCBoolean(false)
            }
        }
        return Packet.of(0x07, buf)
    }
}

data class RegistryData(val id: Identifier, val entries: Map<Identifier, NbtCompound?>)

