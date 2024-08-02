package io.github.liyze09.arms.network

import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import org.jetbrains.annotations.Contract
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class Connection private constructor(@JvmField val ctx: ChannelHandlerContext) {
    @Synchronized
    fun updateStatus(status: Status) {
        Objects.requireNonNull(status)
        this.status = status
    }

    fun getMainHand(): MainHand {
        checkNotNull(mainHand)
        return mainHand as MainHand
    }

    fun updateMainHand(mainHand: MainHand) {
        Objects.requireNonNull(mainHand)
        this.mainHand = mainHand
    }

    fun updateAllowServerListings(allowServerListings: Boolean) {
        this.isAllowServerListings = allowServerListings
    }

    fun getDisplayedSkinParts(): DisplayedSkinParts {
        checkNotNull(displayedSkinParts)
        return displayedSkinParts as DisplayedSkinParts
    }

    fun updateDisplayedSkinParts(displayedSkinParts: DisplayedSkinParts) {
        Objects.requireNonNull(displayedSkinParts)
        this.displayedSkinParts = displayedSkinParts
    }

    fun updateChatColors(chatColors: Boolean) {
        this.isChatColors = chatColors
    }

    var name: String?
        get() {
            checkNotNull(displayedSkinParts)
            return username
        }
        set(username) {
            Objects.requireNonNull(username)
            check(this.username == null)
            this.username = username
        }

    fun getLocale(): String {
        checkNotNull(locale)
        return locale as String
    }

    fun updateLocale(locale: String) {
        Objects.requireNonNull(locale)
        this.locale = locale
    }

    fun getViewDistance(): Byte {
        check(viewDistance.toInt() != -1)
        return viewDistance
    }

    fun updateViewDistance(viewDistance: Byte) {
        require(viewDistance >= 0)
        this.viewDistance = viewDistance
    }

    fun updateChatMode(chatMode: ChatMode) {
        Objects.requireNonNull(chatMode)
        this.chatMode = chatMode
    }

    fun getProtocolVersion(): Int {
        check(protocolVersion != -1)
        return protocolVersion
    }

    fun setProtocolVersion(protocolVersion: Int) {
        require(protocolVersion >= 0)
        check(this.protocolVersion == -1)
        this.protocolVersion = protocolVersion
    }

    var uUID: UUID
        get() {
            checkNotNull(uuid)
            return uuid as UUID
        }
        set(uuid) {
            Objects.requireNonNull(uuid)
            check(this.uuid == null)
            this.uuid = uuid
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Connection) return false
        return ctx == other.ctx
    }

    override fun hashCode(): Int {
        return ctx.hashCode()
    }

    override fun toString(): String {
        return "Connection{" +
                "context=" + ctx.name() +
                ", username='" + username +
                ", uuid=" + uuid.toString() +
                ", protocolVersion=" + protocolVersion +
                ", status=" + status +
                ", locale='" + locale +
                ", viewDistance=" + viewDistance +
                ", chatMode=" + chatMode +
                ", chatColors=" + isChatColors +
                ", displayedSkinParts=" + displayedSkinParts +
                ", allowServerListings=" + isAllowServerListings +
                ", mainHand=" + mainHand +
                '}'
    }

    enum class Status {
        HANDSHAKE,
        LOGIN,
        PLAY,
        CONFIGURATION,
        STATUS
    }

    @JvmRecord
    data class UUID(val a: Long, val b: Long) {
        override fun toString(): String {
            return String.format("%016x%016x", a, b)
        }
    }

    enum class ChatMode {
        ENABLED,
        COMMANDS_ONLY,
        HIDDEN
    }

    @JvmRecord
    data class DisplayedSkinParts(
        val cape: Boolean,
        val jacket: Boolean,
        val leftSleeve: Boolean,
        val rightSleeve: Boolean,
        val leftPantsLeg: Boolean,
        val rightPantsLeg: Boolean,
        val hat: Boolean
    )

    enum class MainHand {
        LEFT,
        RIGHT
    }

    private var username: String? = null
    private var uuid: UUID? = null
    internal var protocolVersion = -1
    var status: Status = Status.HANDSHAKE
        private set
    private var locale: String? = null
    private var viewDistance: Byte = -1
    private var chatMode: ChatMode? = null
    private var isChatColors: Boolean = false
    private var displayedSkinParts: DisplayedSkinParts? = null
    private var mainHand: MainHand? = null
    private var isAllowServerListings: Boolean = true

    init {
        connections[ctx] = this
    }

    companion object {
        @Contract("_ -> new")
        fun addConnection(ctx: ChannelHandlerContext): Connection {
            return Connection(
                Objects.requireNonNull(ctx)
            )
        }

        @JvmStatic
        fun getInstance(session: ChannelHandlerContext): Connection {
            Objects.requireNonNull(session)
            var connection = connections[session]
            if (connection == null) {
                connection = addConnection(session)
            }
            return connection
        }

        fun disconnect(session: ChannelHandlerContext): ChannelFuture {
            connections.remove(session)
            return session.close()
        }

        private val connections: MutableMap<ChannelHandlerContext, Connection> = ConcurrentHashMap()
    }
}
