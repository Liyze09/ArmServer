package io.github.liyze09.arms.network;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class Connection {
    @Contract("_ -> new")
    public static @NotNull Connection addConnection(ChannelHandlerContext ctx) {
        return new Connection(
                Objects.requireNonNull(ctx)
        );
    }

    public static @NotNull Connection getInstance(ChannelHandlerContext session) {
        Objects.requireNonNull(session);
        var connection = connections.get(session);
        if (connection == null) {
            connection = addConnection(session);
        }
        return connection;
    }

    public static ChannelFuture disconnect(ChannelHandlerContext session) {
        connections.remove(session);
        return session.close();
    }

    private static final Map<ChannelHandlerContext, Connection> connections = new ConcurrentHashMap<>();

    private Connection(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        connections.put(ctx, this);
    }

    public Status getStatus() {
        return this.status;
    }

    public synchronized void updateStatus(Status status) {
        Objects.requireNonNull(status);
        this.status = status;
    }

    public MainHand getMainHand() {
        if(mainHand == null) {
            throw new IllegalStateException();
        }
        return mainHand;
    }

    public void updateMainHand(MainHand mainHand) {
        Objects.requireNonNull(mainHand);
        this.mainHand = mainHand;
    }

    public boolean isAllowServerListings() {
        return allowServerListings;
    }

    public void updateAllowServerListings(boolean allowServerListings) {
        this.allowServerListings = allowServerListings;
    }

    public DisplayedSkinParts getDisplayedSkinParts() {
        if (displayedSkinParts == null) {
            throw new IllegalStateException();
        }
        return displayedSkinParts;
    }

    public void updateDisplayedSkinParts(DisplayedSkinParts displayedSkinParts) {
        Objects.requireNonNull(displayedSkinParts);
        this.displayedSkinParts = displayedSkinParts;
    }

    public boolean isChatColors() {
        return chatColors;
    }

    public void updateChatColors(boolean chatColors) {
        this.chatColors = chatColors;
    }

    public String getName() {
        if (displayedSkinParts == null) {
            throw new IllegalStateException();
        }
        return username;
    }

    public String getLocale() {
        if (locale == null) {
            throw new IllegalStateException();
        }
        return locale;
    }

    public void updateLocale(String locale) {
        Objects.requireNonNull(locale);
        this.locale = locale;
    }

    public byte getViewDistance() {
        if (viewDistance == -1) {
            throw new IllegalStateException();
        }
        return viewDistance;
    }

    public void updateViewDistance(byte viewDistance) {
        if (viewDistance < 0) throw new IllegalArgumentException();
        this.viewDistance = viewDistance;
    }

    public ChatMode getChatMode() {
        return chatMode;
    }

    public void updateChatMode(ChatMode chatMode) {
        Objects.requireNonNull(chatMode);
        this.chatMode = chatMode;
    }

    public int getProtocolVersion() {
        if (protocolVersion == -1) {
            throw new IllegalStateException();
        }
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        if (protocolVersion < 0) throw new IllegalArgumentException();
        if (this.protocolVersion != -1) throw new IllegalStateException();
        this.protocolVersion = protocolVersion;
    }

    public void setName(String username) {
        Objects.requireNonNull(username);
        if (this.username != null) throw new IllegalStateException();
        this.username = username;
    }

    public UUID getUUID() {
        if (uuid == null) {
            throw new IllegalStateException();
        }
        return uuid;
    }

    public void setUUID(UUID uuid) {
        Objects.requireNonNull(uuid);
        if (this.uuid != null) throw new IllegalStateException();
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Connection that)) return false;
        return Objects.equals(ctx, that.ctx);
    }

    @Override
    public int hashCode() {
        return ctx.hashCode();
    }

    @Override
    public String toString() {
        return "Connection{" +
                "context=" + ctx.name() +
                ", username='" + username +
                ", uuid=" + uuid.toString() +
                ", protocolVersion=" + protocolVersion +
                ", status=" + status +
                ", locale='" + locale +
                ", viewDistance=" + viewDistance +
                ", chatMode=" + chatMode +
                ", chatColors=" + chatColors +
                ", displayedSkinParts=" + displayedSkinParts +
                ", allowServerListings=" + allowServerListings +
                ", mainHand=" + mainHand +
                '}';
    }

    public enum Status {
        HANDSHAKE,
        LOGIN,
        PLAY,
        CONFIGURATION,
        STATUS
    }

    public record UUID(long a, long b) {
        @Override
        public @NotNull String toString() {
            return String.format("%016x%016x", a, b);
        }
    }

    public enum ChatMode {
        ENABLED,
        COMMANDS_ONLY,
        HIDDEN
    }

    public record DisplayedSkinParts(boolean cape,
                                     boolean jacket,
                                     boolean leftSleeve,
                                     boolean rightSleeve,
                                     boolean leftPantsLeg,
                                     boolean rightPantsLeg,
                                     boolean hat
    ) {
    }

    public enum MainHand {
        LEFT,
        RIGHT
    }

    public final ChannelHandlerContext ctx;
    private String username = null;
    private UUID uuid = null;
    private int protocolVersion = -1;
    private Status status = Status.HANDSHAKE;
    private String locale = null;
    private byte viewDistance = -1;
    private ChatMode chatMode = null;
    private boolean chatColors = false;
    private DisplayedSkinParts displayedSkinParts = null;
    private MainHand mainHand = null;
    private boolean allowServerListings = true;
}
