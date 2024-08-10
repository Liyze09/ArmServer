package net.minecraftarm.api.nbt

import io.netty.buffer.ByteBuf

class NbtIntArray(val value: IntArray) : NbtTag {
    override val typeID: Byte
        get() = 11

    override fun encode(buf: ByteBuf) {
        buf.writeInt(value.size)
        for (i in value) {
            buf.writeInt(i)
        }
    }
}