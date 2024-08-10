package io.github.liyze09.arms.world

import io.github.liyze09.arms.Configuration
import io.github.liyze09.arms.common.Identifier
import io.github.liyze09.arms.common.Position
import io.github.liyze09.arms.common.toByteArray
import io.github.liyze09.arms.common.toLong
import io.github.liyze09.arms.world.impl.Overworld
import java.security.MessageDigest

object World {
    val dimensions = mutableMapOf<Identifier, Dimension>()
    val seed = Configuration.instance.seed
    val hashedSeed: Long

    init {
        dimensions[Identifier("minecraft", "overworld")] = Overworld()
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(seed.toByteArray())
        hashedSeed = messageDigest.digest().take(8).toByteArray().toLong()
    }

    fun getDimension(id: Identifier) = dimensions[id] ?: throw IllegalArgumentException("Dimension $id not found")

    fun getWorldSpawnPoint(): Position {
        // TODO
        return Position(0, 64, 0)
    }
}