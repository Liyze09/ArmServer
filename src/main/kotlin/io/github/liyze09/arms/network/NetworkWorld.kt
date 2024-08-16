package io.github.liyze09.arms.network

import io.github.liyze09.arms.GlobalConfiguration
import io.github.liyze09.arms.network.packet.clientbound.*
import net.minecraftarm.common.Identifier
import net.minecraftarm.common.TeleportBody
import net.minecraftarm.entity.Player
import net.minecraftarm.world.World

object NetworkWorld {
    fun newPlayer(connection: Connection): Player {
        // TODO: Get player data from save
        val ret = Player(connection)
        connection.boundedPlayerEntity = ret
        val currentDimension = World.getDimension(Identifier("minecraft", "overworld"))
        val position = World.getWorldSpawnPoint().toEntityPosition()
        ret.loadToWorld(currentDimension, position)
        connection.sendPacket(ret, PlayLogin)
            .sendPacket(
                GlobalConfiguration.instance.difficulty,
                ChangeDifficulty
            )
            .sendPacket(ret.heldItem, SetHeldItem)
            .sendPacket(
                Pair(ret.entityId, 24/*TODO op level*/),
                EntityEvent
            )
            .sendPacket(
                TeleportBody(position),
                SynchronizePlayerPosition
            ).sendPacket(
                Pair(position.x.toInt() and 15, position.z.toInt() and 15),
                SetCenterChunk
            )

        // TODO Recipe Book / Recipes
        // TODO 24/08/09
        return ret
    }

    fun setPlayerPosition(connection: Connection, x: Double, y: Double, z: Double) {
        val pos = connection.boundedPlayerEntity?.position ?: return
        pos.x = x
        pos.y = y
        pos.z = z
    }

    fun setPlayerRotation(connection: Connection, yaw: Float, pitch: Float) {
        val pos = connection.boundedPlayerEntity?.position ?: return
        pos.yaw = yaw
        pos.pitch = pitch
    }

    fun setPlayerOnGround(connection: Connection, onGround: Boolean) {
        val pos = connection.boundedPlayerEntity?.position ?: return
        pos.onGround = onGround
    }
}