package io.github.liyze09.arms.network;

import io.github.liyze09.arms.Configuration;
import io.github.liyze09.arms.network.packet.Packet;
import io.github.liyze09.arms.network.packet.PacketCodecManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

import static io.github.liyze09.arms.network.PackUtils.*;

public class NettyInitialize {
    public static final int MIN_PROTOCOL_VERSION = 757;
    public static final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    public static final NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    public static final ServerBootstrap server = new ServerBootstrap();
    public static ChannelFuture start() {
        return server.group(bossGroup, workerGroup)
                .channel(io.netty.channel.socket.nio.NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        var pipeline = ch.pipeline();
                        pipeline.addLast(new ByteToMessageCodec<Packet>() {
                            @Override
                            protected void encode(ChannelHandlerContext channelHandlerContext, Packet o, ByteBuf byteBuf) {
                                var buf = channelHandlerContext.alloc().buffer(o.length() + 5);
                                writeVarInt(o.length(), buf);
                                writeVarInt(o.id(), buf);
                                buf.writeBytes(o.data());
                                channelHandlerContext.channel().writeAndFlush(buf);
                            }

                            @Override
                            protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
                                while (byteBuf.isReadable()) {
                                    var length = PackUtils.readVarInt(byteBuf);
                                    var id = readVarInt(byteBuf);
                                    var data = byteBuf.readBytes(length - getVarIntLength(id));
                                    list.add(new Packet(length, id, data));
                                }

                            }
                        });
                        pipeline.addLast(new SimpleChannelInboundHandler<Packet>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
                                var connection = Connection.getInstance(ctx);
                                var processor = PacketCodecManager.getInstance()
                                        .getServerDecoder(connection.getStatus(), packet.id());
                                if (processor != null) {
                                    processor.decode(packet.data(), connection);
                                }
                            }
                        });
                    }
                }).bind(Configuration.getInstance().port);
    }

}
