package net.minecraftarm.nbt

import io.netty.buffer.ByteBuf

class NbtDouble(val value: Double) : NbtTag {
    override val typeID: Byte
        get() = 6

    override fun encode(buf: ByteBuf) {
        buf.writeDouble(value)
    }
}