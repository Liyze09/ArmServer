package io.github.liyze09.arms.network

import io.github.liyze09.arms.common.Identifier
import io.netty.buffer.ByteBuf
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

    fun ByteBuf.writeString(msg: String) {
        val bytes = msg.toByteArray(StandardCharsets.UTF_8)
        this.writeVarInt(bytes.size)
        this.writeBytes(bytes)
    }

    @Contract("_ -> new")
    fun ByteBuf.readString(): String {
        val length = this.readVarInt()

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

    fun ByteBuf.writeVarInt(input: Int) {
        var value = input
        while (true) {
            if ((value and SEGMENT_BITS.inv()) == 0) {
                this.writeByte(value.toByte().toInt())
                return
            }

            this.writeByte(
                ((value and SEGMENT_BITS) or CONTINUE_BIT).toByte()
                    .toInt()
            )

            value = value ushr 7
        }
    }

    class VarIntTooBigException : RuntimeException("VarInt is too big or invalid")
}
