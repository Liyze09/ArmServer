package io.github.liyze09.arms.network;

import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;

public class MinecraftProtocol implements Protocol<Packet> {
    @Override
    public Packet decode(ByteBuffer byteBuffer, AioSession aioSession) {
        return Packet.parsePacket(byteBuffer);
    }
}
