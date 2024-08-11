package net.minecraftarm.nbt

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator

interface NbtTag {
    val typeID: Byte
    fun encode(buf: ByteBuf)
    fun encodeToByteArray(): ByteArray {
        val buf = ByteBufAllocator.DEFAULT.heapBuffer()
        try {
            encode(buf)
            return buf.array()
        } finally {
            buf.release()
        }
    }
    enum class NbtType(val id: Byte) {
        END(0),
        BYTE(1),
        SHORT(2),
        INT(3),
        LONG(4),
        FLOAT(5),
        DOUBLE(6),
        BYTE_ARRAY(7),
        STRING(8),
        LIST(9),
        COMPOUND(10),
        INT_ARRAY(11),
        LONG_ARRAY(12);

        companion object {
            fun fromId(id: Byte): NbtType {
                return entries.first { it.id == id }
            }
        }
    }
}