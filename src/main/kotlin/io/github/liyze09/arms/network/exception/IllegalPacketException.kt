package io.github.liyze09.arms.network.exception

class IllegalPacketException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
}
