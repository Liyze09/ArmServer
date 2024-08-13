@file:JvmName("Utils")

package net.minecraftarm.common

fun Long.toByteArray(): ByteArray {
    val b = ByteArray(8)
    b[7] = (this and 0xffL).toByte()
    b[6] = (this shr 8 and 0xffL).toByte()
    b[5] = (this shr 16 and 0xffL).toByte()
    b[4] = (this shr 24 and 0xffL).toByte()
    b[3] = (this shr 32 and 0xffL).toByte()
    b[2] = (this shr 40 and 0xffL).toByte()
    b[1] = (this shr 48 and 0xffL).toByte()
    b[0] = (this shr 56 and 0xffL).toByte()
    return b
}

fun ByteArray.toLong(): Long {
    return (((this[0].toLong() and 0xffL) shl 56)
            or ((this[1].toLong() and 0xffL) shl 48)
            or ((this[2].toLong() and 0xffL) shl 40)
            or ((this[3].toLong() and 0xffL) shl 32)
            or ((this[4].toLong() and 0xffL) shl 24)
            or ((this[5].toLong() and 0xffL) shl 16)
            or ((this[6].toLong() and 0xffL) shl 8)
            or ((this[7].toLong() and 0xffL) shl 0))
}

fun toYZX(x: Int, y: Int, z: Int) = y shl 8 or (z shl 4) or x
fun toXZ(x: Int, z: Int): Long = (z.toLong() shl 32) or x.toLong()