@file:JvmName("EventRelater")

package net.minecraftarm.api.event

import java.util.concurrent.locks.ReentrantReadWriteLock

object EventRelater {
    fun registerEventHandler(handler: (event: Any, message: Any) -> Unit) {
        try {
            lock.writeLock().lock()
            superEventHandlers.add(handler)
        } finally {
            lock.writeLock().unlock()
        }
    }

    fun registerEventHandler(handler: (message: Any) -> Unit, event: Any) {
        try {
            lock.writeLock().lock()
            eventHandlers.getOrPut(event) { mutableListOf() }.add(handler)
        } finally {
            lock.writeLock().unlock()
        }
    }

    fun broadcastEvent(event: Any, message: Any) {
        Thread.ofVirtual().start {
            try {
                lock.readLock().lock()
                superEventHandlers.forEach {
                    it.invoke(event, message)
                }
            } finally {
                lock.readLock().unlock()
            }
        }
        Thread.ofVirtual().start {
            try {
                lock.readLock().lock()
                eventHandlers[event]?.forEach {
                    it.invoke(message)
                }
            } finally {
                lock.readLock().unlock()
            }
        }
    }

    private val lock = ReentrantReadWriteLock()
    internal val eventHandlers = mutableMapOf<Any, MutableList<(Any) -> Unit>>()
    internal val superEventHandlers = mutableListOf<(Any, Any) -> Unit>()
}