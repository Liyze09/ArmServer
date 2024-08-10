package io.github.liyze09.arms.network

import io.github.liyze09.arms.Configuration
import io.github.liyze09.arms.common.Identifier
import io.github.liyze09.arms.common.TeleportBody
import io.github.liyze09.arms.entity.Player
import io.github.liyze09.arms.network.packet.clientbound.*
import io.github.liyze09.arms.world.World

object NetworkWorld {
    fun newPlayer(connection: Connection): Player {
        // TODO: Get player data from save
        val ret = Player(connection)
        val currentDimension = World.getDimension(Identifier("minecraft", "overworld"))
        val position = World.getWorldSpawnPoint()
        ret.loadToWorld(currentDimension, position)
        connection.sendPacket(ret, PlayLogin)
            .sendPacket(Configuration.instance.difficulty, ChangeDifficulty)
            .sendPacket(ret.heldItem, SetHeldItem)
            .sendPacket(Pair(ret.entityId, 24/*TODO op level*/), EntityEvent)
            .sendPacket(
                TeleportBody(position.x.toDouble(), position.y.toDouble(), position.z.toDouble(), 0F, 0F),
                SynchronizePlayerPosition
            )

        // TODO Recipe Book / Recipes
        // TODO 24/08/09
        return ret
    }
}