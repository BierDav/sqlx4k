package io.github.smyrgeorge.sqlx4k.impl.invalidation

import io.github.smyrgeorge.sqlx4k.TableInvalidationScope
import io.github.smyrgeorge.sqlx4k.TableInvalidationScopeProvider
import kotlinx.coroutines.flow.SharedFlow
import kotlin.reflect.KClass

class TransactionTableInvalidationScope(val parentProvider: TableInvalidationScopeProvider) : TableInvalidationScope {
    val invalidatedTables = mutableSetOf<KClass<*>>()

    override val invalidationFlow: SharedFlow<Set<KClass<*>>>
        get() = parentProvider.invalidationScope.invalidationFlow

    override fun invalidate(tables: Iterable<KClass<*>>) {
        invalidatedTables.addAll(tables)
    }

    fun commit() {
        parentProvider.invalidationScope.invalidate(invalidatedTables)
        invalidatedTables.clear()
    }

    fun rollback() = invalidatedTables.clear()
}