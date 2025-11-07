package io.github.smyrgeorge.sqlx4k.impl.hook

import io.github.smyrgeorge.sqlx4k.SQLError
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.concurrent.atomics.AtomicReference
import kotlin.reflect.KClass
import kotlin.time.measureTime

class MutableEventBus<T : Any>(val parentEventBus: MutableEventBus<T>? = null) : EventBus<T> {
    private val listenersMutex = Mutex()
    private val listeners = AtomicReference(mapOf<KClass<*>, Set<EventBus.Subscriber<*>>>())


    override suspend fun <K : T> subscribe(
        firstType: KClass<K>,
        vararg otherTypes: KClass<K>,
        handler: EventBus.Subscriber<K>
    ) {
        if (!enableGlobally)
            error("Event bus is not enabled globally. Please enable it by setting the `MutableEventBus.enableGlobally` flag to true.")
        val eventTypes = otherTypes.toSet() + firstType
        listenersMutex.withLock {
            val newMap = listeners.load().toMutableMap()
            for (eventType in eventTypes)
                newMap[eventType] = newMap[eventType]?.apply { plus(handler) } ?: mutableSetOf(handler)
            listeners.exchange(newMap)
        }
        try {
            // Keep the subscription active for the lifetime of the caller.
            awaitCancellation()
        } finally {
            listenersMutex.withLock {
                val newMap = listeners.load().toMutableMap()
                for ((eventType, listeners) in eventTypes.mapNotNull { newMap[it]?.let { l -> it to l } }) {
                    if (listeners.size <= 1)
                        newMap.remove(eventType)
                    else
                        newMap[eventType] = listeners.minus(handler)
                }
            }
        }
    }

    /**
     * Publish an event and wait for all current subscribers to complete.
     * Fail-fast: if any handler throws, siblings are cancelled, and the exception is rethrown.
     */
    suspend inline fun <reified K : T> publish(noinline lazyValue: suspend () -> K) =
        publish(K::class, lazyValue)


    /**
     * Publish an event and wait for all current subscribers to complete.
     * Fail-fast: if any handler throws, siblings are cancelled, and the exception is rethrown.
     */
    suspend fun <K : T> publish(type: KClass<K>, lazyValue: suspend () -> K) {
        if (!enableGlobally) return
        val value: K = lazyValue()
        val getLazyValue = suspend {
//            if (value == null)
//                value = lazyValue()
            value
        }
        var listeners: Set<EventBus.Subscriber<*>>? = null

        measureTime {
            listeners = this.listeners.load().filter { it.key.isInstance(value) }.flatMap { it.value }.toSet()
            if (listeners.isEmpty()) {
                parentEventBus?.publish(type, lazyValue)
                return
            }
        }.let { println("lookup took: ${it.inWholeNanoseconds} ns") }


        try {
            coroutineScope {
                parentEventBus?.let { launch { it.publish(type, getLazyValue) } }
                for (handler in listeners!!) {
                    launch {
                        @Suppress("UNCHECKED_CAST")
                        (handler as EventBus.Subscriber<T>).invoke(getLazyValue())
                    }
                }
            }
        } catch (e: Throwable) {
            throw SQLError(SQLError.Code.HookExecutionFailed, "At least one hook handler failed", e)
        }
    }


    companion object {
        /**
         * Enables the event bus globally. Be aware that this might come with a significant performance penalty.
         */
        var enableGlobally = false
    }
}