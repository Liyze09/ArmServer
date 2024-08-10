package io.github.liyze09.arms.registry.item

class ItemSettings {
    var maxCount: Int = 64
    var fireproof: Boolean = false
    fun maxCount(maxStackSize: Int): ItemSettings {
        this.maxCount = maxStackSize
        return this
    }

    fun fireproof(fireproof: Boolean): ItemSettings {
        this.fireproof = fireproof
        return this
    }
}