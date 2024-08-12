package net.minecraftarm.entity

import net.minecraftarm.common.EntityPosition
import net.minecraftarm.common.UUID
import net.minecraftarm.registry.Registries
import net.minecraftarm.world.Dimension
import java.util.concurrent.atomic.AtomicInteger

abstract class Entity : Registries.Registry {
    val entityId = publicId.addAndGet(1)
    abstract val uuid: UUID
    lateinit var currentDimension: Dimension
    lateinit var position: EntityPosition
    fun loadToWorld(dimension: Dimension, position: EntityPosition) {
        this.currentDimension = dimension
        this.position = position
        dimension.entities[entityId] = this
    }

    companion object {
        val publicId: AtomicInteger = AtomicInteger(0)
    }
}