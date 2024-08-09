package io.github.liyze09.arms

import java.util.concurrent.ThreadLocalRandom

class Configuration private constructor() {
    var port: Int = 25565
    var maxPlayers: Int = 20
    var viewDistance: Int = 10
    var simulationDistance: Int = 10
    var difficulty: Int = 3
    var seed: Long = ThreadLocalRandom.current().nextLong()

    companion object {
        val instance: Configuration = Configuration()
    }
}
