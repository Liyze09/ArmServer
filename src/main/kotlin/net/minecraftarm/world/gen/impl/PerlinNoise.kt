package net.minecraftarm.world.gen.impl

import io.github.liyze09.arms.GlobalConfiguration
import kotlin.math.floor
import kotlin.random.Random

// COPYRIGHT 2002 KEN PERLIN.
@Suppress("LocalVariableName", "SpellCheckingInspection")
object PerlinNoise {
    const val HASH_MASK = 255 // Define the mask for hashing coordinates.

    fun noise(x: Double, y: Double, z: Double): Double {
        val floorX = floor(x).toInt()
        val floorY = floor(y).toInt()
        val floorZ = floor(z).toInt()

        val X = floorX and HASH_MASK
        val Y = floorY and HASH_MASK
        val Z = floorZ and HASH_MASK

        val relX = x - floorX.toDouble()
        val relY = y - floorY.toDouble()
        val relZ = z - floorZ.toDouble()

        val u = fade(relX)
        val v = fade(relY)
        val w = fade(relZ)

        val hashA = p[X] + Y
        val hashAA = p[hashA] + Z
        val hashAB = p[hashA + 1] + Z
        val hashB = p[X + 1] + Y
        val hashBA = p[hashB] + Z
        val hashBB = p[hashB + 1] + Z

        return lerp(
            w,
            lerp(
                v,
                lerp(u, grad(p[hashAA], relX, relY, relZ), grad(p[hashBA], relX - 1, relY, relZ)),
                lerp(u, grad(p[hashAB], relX, relY - 1, relZ), grad(p[hashBB], relX - 1, relY - 1, relZ))
            ),
            lerp(
                v,
                lerp(u, grad(p[hashAA + 1], relX, relY, relZ - 1), grad(p[hashBA + 1], relX - 1, relY, relZ - 1)),
                lerp(
                    u,
                    grad(p[hashAB + 1], relX, relY - 1, relZ - 1),
                    grad(p[hashBB + 1], relX - 1, relY - 1, relZ - 1)
                )
            )
        )
    }


    fun fade(t: Double): Double {
        return t * t * t * (t * (t * 6 - 15) + 10)
    }

    fun lerp(t: Double, a: Double, b: Double): Double {
        return a + t * (b - a)
    }

    fun grad(hash: Int, x: Double, y: Double, z: Double): Double {
        val h = hash and 15 // CONVERT LO 4 BITS OF HASH CODE
        val u = if (h < 8) x else y // INTO 12 GRADIENT DIRECTIONS.
        val v = if (h < 4) y else if (h == 12 || h == 14) x else z
        return (if ((h and 1) == 0) u else -u) + (if ((h and 2) == 0) v else -v)
    }

    val p = IntArray(512)

    init {
        val random = Random(GlobalConfiguration.instance.seed)
        for (i in 0..255) {
            p[i] = random.nextInt(0, 255)
            p[256 + i] = p[i]
        }
    }
}