package net.minecraftarm.world.gen.impl

import io.github.liyze09.arms.GlobalConfiguration
import kotlin.math.floor
import kotlin.random.Random

// COPYRIGHT 2002 KEN PERLIN.
@Suppress("LocalVariableName", "SpellCheckingInspection")
object PerlinNoise {
    fun noise(x: Double, y: Double, z: Double): Double {
        var x = x
        var y = y
        var z = z
        val X = floor(x).toInt() and 255 // FIND UNIT CUBE THAT
        val Y = floor(y).toInt() and 255 // CONTAINS POINT.
        val Z = floor(z).toInt() and 255
        x -= floor(x) // FIND RELATIVE X,Y,Z
        y -= floor(y) // OF POINT IN CUBE.
        z -= floor(z)
        val u = fade(x) // COMPUTE FADE CURVES
        val v = fade(y) // FOR EACH OF X,Y,Z.
        val w = fade(z)
        val A = p[X] + Y
        val AA = p[A] + Z
        val AB = p[A + 1] + Z // HASH COORDINATES OF
        val B = p[X + 1] + Y
        val BA = p[B] + Z
        val BB = p[B + 1] + Z // THE 8 CUBE CORNERS,

        return lerp(
            w, lerp(
                v, lerp(
                    u, grad(p[AA], x, y, z),  // AND ADD
                    grad(p[BA], x - 1, y, z)
                ),  // BLENDED
                lerp(
                    u, grad(p[AB], x, y - 1, z),  // RESULTS
                    grad(p[BB], x - 1, y - 1, z)
                )
            ),  // FROM  8
            lerp(
                v, lerp(
                    u, grad(p[AA + 1], x, y, z - 1),  // CORNERS
                    grad(p[BA + 1], x - 1, y, z - 1)
                ),  // OF CUBE
                lerp(
                    u, grad(p[AB + 1], x, y - 1, z - 1),
                    grad(p[BB + 1], x - 1, y - 1, z - 1)
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