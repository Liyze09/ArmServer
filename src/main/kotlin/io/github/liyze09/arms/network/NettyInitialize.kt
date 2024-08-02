package io.github.liyze09.arms.network

import io.github.liyze09.arms.Configuration
import io.github.liyze09.arms.network.Connection.Companion.getInstance
import io.github.liyze09.arms.network.packet.Packet
import io.github.liyze09.arms.network.packet.PacketCodecManager
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.ByteToMessageCodec

object NettyInitialize {
    const val MIN_PROTOCOL_VERSION: Int = 767

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
                            PackUtils.writeVarInt(o.length, buf)
                            PackUtils.writeVarInt(o.id, buf)
                            buf.writeBytes(o.data)
                            channelHandlerContext.channel().writeAndFlush(buf)
                        }

                        override fun decode(
                            channelHandlerContext: ChannelHandlerContext,
                            byteBuf: ByteBuf,
                            list: MutableList<Any>
                        ) {
                            while (byteBuf.isReadable) {
                                val length = PackUtils.readVarInt(byteBuf)
                                val id = PackUtils.readVarInt(byteBuf)
                                val data = byteBuf.readBytes(length - PackUtils.getVarIntLength(id))
                                list.add(Packet(length, id, data))
                            }
                        }
                    })
                    pipeline.addLast(object : SimpleChannelInboundHandler<Packet>() {
                        override fun channelRead0(ctx: ChannelHandlerContext, packet: Packet) {
                            val connection = getInstance(ctx)
                            PacketCodecManager.getServerDecoder(connection.status, packet.id)
                                ?.decode(packet.data, connection)
                        }
                    })
                }
            }).bind(Configuration.instance.port)
    }
}
