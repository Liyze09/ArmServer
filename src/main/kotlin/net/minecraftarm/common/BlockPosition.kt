package net.minecraftarm.common


class BlockPosition {
    var x: Int
    var y: Int
    var z: Int

    constructor(x: Int, y: Int, z: Int) {
        this.x = x
        this.y = y
        this.z = z
    }

    constructor(posInt: Long) {
        x = (posInt shr 38).toInt()
        y = (posInt shl 52 shr 52).toInt()
        z = (posInt shl 26 shr 38).toInt()
    }

    fun toLong() = ((x and 0x3FFFFFF) shl 38) or ((z and 0x3FFFFFF) shl 12) or (y and 0xFFF)
    fun toEntityPosition() = EntityPosition(x.toDouble(), y.toDouble(), z.toDouble(), 0F, 0F, true)

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BlockPosition) return false

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }
}
