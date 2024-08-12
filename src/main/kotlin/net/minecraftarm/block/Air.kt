package net.minecraftarm.block

import net.minecraftarm.registry.block.Block
import net.minecraftarm.registry.block.BlockSettings
import net.minecraftarm.registry.block.BlockState
import net.minecraftarm.registry.block.BlockType

object Air : Block(BlockSettings().strength(0.0F).type(BlockType.AIR)) {
    private val defaultState = object : BlockState() {
        override val parent: Block = this@Air
        init {
            protocolId = 0
        }
    }

    override fun getStates(): List<BlockState> = listOf(defaultState)

    override fun getDefaultState(): BlockState = defaultState
}