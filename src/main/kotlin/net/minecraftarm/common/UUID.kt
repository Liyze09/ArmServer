package net.minecraftarm.common

@JvmRecord
data class UUID(val a: Long, val b: Long) {
    override fun toString(): String = String.format("%016x%016x", a, b)
}