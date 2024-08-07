package io.github.liyze09.arms.entity

import io.github.liyze09.arms.network.Connection

class Player internal constructor(val connection: Connection) : LivingEntity() {
    val previousGamemode: GameMode? = null
    var gamemode = GameMode.SURVIVAL
    var portalCooldownTick = 0
}

enum class GameMode {
    SURVIVAL,
    CREATIVE,
    ADVENTURE,
    SPECTATOR
}
