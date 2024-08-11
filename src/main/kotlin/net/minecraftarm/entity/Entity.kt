package net.minecraftarm.entity

import io.github.liyze09.arms.network.Connection
import net.minecraftarm.common.Position
import net.minecraftarm.registry.Registries
import net.minecraftarm.world.Dimension
import java.util.concurrent.ThreadLocalRandom

abstract class Entity : Registries.Registry {
    val entityId = ThreadLocalRandom.current().nextInt(Int.MIN_VALUE, Int.MAX_VALUE)
    abstract val uuid: Connection.UUID
    lateinit var currentDimension: Dimension
    lateinit var position: Position
    fun loadToWorld(dimension: Dimension, position: Position) {
        this.currentDimension = dimension
        this.position = position
        dimension.entityMap[position] = this
    }
}