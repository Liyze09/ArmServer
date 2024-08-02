package io.github.liyze09.arms

class Configuration private constructor() {
    var port: Int = 25565

    companion object {
        val instance: Configuration = Configuration()
    }
}
