package io.github.liyze09.arms.network.packet.serverbound;

import io.github.liyze09.arms.network.Connection;
import io.github.liyze09.arms.network.packet.ServerBoundPacketDecoder;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import static io.github.liyze09.arms.network.PackUtils.readString;

public class ClientInformation implements ServerBoundPacketDecoder {
    @Override
    public void decode(ByteBuf buf, @NotNull Connection connection) {
        connection.updateLocale(readString(buf));
        connection.updateViewDistance(buf.readByte());
        connection.updateChatMode(Connection.ChatMode.values()[buf.readByte()]);
        connection.updateChatColors(buf.readByte() == 1);
        var displayedSkinParts = buf.readByte();
        connection.updateDisplayedSkinParts(new Connection.DisplayedSkinParts(
                (displayedSkinParts & 0x01) != 0,
                (displayedSkinParts & 0x02) != 0,
                (displayedSkinParts & 0x04) != 0,
                (displayedSkinParts & 0x08) != 0,
                (displayedSkinParts & 0x10) != 0,
                (displayedSkinParts & 0x20) != 0,
                (displayedSkinParts & 0x40) != 0
        ));
        connection.updateMainHand(Connection.MainHand.values()[buf.readByte()]);
        buf.readByte();
        connection.updateAllowServerListings(buf.readByte() == 1);
    }
}
