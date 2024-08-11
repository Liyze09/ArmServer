package net.minecraftarm.world

import io.github.liyze09.arms.Configuration
import net.minecraftarm.common.Identifier
import net.minecraftarm.common.Position
import net.minecraftarm.common.toByteArray
import net.minecraftarm.common.toLong
import net.minecraftarm.world.impl.Overworld
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