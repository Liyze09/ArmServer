package net.minecraftarm.nbt

import io.netty.buffer.ByteBuf

class NbtLongArray(val value: LongArray) : NbtTag {
    override val typeID: Byte
        get() = 12

    override fun encode(buf: ByteBuf) {
        buf.writeInt(value.size)
        for (i in value) {
            buf.writeLong(i)
        }
    }
}