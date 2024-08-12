package net.minecraftarm.registry.block

abstract class BlockState {
    var protocolId: Int = -1
    abstract val parent: Block
}