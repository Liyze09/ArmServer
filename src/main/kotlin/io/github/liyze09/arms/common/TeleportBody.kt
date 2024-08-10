package io.github.liyze09.arms.common

import java.util.concurrent.ThreadLocalRandom

class TeleportBody(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float,
    var id: Int = ThreadLocalRandom.current().nextInt(0, 16383),
) {

}