package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.common.Identifier
import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils.writeMCBoolean
import io.github.liyze09.arms.network.PackUtils.writeString
import io.github.liyze09.arms.network.PackUtils.writeVarInt
import io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder
import io.github.liyze09.arms.network.packet.Packet
import io.github.liyze09.arms.registry.Registries.nbtSerializer
import kotlinx.serialization.encodeToByteArray
import net.benwoodworth.knbt.NbtTag

object RegistryDataPacket : ClientBoundPacketEncoder<RegistryData> {
    override fun encode(msg: RegistryData, connection: Connection): Packet {
        val buf = connection.ctx.alloc().buffer()
        buf.writeString(msg.id.toString())
        buf.writeVarInt(msg.entries.size)
        msg.entries.forEach { (id, nbt) ->
            buf.writeString(id.toString())
            if (nbt != null) {
                buf.writeMCBoolean(true)
                buf.writeBytes(nbtSerializer.encodeToByteArray(nbt))
            } else {
                buf.writeMCBoolean(false)
            }
        }
        return Packet.of(0x07, buf)
    }
}

data class RegistryData(val id: Identifier, val entries: Map<Identifier, NbtTag?>)

