package io.github.liyze09.arms.world

import io.github.liyze09.arms.common.Identifier
import io.github.liyze09.arms.common.Position
import io.github.liyze09.arms.common.toByteArray
import io.github.liyze09.arms.common.toLong
import java.security.MessageDigest
import java.util.concurrent.ThreadLocalRandom

object World {
    val dimensions = mutableMapOf<Identifier, Dimension>()
    val seed = ThreadLocalRandom.current().nextLong()
    val hashedSeed: Long

    init {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(seed.toByteArray())
        hashedSeed = messageDigest.digest().take(8).toByteArray().toLong()
    }

    fun getDimension(id: Identifier): Dimension {
        return dimensions[id] ?: throw IllegalArgumentException("Dimension $id not found")
    }

    fun getWorldSpawnPoint(): Position {
        // TODO
        return Position(0, 64, 0)
    }
}