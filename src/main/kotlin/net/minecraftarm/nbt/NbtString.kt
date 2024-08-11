package net.minecraftarm.nbt

import io.netty.buffer.ByteBuf

class NbtString(val value: String) : NbtTag {
    constructor(any: Any) : this(any.toString())

    override val typeID: Byte
        get() = 8

    override fun encode(buf: ByteBuf) {
        val bytes = value.toByteArray()
        buf.writeShort(bytes.size)
        buf.writeBytes(bytes)
    }
}

