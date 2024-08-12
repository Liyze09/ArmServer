package net.minecraftarm.common

import java.util.concurrent.ThreadLocalRandom

data class TeleportBody(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float,
    var id: Int = ThreadLocalRandom.current().nextInt(0, 16383),
) {
    constructor(entityPosition: EntityPosition) : this(
        entityPosition.x,
        entityPosition.y,
        entityPosition.z,
        entityPosition.yaw,
        entityPosition.pitch
    )
}