package net.minecraftarm.registry.block

import net.minecraftarm.common.BlockPosition
import net.minecraftarm.registry.Registries
import net.minecraftarm.world.BlockUpdateMessage
import net.minecraftarm.world.Dimension

abstract class Block(val blockSettings: BlockSettings) : Registries.Registry {
    abstract fun getStates(): List<BlockState>
    abstract fun getDefaultState(): BlockState?
    open fun beforeBlockActionApply(
        action: BlockAction,
        dimension: Dimension,
        position: BlockPosition,
        state: BlockState
    ): BlockUpdateMessage = BlockUpdateMessage(
        null,
        emptyList()
    )
    open fun duringBlockActionApply(action: BlockAction, msg: Any?) {}
}