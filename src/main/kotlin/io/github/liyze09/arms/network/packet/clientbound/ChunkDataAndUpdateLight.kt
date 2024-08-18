package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.PackUtils.writeVarInt
import io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder
import io.github.liyze09.arms.network.packet.Packet
import net.minecraftarm.nbt.NbtCompound
import net.minecraftarm.nbt.NbtLongArray
import net.minecraftarm.world.Chunk

object ChunkDataAndUpdateLight : ClientBoundPacketEncoder<Chunk> {
    override fun encode(msg: Chunk, connection: Connection): Packet {
        val buf = connection.ctx.alloc().buffer()
        buf.writeInt(msg.x)
        buf.writeInt(msg.z)
        NbtCompound(
            "MOTION_BLOCKING" to NbtLongArray(msg.getMotionBlocking()),
            "WORLD_SURFACE" to NbtLongArray(msg.getWorldSurface())
        ).encodeAsRoot(buf)
        val sectionsBuf = connection.ctx.alloc().heapBuffer()
        msg.sections().forEach { chunk ->
            sectionsBuf.writeShort(chunk.getBlockCount())
            val palette = mutableSetOf<Int>()
            for (id in chunk.getBlockArray()) {
                palette.add(id)
            }
            val bitPerEntry = when {
                palette.size <= 2 -> 1
                palette.size <= 4 -> 2
                palette.size <= 8 -> 3
                palette.size <= 16 -> 4
                palette.size <= 32 -> 5
                palette.size <= 64 -> 6
                palette.size <= 128 -> 7
                palette.size <= 256 -> 8
                else -> 15
            }
            val array: IntArray?
            if (bitPerEntry == 15) {
                array = chunk.getBlockArray()
            } else {
                sectionsBuf.writeVarInt(bitPerEntry)
                sectionsBuf.writeVarInt(palette.size)
                for (id in palette) {
                    sectionsBuf.writeVarInt(id)
                }
                array = IntArray(4096)
                for ((index, id) in chunk.getBlockArray().withIndex()) {
                    array[index] = id
                }
            }
            val dataBuf = connection.ctx.alloc().heapBuffer(bitPerEntry * 4096)
            val data = LongArray(4096 / (64 / bitPerEntry))
            array.forEachIndexed { index, id ->
                data[index / (64 / bitPerEntry)] =
                    data[index / (64 / bitPerEntry)] or (id.toLong() shl (index % (64 / bitPerEntry) * bitPerEntry))
            }
            for (i in data.indices) {
                dataBuf.writeLong(data[i])
            }
            sectionsBuf.writeVarInt(data.size)
            sectionsBuf.writeBytes(dataBuf)
            dataBuf.release()

            // TODO Biomes
        }
        buf.writeVarInt(sectionsBuf.writerIndex())
        buf.writeBytes(sectionsBuf)
        sectionsBuf.release()
        buf.writeVarInt(0) // TODO Block Entities
        TODO("Not yet implemented")
    }
}