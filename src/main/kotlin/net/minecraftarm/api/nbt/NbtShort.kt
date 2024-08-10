package net.minecraftarm.api.nbt

import io.netty.buffer.ByteBuf

class NbtShort(val value: Short) : NbtTag {
    override val typeID: Byte
        get() = 2

    override fun encode(buf: ByteBuf) {
        buf.writeShort(value.toInt())
    }
}