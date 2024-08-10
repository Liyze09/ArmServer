package net.minecraftarm.api.nbt

import io.netty.buffer.ByteBuf

class NbtCompound(
    vararg val tags: Pair<String, NbtTag>
) : NbtTag {
    override val typeID: Byte
        get() = 10

    override fun encode(buf: ByteBuf) {
        for (tag in tags) {
            buf.writeByte(tag.second.typeID.toInt())
            val name = tag.first.toByteArray()
            buf.writeShort(name.size)
            buf.writeBytes(name)
            tag.second.encode(buf)
        }
        buf.writeByte(0)
    }

    fun encodeAsRoot(buf: ByteBuf) {
        buf.writeByte(0x0a)
        encode(buf)
    }
}