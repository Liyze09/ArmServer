package net.minecraftarm.api.nbt

import io.netty.buffer.ByteBuf

class NbtByte(val value: Byte) : NbtTag {
    constructor(value: Boolean) : this(if (value) 1 else 0)

    override val typeID: Byte
        get() = 1

    override fun encode(buf: ByteBuf) {
        buf.writeByte(value.toInt())
    }
}