package io.github.smyrgeorge.sqlx4k

import kotlinx.coroutines.flow.*
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
    fun invalidate(vararg tables: KClass<*>) = invalidate(tables.toList())

    /**
     * Marks a collection of tables as invalid.
     */
    fun invalidate(tables: Iterable<KClass<*>>)
}

/**
 * Creates a [Flow] that listens for invalidations on a specific set of tables and re-executes a [query]
 * to emit new data.
 *
 * This function provides a "read-with-invalidation" pattern.
 *
 * 1.  **Initial Emission:** The [query] is executed immediately upon collection, and its result is emitted.
 * 2.  **Invalidation Listening:** It then listens for invalidations on the [TableInvalidationScope].
 * 3.  **Re-Query:** When an invalidation event occurs for any of the specified [tables], the [query] is
 * re-executed, and the new result is emitted.
 * 4.  **Conflation:** This flow is **conflated**. If multiple invalidations occur in quick succession
 * while the [query] is running, intermediate invalidation signals will be dropped. The [query]
 * will re-run only *after* the current execution finishes, using the latest invalidation signal.
 * This prevents queuing up multiple, stale queries.
 *
 * **Edge Case:** If no [tables] are provided (the vararg is empty), the [query] is executed once,
 * its result is emitted, and the flow completes.
 *
 * @param T The type of data returned by the [query].
 * @param tables The set of table classes (`KClass<*>`) to monitor. If any of these are invalidated,
 * the [query] will be re-run.
 * @param query The suspendable lambda function that fetches and returns the data.
 * @return A [Flow] that emits the initial data from [query] and then re-emits updated data
 * whenever a relevant table invalidation occurs.
 */
fun <T> QueryExecutor.listenForInvalidation(
    vararg tables: KClass<*>,
    query: suspend QueryExecutor.() -> T
): Flow<T> = channelFlow {
    val dependentTables = tables.toSet()
    if (dependentTables.isEmpty()) {
        send(query())
        return@channelFlow
    }

    invalidationScope.invalidationFlow
        .filter { invalidatedSet ->
            invalidatedSet.any { it in dependentTables }
        }
        .onStart { send(query()) }
        .collect { send(query()) }
}
