package io.github.liyze09.arms.network

import io.github.liyze09.arms.common.Identifier
import io.github.liyze09.arms.entity.Player
import io.github.liyze09.arms.network.packet.clientbound.PlayLogin
import io.github.liyze09.arms.world.World

object NetworkWorld {
    fun newPlayer(connection: Connection): Player {
        // TODO: Get player data from save
        val ret = Player(connection)
        val currentDimension = World.getDimension(Identifier("minecraft", "overworld"))
        val position = World.getWorldSpawnPoint()
        ret.currentDimension = currentDimension
        ret.position = position
        currentDimension.entityMap[position] = ret
        connection.sendPacket(ret, PlayLogin)
        // TODO 24/08/07
        return ret
    }
}