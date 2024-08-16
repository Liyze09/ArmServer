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
                        override fun encode(channelHandlerContext: ChannelHandlerContext, o: Packet, byteBuf: ByteBuf) {
                            val buf = channelHandlerContext.alloc().buffer(o.length + 5)
                            buf.writeVarInt(o.length)
                            buf.writeVarInt(o.id)
                            buf.writeBytes(o.data)
                            channelHandlerContext.channel().writeAndFlush(buf)
                        }

                        override fun decode(
                            channelHandlerContext: ChannelHandlerContext,
                            byteBuf: ByteBuf,
                            list: MutableList<Any>
                        ) {
                            while (byteBuf.isReadable) {
                                val length = byteBuf.readVarInt()
                                val id = byteBuf.readVarInt()
                                val data = byteBuf.readBytes(length - getVarIntLength(id))
                                val packet = Packet(length, id, data)
                                LOGGER.trace("{}: {}", channelHandlerContext.name(), packet)
                                list.add(packet)
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
