package net.minecraftarm.api.nbt

import io.netty.buffer.ByteBuf

class NbtFloat(val value: Float) : NbtTag {
    override val typeID: Byte
        get() = 5

    override fun encode(buf: ByteBuf) {
        buf.writeFloat(value)
    }
}