package io.github.smyrgeorge.sqlx4k

import io.github.smyrgeorge.sqlx4k.impl.hook.AfterHookEvent
import io.github.smyrgeorge.sqlx4k.impl.hook.HookApi
import io.github.smyrgeorge.sqlx4k.impl.hook.HookEvent
import io.github.smyrgeorge.sqlx4k.invalidation.InvalidationHandlerTarget

/**
 * Represents a driver interface that extends various query execution and management capabilities.
 *
 * The `Driver` interface combines functionalities provided by the `QueryExecutor`,
 * `QueryExecutor.Pool`, `QueryExecutor.Transactional`, and `QueryExecutor.Migrate` interfaces.
 * It provides a unified contract for executing SQL statements, managing connection pools,
 * handling transactions, and executing database migrations.
 *
 * Implementing this interface allows a class to offer comprehensive database interaction
 * and management capabilities, including query execution, connection pooling, transactional
 * operations, and migration mechanisms.
 */
interface Driver : ConnectionPool, QueryExecutor, QueryExecutor.Transactional, QueryExecutor.Migrate, HookApi,
    InvalidationHandlerTarget {
    // Acquire
    class BeforeAcquireHook(override val source: Driver) : HookEvent<Driver>
    class AfterAcquireHook(
        override val source: Driver,
        override val result: Result<Connection>
    ) : AfterHookEvent<Driver, Connection>

    // Close
    class BeforeCloseHook(override val source: Driver) : HookEvent<Driver>
    class AfterCloseHook(
        override val source: Driver,
        override val result: Result<Unit>
    ) : AfterHookEvent<Driver, Unit>


    // Begin transaction
    class BeforeBeginHook(override val source: Driver) : Hooks.BeforeBeginTransaction<Driver>
    class AfterBeginTransaction(
        override val source: Driver,
        override val result: Result<Transaction>,
    ) : Hooks.AfterBeginTransaction<Driver>

    // Statement
    interface BeforeStatementHook : Hooks.BeforeStatement<Driver>
    interface AfterStatementHook<T : Any> : Hooks.AfterStatement<Driver, T>


    // Execute
    class BeforeExecuteHook(override val source: Driver, override val sql: String) :
        Hooks.BeforeExecute<Driver>, BeforeStatementHook

    class AfterExecuteHook(
        override val source: Driver,
        override val statement: String,
        override val result: Result<Long>
    ) : Hooks.AfterExecute<Driver>, AfterStatementHook<Long>

    // FetchAll
    class BeforeFetchAllHook(override val source: Driver, override val sql: String) :
        Hooks.BeforeFetchAll<Driver>, BeforeStatementHook

    class AfterFetchAllHook(
        override val source: Driver,
        override val statement: String,
        override val result: Result<ResultSet>
    ) : Hooks.AfterFetchAll<Driver>, AfterStatementHook<ResultSet>
}