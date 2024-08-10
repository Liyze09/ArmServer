package io.github.liyze09.arms.registry.block

class BlockSettings {
    internal var hardness: Float = 2.0F
    internal var resistance: Float = 6.0F
    internal var type = BlockType.SOLID
    fun hardness(hardness: Float): BlockSettings {
        this.hardness = hardness
        return this
    }

    fun resistance(resistance: Float): BlockSettings {
        this.resistance = resistance
        return this
    }

    fun strength(strength: Float): BlockSettings {
        this.hardness = strength
        this.resistance = strength
        return this
    }

    fun type(type: BlockType): BlockSettings {
        this.type = type
        return this
    }
}