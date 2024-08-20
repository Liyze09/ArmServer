package io.github.liyze09.arms.network

import io.netty.buffer.ByteBuf
import net.minecraftarm.common.Identifier
import org.jetbrains.annotations.Contract
import java.nio.charset.StandardCharsets

object PackUtils {
    private const val SEGMENT_BITS = 0x7F
    private const val CONTINUE_BIT = 0x80
    fun checkProtocolVersion(protocolVersion: Int, minVersion: Int): Boolean {
        return protocolVersion in minVersion..0x3fffffff
    }


    fun getVarIntLength(varInt: Int): Int {
        return if (varInt < 0) {
            5
        } else if (varInt <= 127) {
            1
        } else if (varInt <= 16383) {
            2
        } else if (varInt <= 2097151) {
            3
        } else if (varInt <= 268435455) {
            4
        } else {
            5
        }
    }

    fun ByteBuf.writeString(msg: String): ByteBuf {
        val bytes = msg.toByteArray(StandardCharsets.UTF_8)
        this.writeVarInt(bytes.size)
        this.writeBytes(bytes)
        return this
    }

    @Contract("_ -> new")
    fun ByteBuf.readString(maxLength: Int = 32767): String {
        val length = this.readVarInt()
        if (length > maxLength) throw io.github.liyze09.arms.network.exception.IllegalPacketException("String length is too big: $length > $maxLength")

        val data = ByteArray(length)
        this.readBytes(data)

        return String(data, StandardCharsets.UTF_8)
    }

    fun ByteBuf.readIdentifier(): Identifier = Identifier(this.readString())

    fun ByteBuf.readVarInt(): Int {
        var value = 0
        var position = 0
        var currentByte: Byte

        while (true) {
            currentByte = this.readByte()
            value = value or ((currentByte.toInt() and SEGMENT_BITS) shl position)

            if ((currentByte.toInt() and CONTINUE_BIT) == 0) break

            position += 7

            if (position >= 32) throw VarIntTooBigException()
        }

        return value
    }

    fun ByteBuf.writeVarInt(input: Int): ByteBuf {
        var value = input
        while (true) {
            if ((value and SEGMENT_BITS.inv()) == 0) {
                this.writeByte(value.toByte().toInt())
                return this
            }

            this.writeByte(
                ((value and SEGMENT_BITS) or CONTINUE_BIT).toByte()
                    .toInt()
            )

            value = value ushr 7
        }
    }

    fun ByteBuf.getVarInt(): Pair<Int, Int> {
        var value = 0
        var position = 0
        var currentByte: Byte
        var index = this.readerIndex()

        while (true) {
            currentByte = this.getByte(index)
            value = value or ((currentByte.toInt() and SEGMENT_BITS) shl position)

            if ((currentByte.toInt() and CONTINUE_BIT) == 0) break

            position += 7
            index++

            if (position >= 32) throw VarIntTooBigException()
        }

        return Pair(value, index - this.readerIndex() + 1)
    }

    fun ByteBuf.readMCBoolean(): Boolean {
        return this.readByte().toInt() != 0
    }

    fun ByteBuf.writeMCBoolean(value: Boolean): ByteBuf {
        this.writeByte(if (value) 1 else 0)
        return this
    }

    class VarIntTooBigException : RuntimeException("VarInt is too big or invalid")
}
