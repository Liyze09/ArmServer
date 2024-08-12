package net.minecraftarm.common

data class EntityPosition(
    var x: Double,
    var y: Double,
    var z: Double,
    var yaw: Float,
    var pitch: Float,
    var onGround: Boolean
)