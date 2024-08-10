package io.github.liyze09.arms.registry.block

import io.github.liyze09.arms.common.Position
import io.github.liyze09.arms.registry.Registries
import io.github.liyze09.arms.world.Dimension

abstract class Block(val blockSettings: BlockSettings) : Registries.Registry {
    abstract fun getStates(): List<BlockState>
    abstract fun getDefaultState(): BlockState?
    open fun beforeBlockActionApply(action: BlockAction, dimension: Dimension, position: Position): Any? = null
    open fun duringBlockActionApply(action: BlockAction, msg: Any?) {}
}