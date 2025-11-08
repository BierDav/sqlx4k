package io.github.smyrgeorge.sqlx4k

import io.github.smyrgeorge.sqlx4k.impl.hook.AfterHookEvent
import io.github.smyrgeorge.sqlx4k.impl.hook.HookEvent
import kotlin.reflect.KClass

interface Hooks {
    sealed interface BeforeBeginTransaction<T : QueryExecutor> : HookEvent<T>
    sealed interface AfterBeginTransaction<T : QueryExecutor> :
        AfterHookEvent<T, Transaction>

    // Statement
    sealed interface BeforeStatement<T : QueryExecutor> : HookEvent<T> {
        val sql: String
    }

    sealed interface AfterStatement<T : QueryExecutor, K : Any> : AfterHookEvent<T, K> {
        val statement: String
    }


    // Execute
    sealed interface BeforeExecute<T : QueryExecutor> : BeforeStatement<T>
    sealed interface AfterExecute<T : QueryExecutor> : AfterStatement<T, Long>

    // FetchAll
    sealed interface BeforeFetchAll<T : QueryExecutor> : BeforeStatement<T>
    sealed interface AfterFetchAll<T : QueryExecutor> : AfterStatement<T, ResultSet>


    // Repo
    interface BeforeRepoStatement<T : QueryExecutor> : HookEvent<T> {
        val dependentTables: List<KClass<*>>
    }

    interface AfterRepoStatement<T : QueryExecutor, K : Any> : AfterHookEvent<T, K> {
        val dependentTables: List<KClass<*>>
    }


    interface BeforeCrudRepoStatement<T : QueryExecutor> : BeforeRepoStatement<T>
    interface AfterCrudRepoStatement<T : QueryExecutor, K : Any> : AfterRepoStatement<T, K>


    interface Repository {
        class BeforeCrudRepoHook(
            override val dependentTables: List<KClass<*>>,
            override val source: QueryExecutor
        ) : BeforeCrudRepoStatement<QueryExecutor>

        class AfterCrudRepoHook(
            override val dependentTables: List<KClass<*>>,
            override val result: Result<Long>,
            override val source: QueryExecutor
        ) : AfterCrudRepoStatement<QueryExecutor, Long>
    }


    /*sealed interface Driver {
        // Open connection
        @Deprecated(
            "Use Driver.BeforeOpenConnectionHook instead",
            replaceWith = ReplaceWith("Driver.BeforeAcquireHook")
        )
        typealias BeforeAcquire = Driver.BeforeAcquireHook
        @Deprecated(
            "Use Driver.AfterOpenConnectionHook instead",
            replaceWith = ReplaceWith("Driver.AfterAcquireHook")
        )
        typealias AfterAcquire = Driver.AfterAcquireHook
    }

    sealed interface Connection {
        // Close connection
        @Deprecated(
            "Use Connection.BeforeCloseConnectionHook instead",
            replaceWith = ReplaceWith("Connection.BeforeCloseConnectionHook")
        )
        typealias BeforeCloseConnection = io.github.smyrgeorge.sqlx4k.Connection.BeforeCloseHook
        @Deprecated(
            "Use Connection.AfterCloseConnectionHook instead",
            replaceWith = ReplaceWith("Connection.AfterCloseConnectionHook")
        )
        typealias AfterCloseConnection = io.github.smyrgeorge.sqlx4k.Connection.AfterCloseHook
    }

    sealed interface Transaction {
        // Commit
        @Deprecated(
            "Use Transaction.BeforeOpenConnectionHook instead",
            replaceWith = ReplaceWith("Transaction.BeforeCommitHook")
        )
        typealias BeforeCommit = io.github.smyrgeorge.sqlx4k.Transaction.BeforeCommitHook
        @Deprecated("Use Transaction.AfterCommitHook instead", replaceWith = ReplaceWith("Transaction.AfterCommitHook"))
        typealias AfterCommit = io.github.smyrgeorge.sqlx4k.Transaction.AfterCommitHook

        // Rollback
        @Deprecated(
            "Use Transaction.BeforeRollbackHook instead",
            replaceWith = ReplaceWith("Transaction.BeforeRollbackHook")
        )
        typealias BeforeRollback = io.github.smyrgeorge.sqlx4k.Transaction.BeforeRollbackHook
        @Deprecated(
            "Use Transaction.AfterRollbackHook instead",
            replaceWith = ReplaceWith("Transaction.AfterRollbackHook")
        )
        typealias AfterRollback = io.github.smyrgeorge.sqlx4k.Transaction.AfterRollbackHook
    }

    sealed interface ConnectionPool {

    }*/
}