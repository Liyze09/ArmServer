package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.Configuration
import io.github.liyze09.arms.entity.Player
import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils.writeMCBoolean
import io.github.liyze09.arms.network.PackUtils.writeString
import io.github.liyze09.arms.network.PackUtils.writeVarInt
import io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder
import io.github.liyze09.arms.network.packet.Packet
import io.github.liyze09.arms.world.World

object PlayLogin : ClientBoundPacketEncoder<Player> {
    override fun encode(msg: Player, connection: Connection): Packet {
        val buf = connection.ctx.alloc().buffer()
        buf.writeInt(msg.entityId)
            .writeMCBoolean(false) // is hardcore
            .writeVarInt(World.dimensions.size)
        World.dimensions.forEach { (id, _) ->
            buf.writeString(id.toString())
        }
        buf.writeVarInt(Configuration.instance.viewDistance)
            .writeVarInt(Configuration.instance.simulationDistance)
            .writeVarInt(Configuration.instance.maxPlayers)
            .writeMCBoolean(false) // is reduced debug info
            .writeMCBoolean(true) // enable respawn screen
            .writeMCBoolean(false) // do limited crafting
            .writeVarInt(msg.currentDimension.getProtocolId()) // dimension id
            .writeString(msg.currentDimension.name.toString()) // dimension name
            .writeLong(World.hashedSeed)
            .writeByte(msg.gamemode.ordinal)
            .writeByte(msg.previousGamemode?.ordinal ?: -1)
            .writeMCBoolean(false) // is debug
            .writeMCBoolean(false) // is flat
            .writeMCBoolean(false) // TODO Last Death
            .writeVarInt(msg.portalCooldownTick)
            .writeMCBoolean(false) // Enforce Secure Chat
        return Packet.of(0x2B, buf)
    }
}