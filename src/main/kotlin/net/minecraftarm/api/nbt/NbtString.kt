package net.minecraftarm.api.nbt

import io.github.liyze09.arms.common.Identifier
import io.netty.buffer.ByteBuf

class NbtString(val value: String) : NbtTag {
    constructor(identifier: Identifier) : this(identifier.toString())

    override val typeID: Byte
        get() = 8

    override fun encode(buf: ByteBuf) {
        val bytes = value.toByteArray()
        buf.writeShort(bytes.size)
        buf.writeBytes(bytes)
    }
}

