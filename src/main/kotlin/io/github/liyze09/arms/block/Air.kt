package io.github.liyze09.arms.block

import io.github.liyze09.arms.registry.Block
import io.github.liyze09.arms.registry.BlockSettings
import io.github.liyze09.arms.registry.BlockState
import io.github.liyze09.arms.registry.BlockType

class Air : Block(BlockSettings().strength(0.0F).type(BlockType.AIR)) {
    private val defaultState = object : BlockState() {
        init {
            protocolId = 0
        }
    }

    override fun getStates(): List<BlockState> = listOf(defaultState)

    override fun getDefaultState(): BlockState = defaultState
}