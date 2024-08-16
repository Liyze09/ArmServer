package io.github.liyze09.arms.network

import io.github.liyze09.arms.GlobalConfiguration
import io.github.liyze09.arms.network.Connection.Companion.getInstance
import io.github.liyze09.arms.network.PackUtils.getVarIntLength
import io.github.liyze09.arms.network.PackUtils.readVarInt
import io.github.liyze09.arms.network.PackUtils.writeVarInt
import io.github.liyze09.arms.network.packet.Packet
import io.github.liyze09.arms.network.packet.PacketCodecManager
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.ByteToMessageCodec
import org.tinylog.kotlin.Logger
import java.util.zip.Deflater
import java.util.zip.Inflater

object NettyInitialize {
    const val MIN_PROTOCOL_VERSION: Int = 767
    val LOGGER = Logger.tag("Network")

    @JvmField
    val bossGroup: NioEventLoopGroup = NioEventLoopGroup()

    @JvmField
    val workerGroup: NioEventLoopGroup = NioEventLoopGroup()
    val server: ServerBootstrap = ServerBootstrap()

    @JvmStatic
    fun start(): ChannelFuture {
        return server.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(object : ChannelInitializer<Channel>() {
                override fun initChannel(ch: Channel) {
                    val pipeline = ch.pipeline()
                    pipeline.addLast(object : ByteToMessageCodec<Packet>() {
                        @Suppress("SpellCheckingInspection")
                        override fun encode(channelHandlerContext: ChannelHandlerContext, o: Packet, byteBuf: ByteBuf) {
                            if (GlobalConfiguration.instance.compressThreshold >= 0) {
                                if (o.length >= GlobalConfiguration.instance.compressThreshold) {
                                    val buf = channelHandlerContext.alloc().heapBuffer(o.length)
                                    /* Packet ID */buf.writeVarInt(o.id)
                                    /* Data */buf.writeBytes(o.data)
                                    val deflater = Deflater()
                                    val nioBuffer = buf.nioBuffer()
                                    deflater.setInput(nioBuffer)
                                    deflater.deflate(nioBuffer)
                                    deflater.end()
                                    val buf2 = channelHandlerContext.alloc().buffer()
                                    /* Packet Length */buf2.writeVarInt(getVarIntLength(o.length) + (buf.capacity() - buf.writableBytes()))
                                    /* Data Length */buf2.writeVarInt(o.length)
                                    buf2.writeBytes(buf)
                                    buf.release()
                                    channelHandlerContext.channel().writeAndFlush(buf2)
                                } else {
                                    val buf = channelHandlerContext.alloc().buffer(o.length + 6)
                                    buf.writeVarInt(o.length + 1)
                                    buf.writeVarInt(0)
                                    buf.writeVarInt(o.id)
                                    buf.writeBytes(o.data)
                                    channelHandlerContext.channel().writeAndFlush(buf)
                                }
                            } else {
                                val buf = channelHandlerContext.alloc().buffer(o.length + 5)
                                buf.writeVarInt(o.length)
                                buf.writeVarInt(o.id)
                                buf.writeBytes(o.data)
                                channelHandlerContext.channel().writeAndFlush(buf)
                            }
                        }

                        @Suppress("SpellCheckingInspection")
                        override fun decode(
                            channelHandlerContext: ChannelHandlerContext,
                            byteBuf: ByteBuf,
                            list: MutableList<Any>
                        ) {
                            while (byteBuf.isReadable) {
                                if (GlobalConfiguration.instance.compressThreshold >= 0) {
                                    val length = byteBuf.readVarInt()
                                    val dataLength = byteBuf.readVarInt()
                                    if (dataLength == 0) {
                                        val id = byteBuf.readVarInt()
                                        val data =
                                            byteBuf.readBytes(length - getVarIntLength(id) - getVarIntLength(dataLength))
                                        val packet = Packet(length, id, data)
                                        LOGGER.trace("{}: {}", channelHandlerContext.name(), packet)
                                        list.add(packet)
                                    } else {
                                        val buf = channelHandlerContext.alloc().heapBuffer(dataLength)
                                        val buf2 = channelHandlerContext.alloc().heapBuffer()
                                        byteBuf.readBytes(buf, dataLength)
                                        val inflater = Inflater()
                                        inflater.setInput(buf.nioBuffer())
                                        inflater.inflate(buf2.nioBuffer())
                                        inflater.end()
                                        buf.release()
                                        val id = buf2.readVarInt()
                                        val data = buf2.readBytes(dataLength - getVarIntLength(id))
                                        val packet = Packet(dataLength, id, data)
                                        buf2.release()
                                        LOGGER.trace("{}: {}", channelHandlerContext.name(), packet)
                                        list.add(packet)
                                    }
                                } else {
                                    val length = byteBuf.readVarInt()
                                    val id = byteBuf.readVarInt()
                                    val data = byteBuf.readBytes(length - getVarIntLength(id))
                                    val packet = Packet(length, id, data)
                                    LOGGER.trace("{}: {}", channelHandlerContext.name(), packet)
                                    list.add(packet)
                                }
                            }
                        }
                    })
                    pipeline.addLast(object : SimpleChannelInboundHandler<Packet>() {
                        override fun channelRead0(ctx: ChannelHandlerContext, packet: Packet) {
                            val connection = getInstance(ctx)
                            try {
                                PacketCodecManager.getServerDecoder(connection.getStatus(), packet.id)
                                    ?.decode(packet.data, connection)
                            } catch (e: Exception) {
                                LOGGER.warn(
                                    "Error while decoding packet {}\n Connection: {}\n Cause by: {}",
                                    packet,
                                    connection,
                                    e.stackTraceToString()
                                )
                                ctx.close()
                            } finally {
                                packet.data.release()
                            }
                        }
                    })
                }
            }).bind(GlobalConfiguration.instance.port)
    }
}
