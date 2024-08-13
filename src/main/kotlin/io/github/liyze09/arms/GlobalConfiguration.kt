package io.github.liyze09.arms

import java.util.concurrent.ThreadLocalRandom

class GlobalConfiguration private constructor() {
    var port: Int = 25565
    var maxPlayers: Int = 20
    var viewDistance: Int = 10
    var simulationDistance: Int = 10
    var difficulty: Int = 3
    var seed: Long = ThreadLocalRandom.current().nextLong()
    var logFormat = ""
    var logLevel = "info"

    companion object {
        val instance: GlobalConfiguration = GlobalConfiguration()
    }
}
