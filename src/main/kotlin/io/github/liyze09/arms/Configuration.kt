package io.github.liyze09.arms

class Configuration private constructor() {
    var port: Int = 25565
    var maxPlayers: Int = 20
    var viewDistance: Int = 10
    var simulationDistance: Int = 10
    var difficulty: Int = 3
    companion object {
        val instance: Configuration = Configuration()
    }
}
