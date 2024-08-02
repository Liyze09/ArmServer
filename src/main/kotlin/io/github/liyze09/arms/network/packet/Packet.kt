package io.github.liyze09.arms.network.packet

import io.github.liyze09.arms.network.PackUtils
import io.netty.buffer.ByteBuf
import org.jetbrains.annotations.Contract

@JvmRecord
data class Packet(@JvmField val length: Int, @JvmField val id: Int, @JvmField val data: ByteBuf) {
    companion object {
        @Contract("_, _ -> new")
        fun of(id: Int, data: ByteBuf): Packet {
            return Packet(data.readableBytes() + PackUtils.getVarIntLength(id), id, data)
        }
    }
}
