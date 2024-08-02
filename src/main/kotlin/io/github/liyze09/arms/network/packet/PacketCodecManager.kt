package io.github.liyze09.arms.network.packet

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.NettyInitialize
import io.github.liyze09.arms.network.PackUtils
import io.github.liyze09.arms.network.exception.IllegalPacketException
import io.github.liyze09.arms.network.packet.serverbound.ClientInformation
import io.github.liyze09.arms.network.packet.serverbound.LoginAcknowledged
import io.github.liyze09.arms.network.packet.serverbound.LoginStart
import io.netty.buffer.ByteBuf
import org.jetbrains.annotations.Contract
import java.util.concurrent.ConcurrentHashMap

object PacketCodecManager {
    private fun registerServerBoundPacket(type: Connection.Status, id: Int, decoder: ServerBoundPacketDecoder) {
        getServerMap(type)[id] = decoder
    }


    fun getServerDecoder(type: Connection.Status, id: Int): ServerBoundPacketDecoder? {
        if (type == Connection.Status.HANDSHAKE) {
            return handshakeHandler
        }
        return getServerMap(type)[id]
    }

    private val serverLogin: MutableMap<Int, ServerBoundPacketDecoder> = ConcurrentHashMap()
    private val serverConfiguration: MutableMap<Int, ServerBoundPacketDecoder> = ConcurrentHashMap()
    private val serverPlay: MutableMap<Int, ServerBoundPacketDecoder> = ConcurrentHashMap()
    private val serverStatus: MutableMap<Int, ServerBoundPacketDecoder> = ConcurrentHashMap()

    @Contract(pure = true)
    private fun getServerMap(type: Connection.Status): MutableMap<Int, ServerBoundPacketDecoder> {
        return when (type) {
            Connection.Status.LOGIN -> serverLogin
            Connection.Status.CONFIGURATION -> serverConfiguration
            Connection.Status.PLAY -> serverPlay
            Connection.Status.STATUS -> serverStatus
            else -> throw IllegalStateException("Unexpected value: $type")
        }
    }

    fun registerPackets() {
        registerServerBoundPacket(Connection.Status.LOGIN, 0x00, LoginStart())
        registerServerBoundPacket(Connection.Status.LOGIN, 0x03, LoginAcknowledged())
        registerServerBoundPacket(Connection.Status.CONFIGURATION, 0x00, ClientInformation())
    }


    private val handshakeHandler: ServerBoundPacketDecoder =
        ServerBoundPacketDecoder { buf: ByteBuf, connection: Connection ->
            val protocolVersion = PackUtils.readVarInt(buf)
            if (!PackUtils.checkProtocolVersion(protocolVersion, NettyInitialize.MIN_PROTOCOL_VERSION)) {
                throw IllegalPacketException("Invalid protocol version: $protocolVersion")
            }
            connection.protocolVersion = protocolVersion // Protocol Version
            PackUtils.readString(buf) // Server Address (unused)
            buf.readShort() // Server Port (unused)
            when (PackUtils.readVarInt(buf)) {
                2 -> connection.updateStatus(Connection.Status.LOGIN)
                1 -> connection.updateStatus(Connection.Status.STATUS)
                else -> throw IllegalPacketException("Invalid next state: " + PackUtils.readVarInt(buf))

            }
        }
}
