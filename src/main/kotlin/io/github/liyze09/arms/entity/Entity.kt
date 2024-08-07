package io.github.liyze09.arms.entity

import io.github.liyze09.arms.common.Position
import io.github.liyze09.arms.registry.Registries
import io.github.liyze09.arms.world.Dimension
import java.util.concurrent.ThreadLocalRandom

abstract class Entity : Registries.Registry() {
    val entityId = ThreadLocalRandom.current().nextInt(Int.MIN_VALUE, Int.MAX_VALUE)
    lateinit var currentDimension: Dimension
    lateinit var position: Position
}