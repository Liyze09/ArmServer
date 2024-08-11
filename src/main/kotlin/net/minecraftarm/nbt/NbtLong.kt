package net.minecraftarm.nbt

import io.netty.buffer.ByteBuf

class NbtLong(val value: Long) : NbtTag {
    override val typeID: Byte
        get() = 4

    override fun encode(buf: ByteBuf) {
        buf.writeLong(value)
    }
}