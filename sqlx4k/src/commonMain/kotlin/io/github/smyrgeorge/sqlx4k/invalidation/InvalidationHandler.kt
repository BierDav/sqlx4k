package io.github.smyrgeorge.sqlx4k.invalidation

import io.github.smyrgeorge.sqlx4k.Driver
import io.github.smyrgeorge.sqlx4k.Hooks
import io.github.smyrgeorge.sqlx4k.QueryExecutor
import io.github.smyrgeorge.sqlx4k.Transaction
import io.github.smyrgeorge.sqlx4k.impl.hook.HookApi
import io.github.smyrgeorge.sqlx4k.impl.hook.MutableEventBus
import io.github.smyrgeorge.sqlx4k.impl.hook.subscribeAsync
import io.github.smyrgeorge.sqlx4k.impl.metadata.MetadataStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlin.reflect.KClass


class InvalidationScope {
    /**
     * The backing [MutableSharedFlow] for our public [invalidationFlow].
     */
    private val _invalidationFlow = MutableSharedFlow<Set<KClass<*>>>(extraBufferCapacity = 1)

    val invalidationFlow: SharedFlow<Set<KClass<*>>> = _invalidationFlow.asSharedFlow()

    internal fun invalidate(tables: Iterable<KClass<*>>) {
        _invalidationFlow.tryEmit(tables.toSet())
    }
}

class InvalidationTransactionMetadata(val dependentTables: MutableSet<KClass<*>> = mutableSetOf())


private fun MetadataStorage.requireTransactionMetadata() = get<InvalidationTransactionMetadata>()
    ?: error("Transaction without ${InvalidationTransactionMetadata::class.simpleName} metadata found. Have you called `Driver.invalidationHandler()` in a stable coroutine scope?: ${this::class.simpleName}")

private fun MetadataStorage.requireInvalidationScope() = get<InvalidationScope>()
    ?: error("QueryExecutor without ${InvalidationScope::class.simpleName} metadata found. Have you called `Driver.invalidationHandler()` in a stable coroutine scope?: ${this::class.simpleName}")

interface InvalidationHandlerTarget : QueryExecutor, QueryExecutor.Transactional, HookApi

/**
 * Applies the required hooks for the InvalidationHandler. Be aware that this will enable [MutableEventBus.enableGlobally].
 * Call this in a stable coroutine scope. As soon as the parent CoroutineScope gets cancelled, all Repository calls will fail.
 *
 * @see MutableEventBus.enableGlobally
 */
fun InvalidationHandlerTarget.applyInvalidationHandler(coroutineScope: CoroutineScope) {
    MutableEventBus.enableGlobally = true

    val scope = InvalidationScope()
    metadata.set(scope)

    hook.subscribeAsync(coroutineScope, Driver.AfterAcquireHook::class) {
        it.result.getOrNull()?.metadata?.set(scope)
    }
    hook.subscribeAsync(coroutineScope, Hooks.AfterBeginTransaction::class) {
        it.result.getOrNull()?.metadata?.apply {
            set(scope)
            set(InvalidationTransactionMetadata())
        }
    }


    hook.subscribeAsync(coroutineScope, Hooks.AfterCrudRepoStatement::class) {
        when (it.source) {
            is Transaction -> {
                val transactionMetadata = it.source.metadata.requireTransactionMetadata()
                transactionMetadata.dependentTables.addAll(it.dependentTables)
            }

            else -> {
                scope.invalidate(it.dependentTables)
            }
        }
    }

    hook.subscribeAsync(coroutineScope, Transaction.AfterCommitHook::class) {
        if (it.result.isFailure)
            return@subscribeAsync
        val transactionMetadata = it.source.metadata.requireTransactionMetadata()
        scope.invalidate(transactionMetadata.dependentTables)
        transactionMetadata.dependentTables.clear()
    }

    hook.subscribeAsync(coroutineScope, Transaction.AfterRollbackHook::class) {
        if (it.result.isFailure)
            return@subscribeAsync
        val transactionMetadata = it.source.metadata.requireTransactionMetadata()
        transactionMetadata.dependentTables.clear()
    }
}


fun <T> QueryExecutor.listenForInvalidation(
    vararg tables: KClass<*>,
    query: suspend QueryExecutor.() -> T
): Flow<T> {
    val scope = metadata.requireInvalidationScope()
    return channelFlow {
        val dependentTables = tables.toSet() + Any::class

        scope.invalidationFlow
            .filter { invalidatedSet ->
                invalidatedSet.any { it in dependentTables }
            }
            .onStart { send(query()) }
            .collect { send(query()) }
    }
}