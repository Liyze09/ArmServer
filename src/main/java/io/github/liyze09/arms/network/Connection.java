package io.github.liyze09.arms.network;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.smartboot.socket.transport.AioSession;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public final class Connection {
    private static final Map<AioSession, Connection> connections = new ConcurrentHashMap<>();
    public final AioSession session;
    private volatile String username = null;
    private volatile UUID uuid = null;
    private volatile int protocolVersion = -1;
    private volatile Status status = Status.HANDSHAKE;
    private static final ReentrantLock lock0 = new ReentrantLock();
    private final ReentrantLock lock = new ReentrantLock();
    private Connection(AioSession session) {
        this.session = session;
        connections.put(session, this);
    }

    @Contract("_ -> new")
    public static @NotNull Connection addConnection(AioSession session) {
        try {
            lock0.lock();
            return new Connection(
                    Objects.requireNonNull(session)
            );
        } finally {
            lock0.unlock();
        }
    }

    public static @NotNull Connection getInstance(AioSession session) {
        try {
            lock0.lock();
            Objects.requireNonNull(session);
            var connection = connections.get(session);
            if (connection == null) {
                connection = addConnection(session);
            }
            return connection;
        } finally {
            lock0.unlock();
        }
    }

    public static void disconnect(AioSession session) {
        try {
            lock0.lock();
            connections.remove(session);
            session.close();
        } finally {
            lock0.unlock();
        }
    }


    public synchronized void updateStatus(Status status) {
        Objects.requireNonNull(status);
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    public String getName() {
        return username;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        if (protocolVersion < 0) throw new IllegalArgumentException();
        if (this.protocolVersion != -1) throw new UnsupportedOperationException();
        this.protocolVersion = protocolVersion;
    }

    public void setName(String username) {
        Objects.requireNonNull(username);
        if (this.username != null) throw new UnsupportedOperationException();
        this.username = username;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        Objects.requireNonNull(uuid);
        if (this.uuid != null) throw new UnsupportedOperationException();
        this.uuid = uuid;
    }
    public synchronized void lock() {
        lock.lock();
    }
    public synchronized void unlock() {
        lock.unlock();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Connection that)) return false;
        return Objects.equals(session, that.session);
    }

    @Override
    public int hashCode() {
        return session.hashCode();
    }

    public enum Status {
        HANDSHAKE,
        LOGIN,
        PLAY,
        CONFIGURATION,
        STATUS
    }

    public record UUID(long a, long b) {
    }
}
