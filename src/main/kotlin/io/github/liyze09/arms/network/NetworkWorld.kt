package io.github.liyze09.arms.network

import io.github.liyze09.arms.Configuration
import io.github.liyze09.arms.common.Identifier
import io.github.liyze09.arms.entity.Player
import io.github.liyze09.arms.network.packet.clientbound.ChangeDifficulty
import io.github.liyze09.arms.network.packet.clientbound.EntityEvent
import io.github.liyze09.arms.network.packet.clientbound.PlayLogin
import io.github.liyze09.arms.network.packet.clientbound.SetHeldItem
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
        // TODO 24/08/08
        return ret
    }
}