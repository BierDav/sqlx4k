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
    private val _invalidationFlow = MutableSharedFlow<Set<KClass<*>>>(extraBufferCapacity = 1)

    override val invalidationFlow: SharedFlow<Set<KClass<*>>> = _invalidationFlow.asSharedFlow()

    override fun invalidate(tables: Iterable<KClass<*>>) {
        _invalidationFlow.tryEmit(tables.toSet())
    }
}