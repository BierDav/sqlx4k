package io.github.smyrgeorge.sqlx4k.impl.hook

import io.github.smyrgeorge.sqlx4k.impl.hook.EventBus.Subscriber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.reflect.KClass


interface EventBus<T : Any> {
    typealias Subscriber<T> = suspend (event: T) -> Unit

    /**
     * Suspends and keeps [handler] subscribed until the caller's coroutine is cancelled.
     * No need to keep a handle for unsubscribing. Cancel the scope/job running this function.
     *
     * Note that unsubscribing is not instant so include fail-safe logic for the case that
     * required dependencies might be already uninitialized.
     *
     * @param firstType The event type to subscribe to.
     * @param otherTypes Additional event types to subscribe to.
     * @param handler The concurrent handler to be invoked when an event of the specified type is published.
     */
    suspend fun <K : T> subscribe(firstType: KClass<K>, vararg otherTypes: KClass<K>, handler: Subscriber<K>)

}


/**
 * Keeps [handler] subscribed until the [CoroutineScope] is cancelled.
 * No need to keep a handle for unsubscribing. Cancel the scope/job running this function.
 *
 * Note that unsubscribing is not instant so include fail-safe logic for the case that
 * required dependencies might be already uninitialized.
 *
 * @param firstType The event type to subscribe to.
 * @param otherTypes Additional event types to subscribe to.
 * @param handler The concurrent handler to be invoked when an event of the specified type is published.
 */
context(scope: CoroutineScope)
fun <K : T, T : Any> EventBus<T>.subscribeAsync(
    firstType: KClass<K>,
    vararg otherTypes: KClass<K>,
    handler: Subscriber<K>
) = scope.launch { subscribe(firstType, *otherTypes, handler = handler) }


