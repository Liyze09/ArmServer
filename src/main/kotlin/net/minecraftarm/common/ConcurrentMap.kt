package net.minecraftarm.common

import java.util.concurrent.locks.StampedLock

class ConcurrentMap<K, V>(val base: MutableMap<K, V>) : MutableMap<K, V> {
    private val lock = StampedLock()
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = base.entries
    override val keys: MutableSet<K>
        get() = base.keys
    override val size: Int
        get() = base.size
    override val values: MutableCollection<V>
        get() = base.values

    override fun clear() {
        val stamp = lock.writeLock()
        try {
            base.clear()
        } finally {
            lock.unlockWrite(stamp)
        }
    }

    override fun isEmpty(): Boolean {
        val stamp = lock.tryOptimisticRead()
        val ret = base.isEmpty()
        if (!lock.validate(stamp)) {
            val rl = lock.readLock()
            try {
                return base.isEmpty()
            } finally {
                lock.unlockRead(rl)
            }
        }
        return ret
    }

    override fun remove(key: K): V? {
        val stamp = lock.writeLock()
        try {
            return base.remove(key)
        } finally {
            lock.unlockWrite(stamp)
        }
    }

    override fun putAll(from: Map<out K, V>) {
        val stamp = lock.writeLock()
        try {
            base.putAll(from)
        } finally {
            lock.unlockWrite(stamp)
        }
    }

    override fun put(key: K, value: V): V? {
        val stamp = lock.writeLock()
        try {
            return base.put(key, value)
        } finally {
            lock.unlockWrite(stamp)
        }
    }

    override fun get(key: K): V? {
        val stamp = lock.tryOptimisticRead()
        val ret = base[key]
        if (!lock.validate(stamp)) {
            val rl = lock.readLock()
            try {
                return base[key]
            } finally {
                lock.unlockRead(rl)
            }
        }
        return ret
    }

    override fun containsValue(value: V): Boolean {
        val stamp = lock.tryOptimisticRead()
        val ret = base.containsValue(value)
        if (!lock.validate(stamp)) {
            val rl = lock.readLock()
            try {
                return base.containsValue(value)
            } finally {
                lock.unlockRead(rl)
            }
        }
        return ret
    }

    override fun containsKey(key: K): Boolean {
        val stamp = lock.tryOptimisticRead()
        val ret = base.containsKey(key)
        if (!lock.validate(stamp)) {
            val rl = lock.readLock()
            try {
                return base.containsKey(key)
            } finally {
                lock.unlockRead(rl)
            }
        }
        return ret
    }

    companion object {
        @JvmStatic
        fun <K, V> MutableMap<K, V>.toConcurrentMap(): ConcurrentMap<K, V> {
            return ConcurrentMap(this)
        }
    }
}