package io.github.liyze09.arms.network

import io.github.liyze09.arms.Configuration
import io.github.liyze09.arms.network.packet.clientbound.SynchronizePlayerPosition
import net.minecraftarm.common.Identifier
import net.minecraftarm.common.TeleportBody
import net.minecraftarm.entity.Player
import net.minecraftarm.world.World

object NetworkWorld {
    fun newPlayer(connection: Connection): Player {
        // TODO: Get player data from save
        val ret = Player(connection)
        val currentDimension = World.getDimension(Identifier("minecraft", "overworld"))
        val position = World.getWorldSpawnPoint()
        ret.loadToWorld(currentDimension, position)
        connection.sendPacket(ret, io.github.liyze09.arms.network.packet.clientbound.PlayLogin)
            .sendPacket(
                Configuration.instance.difficulty,
                io.github.liyze09.arms.network.packet.clientbound.ChangeDifficulty
            )
            .sendPacket(ret.heldItem, io.github.liyze09.arms.network.packet.clientbound.SetHeldItem)
            .sendPacket(
                Pair(ret.entityId, 24/*TODO op level*/),
                io.github.liyze09.arms.network.packet.clientbound.EntityEvent
            )
            .sendPacket(
                TeleportBody(position.x.toDouble(), position.y.toDouble(), position.z.toDouble(), 0F, 0F),
                SynchronizePlayerPosition
            )

        // TODO Recipe Book / Recipes
        // TODO 24/08/09
        return ret
    }
}