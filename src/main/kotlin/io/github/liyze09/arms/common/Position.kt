package io.github.liyze09.arms.common


class Position {
    var x: Int
    var y: Int
    var z: Int

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

    fun toLong() = ((x and 0x3FFFFFF) shl 38) or ((z and 0x3FFFFFF) shl 12) or (y and 0xFFF)

}
