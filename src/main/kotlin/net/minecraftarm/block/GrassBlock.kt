package net.minecraftarm.block

import net.minecraftarm.registry.block.Block
import net.minecraftarm.registry.block.BlockSettings
import net.minecraftarm.registry.block.BlockState

object GrassBlock : Block(BlockSettings().strength(0.6F)) {
    private val states = listOf(GrassBlockState(false), GrassBlockState(true))
    override fun getStates() = states
    override fun getDefaultState(): BlockState = states[0]
    class GrassBlockState(snowy: Boolean) : BlockState() {
        override val parent = GrassBlock
        init {
            protocolId = if (snowy) 8 else 9
        }
    }
}