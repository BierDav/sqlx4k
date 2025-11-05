package io.github.smyrgeorge.sqlx4k.impl.invalidation

import io.github.smyrgeorge.sqlx4k.TableInvalidationScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.reflect.KClass

class DefaultTableInvalidationScope : TableInvalidationScope {
    /**
     * The backing [MutableSharedFlow] for our public [invalidationFlow].
     */
    private val _invalidationFlow = MutableSharedFlow<Set<KClass<*>>>()

    override val invalidationFlow: SharedFlow<Set<KClass<*>>> = _invalidationFlow.asSharedFlow()

    override suspend fun invalidate(tables: Iterable<KClass<*>>) {
        _invalidationFlow.emit(tables.toSet())
    }
}