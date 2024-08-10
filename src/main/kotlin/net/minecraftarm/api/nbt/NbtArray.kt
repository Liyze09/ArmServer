package net.minecraftarm.api.nbt

import io.netty.buffer.ByteBuf

class NbtArray<T : NbtTag>(val tags: List<T>) : NbtTag {
    constructor(vararg tags: T) : this(tags.toList())

    override val typeID: Byte
        get() = 9

    override fun encode(buf: ByteBuf) {
        buf.writeByte(tags[0].typeID.toInt())
        buf.writeInt(tags.size)
        for (tag in tags) {
            tag.encode(buf)
        }
    }
}