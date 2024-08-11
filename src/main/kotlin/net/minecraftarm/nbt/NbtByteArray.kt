package net.minecraftarm.nbt

import io.netty.buffer.ByteBuf

class NbtByteArray(val value: ByteArray) : NbtTag {
    override val typeID: Byte
        get() = 7

    override fun encode(buf: ByteBuf) {
        buf.writeInt(value.size)
        buf.writeBytes(value)
    }
}