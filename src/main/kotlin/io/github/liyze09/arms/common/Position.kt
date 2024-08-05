package io.github.liyze09.arms.common

class Position {
    val x: Int
    val y: Int
    val z: Int

    constructor(x: Int, y: Int, z: Int) {
        this.x = x
        this.y = y
        this.z = z
    }

    constructor(posInt: Int) {
        x = posInt shr 38
        y = posInt shl 52 shr 52
        z = posInt shl 26 shr 38
    }
}
