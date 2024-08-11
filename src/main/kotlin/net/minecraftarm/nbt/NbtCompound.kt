package net.minecraftarm.nbt

import io.netty.buffer.ByteBuf
import net.minecraftarm.common.Identifier

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

    companion object {
        fun of(vararg tags: Pair<String, Any>): NbtCompound {
            val nbtTags = ArrayList<Pair<String, NbtTag>>(tags.size)
            for (tag in tags) {
                nbtTags.add(
                    when (tag.second) {
                        is Byte -> tag.first to NbtByte(tag.second as Byte)
                        is Short -> tag.first to NbtShort(tag.second as Short)
                        is Int -> tag.first to NbtInt(tag.second as Int)
                        is Long -> tag.first to NbtLong(tag.second as Long)
                        is Float -> tag.first to NbtFloat(tag.second as Float)
                        is Double -> tag.first to NbtDouble(tag.second as Double)
                        is String -> tag.first to NbtString(tag.second as String)
                        is ByteArray -> tag.first to NbtByteArray(tag.second as ByteArray)
                        is IntArray -> tag.first to NbtIntArray(tag.second as IntArray)
                        is LongArray -> tag.first to NbtLongArray(tag.second as LongArray)
                        is NbtTag -> tag.first to tag.second as NbtTag
                        is Boolean -> tag.first to NbtByte(if (tag.second as Boolean) 1 else 0)
                        is Identifier -> tag.first to NbtString(tag.second.toString())
                        else -> {
                            throw IllegalArgumentException("Unsupported type: ${tag.second::class.java.name}")
                        }
                    }
                )
            }
            return NbtCompound(*nbtTags.toTypedArray())
        }
    }
}