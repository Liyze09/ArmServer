package net.minecraftarm.common

class Identifier(val namespace: String, val path: String) {

    constructor(identifier: String) :
            this(
                identifier.substringBefore(':', "minecraft"),
                identifier.substringAfter(':')
            )

    override fun toString(): String = "$namespace:$path"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Identifier) return false
        if (namespace != other.namespace) return false
        return path == other.path
    }

    override fun hashCode(): Int = namespace.hashCode() + path.hashCode()

    companion object {
        internal fun mc(path: String) = Identifier("minecraft", path)
    }
}