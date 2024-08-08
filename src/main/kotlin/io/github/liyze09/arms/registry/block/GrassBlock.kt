package io.github.liyze09.arms.registry.block

import io.github.liyze09.arms.registry.Block
import io.github.liyze09.arms.registry.BlockSettings
import io.github.liyze09.arms.registry.BlockState

class GrassBlock : Block(BlockSettings().strength(0.6F)) {
    private val states = listOf(GrassBlockState(false), GrassBlockState(true))
    override fun getStates() = states
    override fun getDefaultState(): BlockState = states[0]
    class GrassBlockState(var snowy: Boolean) : BlockState() {
        override fun getProtocolId(): Int = if (snowy) 8 else 9
    }
}