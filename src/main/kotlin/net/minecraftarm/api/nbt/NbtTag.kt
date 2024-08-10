package net.minecraftarm.api.nbt

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
}