package io.github.liyze09.arms.network.packet.clientbound

import io.github.liyze09.arms.network.Connection
import io.github.liyze09.arms.network.packet.ClientBoundPacketEncoder
import io.github.liyze09.arms.network.packet.Packet

object GameEvent : ClientBoundPacketEncoder<Pair<GameEvent.GameEventBody, Float>> {
    override fun encode(msg: Pair<GameEventBody, Float>, connection: Connection): Packet {
        val buf = connection.ctx.alloc().buffer()
        buf.writeByte(msg.first.ordinal)
        buf.writeFloat(msg.second)
        return Packet.of(0x22, buf)
    }

    enum class GameEventBody {
        NO_RESPAWN_BLOCK_AVAILABLE,
        BEGIN_RAINING,
        END_RAINING,
        CHANGE_GAME_MODE,
        WIN_GAME,
        DEMO_EVENT,
        ARROW_HIT_PLAYER,
        RAIN_LEVEL_CHANGE,
        THUNDER_LEVEL_CHANGE,

        @Suppress("SpellCheckingInspection")
        PLAY_PUFFERFISH_STING_SOUND,
        PLAY_ELDER_GUARDIAN_MOB_APPEARANCE,
        ENABLE_RESPAWN_SCREEN,
        LIMITED_CRAFTING,
        START_WAITING_FOR_LEVEL_CHUNKS
    }
}