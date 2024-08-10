package net.minecraftarm.api.nbt

import io.netty.buffer.ByteBuf

class NbtInt(val value: Int) : NbtTag {
    override val typeID: Byte
        get() = 3

    override fun encode(buf: ByteBuf) {
        buf.writeInt(value)
    }
}