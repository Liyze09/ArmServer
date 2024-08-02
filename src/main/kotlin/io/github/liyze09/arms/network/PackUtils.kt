package io.github.liyze09.arms.network

import io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder
import io.netty.buffer.ByteBuf
import org.jetbrains.annotations.Contract
import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream
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

    fun <T> sendPacket(connection: Connection, msg: T, encoder: ClientBoundPacketEncoder<T>) {
        try {
            connection.ctx.writeAndFlush(encoder.encode(msg, connection))
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun writeString(msg: String, buffer: ByteBuf) {
        val bytes = msg.toByteArray(StandardCharsets.UTF_8)
        writeVarInt(bytes.size, buffer)
        buffer.writeBytes(bytes)
    }

    @Contract("_ -> new")
    fun readString(input: ByteBuf): String {
        val length = readVarInt(input)

        val data = ByteArray(length)
        input.readBytes(data)

        return String(data, StandardCharsets.UTF_8)
    }

    fun readVarInt(input: ByteBuf): Int {
        var value = 0
        var position = 0
        var currentByte: Byte

        while (true) {
            currentByte = input.readByte()
            value = value or ((currentByte.toInt() and SEGMENT_BITS) shl position)

            if ((currentByte.toInt() and CONTINUE_BIT) == 0) break

            position += 7

            if (position >= 32) throw VarIntTooBigException()
        }

        return value
    }

    @Throws(IOException::class)
    fun writeVarInt(input: Int, out: OutputStream) {
        var value = input
        val output = DataOutputStream(out)
        while (true) {
            if ((value and SEGMENT_BITS.inv()) == 0) {
                output.writeByte(value.toByte().toInt())
                return
            }

            output.writeByte(
                ((value and SEGMENT_BITS) or CONTINUE_BIT).toByte()
                    .toInt()
            )

            value = value ushr 7
        }
    }

    fun writeVarInt(input: Int, out: ByteBuf) {
        var value = input
        while (true) {
            if ((value and SEGMENT_BITS.inv()) == 0) {
                out.writeByte(value.toByte().toInt())
                return
            }

            out.writeByte(
                ((value and SEGMENT_BITS) or CONTINUE_BIT).toByte()
                    .toInt()
            )

            value = value ushr 7
        }
    }

    class VarIntTooBigException : RuntimeException("VarInt is too big or invalid")
}
