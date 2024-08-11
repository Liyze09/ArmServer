package net.minecraftarm.nbt

import io.netty.buffer.ByteBuf

class NbtArray<T : NbtTag>(val tags: List<T>, val type: NbtTag.NbtType) : NbtTag {
    constructor(tags: List<T>) : this(tags, NbtTag.NbtType.fromId(tags[0].typeID))
    constructor(vararg tags: T) : this(tags.toList())
    constructor(type: NbtTag.NbtType) : this(emptyList(), type)

    override val typeID: Byte
        get() = 9

    override fun encode(buf: ByteBuf) {
        if (tags.isEmpty()) {
            buf.writeByte(type.id.toInt())
            buf.writeInt(0)
            return
        }
        buf.writeByte(tags[0].typeID.toInt())
        buf.writeInt(tags.size)
        for (tag in tags) {
            tag.encode(buf)
        }
    }
}