package io.github.smyrgeorge.sqlx4k.impl.invalidation

import io.github.smyrgeorge.sqlx4k.TableInvalidationScope
import io.github.smyrgeorge.sqlx4k.TableInvalidationScopeProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlin.reflect.KClass

class TrackOnlyTableInvalidationScope : TableInvalidationScope {
    val invalidatedTables = mutableSetOf<KClass<*>>()

    override val invalidationFlow: SharedFlow<Set<KClass<*>>> = MutableSharedFlow<Set<KClass<*>>>()

    override fun invalidate(tables: Iterable<KClass<*>>) {
        invalidatedTables.addAll(tables)
    }

    suspend fun commit() = invalidatedTables.clear()

    suspend fun rollback() = invalidatedTables.clear()
}