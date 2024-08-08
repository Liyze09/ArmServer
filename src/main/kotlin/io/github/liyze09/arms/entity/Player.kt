package io.github.liyze09.arms.entity

import io.github.liyze09.arms.network.Connection

class Player internal constructor(val connection: Connection) : LivingEntity() {
    val previousGamemode: GameMode? = null
    var gamemode = GameMode.SURVIVAL
    var portalCooldownTick = 0
    var heldItem = 0
    override fun whenDead() {

    }

    override val uuid: Connection.UUID
        get() = connection.getUUID()
}

enum class GameMode {
    SURVIVAL,
    CREATIVE,
    ADVENTURE,
    SPECTATOR
}
