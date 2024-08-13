package io.github.liyze09.arms.network.packet

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.NettyInitialize
import io.github.liyze09.arms.network.PackUtils
import io.github.liyze09.arms.network.PackUtils.readString
import io.github.liyze09.arms.network.PackUtils.readVarInt
import io.github.liyze09.arms.network.packet.serverbound.*
import io.netty.buffer.ByteBuf
import io.netty.util.collection.IntObjectHashMap
import net.minecraftarm.common.ConcurrentMap
import org.jetbrains.annotations.Contract

object PacketCodecManager {
    private fun registerServerBoundPacket(
        type: Connection.Status,
        id: Int,
        decoder: ServerBoundPacketDecoder
    ) {
        getServerMap(type)[id] = decoder
    }


    fun getServerDecoder(
        type: Connection.Status,
        id: Int
    ): ServerBoundPacketDecoder? {
        if (type == Connection.Status.HANDSHAKE) {
            return handshakeHandler
        }
        return getServerMap(type)[id]
    }

    private val serverLogin: MutableMap<Int, ServerBoundPacketDecoder> =
        ConcurrentMap(IntObjectHashMap())
    private val serverConfiguration: MutableMap<Int, ServerBoundPacketDecoder> =
        ConcurrentMap(IntObjectHashMap())
    private val serverPlay: MutableMap<Int, ServerBoundPacketDecoder> =
        ConcurrentMap(IntObjectHashMap())
    private val serverStatus: MutableMap<Int, ServerBoundPacketDecoder> =
        ConcurrentMap(IntObjectHashMap())

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
        registerServerBoundPacket(
            Connection.Status.LOGIN, 0x00,
            LoginStart
        )
        registerServerBoundPacket(Connection.Status.LOGIN, 0x03, LoginAcknowledged)
        registerServerBoundPacket(Connection.Status.CONFIGURATION, 0x00, ClientInformation)
        registerServerBoundPacket(Connection.Status.CONFIGURATION, 0x02, PluginMessage)
        registerServerBoundPacket(
            Connection.Status.CONFIGURATION, 0x03,
            AcknowledgeFinishConfiguration
        )
        registerServerBoundPacket(Connection.Status.CONFIGURATION, 0x07, ServerBoundKnownPacks)
        registerServerBoundPacket(Connection.Status.PLAY, 0x12, PluginMessage)
        registerServerBoundPacket(Connection.Status.PLAY, 0x00, ConfirmTeleportation)
    }


    private val handshakeHandler: ServerBoundPacketDecoder =
        ServerBoundPacketDecoder { buf: ByteBuf, connection: Connection ->
            val protocolVersion = buf.readVarInt()
            if (!PackUtils.checkProtocolVersion(protocolVersion, NettyInitialize.MIN_PROTOCOL_VERSION)) {
                throw io.github.liyze09.arms.network.exception.IllegalPacketException("Invalid protocol version: $protocolVersion")
            }
            connection.protocolVersion = protocolVersion // Protocol Version
            buf.readString(255) // Server Address (unused)
            buf.readUnsignedShort() // Server Port (unused)
            when (buf.readVarInt()) {
                2 -> connection.updateStatus(Connection.Status.LOGIN)
                1 -> connection.updateStatus(Connection.Status.STATUS)
                else -> throw io.github.liyze09.arms.network.exception.IllegalPacketException("Invalid next state of handshake packet")
            }
        }
}
