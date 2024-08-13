package net.minecraftarm.world

import net.minecraftarm.common.BlockPosition
import net.minecraftarm.registry.block.BlockAction
import net.minecraftarm.registry.block.BlockState

data class BlockUpdateTask(
    val task: Runnable,
    val influenceBlocks: List<BlockPosition>
)

data class BlockUpdateMessage(
    val msg: Any? = null,
    val influenceBlocks: List<BlockPosition> = emptyList(),
    val secondaryUpdates: List<BlockUpdate> = emptyList()
)

data class BlockUpdate(
    val position: BlockPosition,
    val state: BlockState,
    val type: BlockAction
)