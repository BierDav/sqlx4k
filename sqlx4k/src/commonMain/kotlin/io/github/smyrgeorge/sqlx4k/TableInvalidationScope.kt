package io.github.smyrgeorge.sqlx4k

import kotlinx.coroutines.flow.SharedFlow
import kotlin.reflect.KClass

interface TableInvalidationScope {
    /**
     * A hot [SharedFlow] that emits a SET of all tables that were
     * invalidated in a single batch.
     *
     * Emitting a Set is crucial for solving the "multiple invalidations"
     * edge case. Listeners can check this set and re-query just once.
     */
    val invalidationFlow: SharedFlow<Set<KClass<*>>>

    /**
     * Marks multiple tables as invalid in a single atomic operation.
     * This is the preferred method for invalidating multiple tables,
     * as it guarantees they are processed in the same batch.
     */
    suspend fun invalidate(vararg tables: KClass<*>) = invalidate(tables.toList())

    /**
     * Marks a collection of tables as invalid.
     */
    suspend fun invalidate(tables: Iterable<KClass<*>>)
}