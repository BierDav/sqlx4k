package io.github.smyrgeorge.sqlx4k.impl.driver

import io.github.smyrgeorge.sqlx4k.Connection
import io.github.smyrgeorge.sqlx4k.Driver
import io.github.smyrgeorge.sqlx4k.ResultSet
import io.github.smyrgeorge.sqlx4k.Transaction
import io.github.smyrgeorge.sqlx4k.impl.hook.MutableEventBus
import io.github.smyrgeorge.sqlx4k.impl.hook.MutableHookEventBus
import io.github.smyrgeorge.sqlx4k.impl.metadata.MetadataStorage

abstract class DriverBase : Driver {
    override val metadata: MetadataStorage = MetadataStorage()
    override val hook = MutableHookEventBus()

    abstract class CnBase(
        parentHook: MutableHookEventBus
    ) : Connection {
        override val hook = MutableEventBus(parentHook)
        override val metadata: MetadataStorage = MetadataStorage()

        abstract suspend fun internalClose(): Result<Unit>
        override suspend fun close(): Result<Unit> {
            hook.publish { Connection.BeforeCloseHook(this) }
            val result = internalClose()
            hook.publish { Connection.AfterCloseHook(this, result) }
            return result
        }

        abstract suspend fun internalExecute(sql: String): Result<Long>
        override suspend fun execute(sql: String): Result<Long> {
            hook.publish { Connection.BeforeExecuteHook(this, sql) }
            val result = internalExecute(sql)
            hook.publish { Connection.AfterExecuteHook(this, sql, result) }
            return result
        }


        abstract suspend fun internalFetchAll(sql: String): Result<ResultSet>
        override suspend fun fetchAll(sql: String): Result<ResultSet> {
            hook.publish { Connection.BeforeFetchAllHook(this, sql) }
            val result = internalFetchAll(sql)
            hook.publish { Connection.AfterFetchAllHook(this, sql, result) }
            return result
        }

        abstract suspend fun internalBegin(): Result<Transaction>
        override suspend fun begin(): Result<Transaction> {
            hook.publish { Connection.BeforeBeginHook(this) }
            val result = internalBegin()
            hook.publish { Connection.AfterBeginHook(this, result) }
            return result
        }
    }

    abstract class TxBase(
        parentHook: MutableHookEventBus
    ) : Transaction {
        override val hook = MutableEventBus(parentHook)
        override val metadata: MetadataStorage = MetadataStorage()

        abstract suspend fun internalCommit(): Result<Unit>
        override suspend fun commit(): Result<Unit> {
            hook.publish { Transaction.BeforeCommitHook(this) }
            val result = internalCommit()
            hook.publish { Transaction.AfterCommitHook(this, result) }
            return result
        }

        abstract suspend fun internalRollback(): Result<Unit>
        override suspend fun rollback(): Result<Unit> {
            hook.publish { Transaction.BeforeRollbackHook(this) }
            val result = internalRollback()
            hook.publish { Transaction.AfterRollbackHook(this, result) }
            return result
        }

        abstract suspend fun internalExecute(sql: String): Result<Long>
        override suspend fun execute(sql: String): Result<Long> {
            hook.publish { Transaction.BeforeExecuteHook(this, sql) }
            val result = internalExecute(sql)
            hook.publish { Transaction.AfterExecuteHook(this, sql, result) }
            return result
        }


        abstract suspend fun internalFetchAll(sql: String): Result<ResultSet>
        override suspend fun fetchAll(sql: String): Result<ResultSet> {
            hook.publish { Transaction.BeforeFetchAllHook(this, sql) }
            val result = internalFetchAll(sql)
            hook.publish { Transaction.AfterFetchAllHook(this, sql, result) }
            return result
        }

    }


    abstract suspend fun internalAcquire(): Result<Connection>
    override suspend fun acquire(): Result<Connection> {
        hook.publish { Driver.BeforeAcquireHook(this) }
        val result = internalAcquire()
        hook.publish { Driver.AfterAcquireHook(this, result) }
        return result
    }


    abstract suspend fun internalBegin(): Result<Transaction>
    override suspend fun begin(): Result<Transaction> {
        hook.publish { Driver.BeforeBeginHook(this) }
        val result = internalBegin()
        hook.publish { Driver.AfterBeginTransaction(this, result) }
        return result
    }


    abstract suspend fun internalExecute(sql: String): Result<Long>
    override suspend fun execute(sql: String): Result<Long> {
        hook.publish { Driver.BeforeExecuteHook(this, sql) }
        val result = internalExecute(sql)
        hook.publish { Driver.AfterExecuteHook(this, sql, result) }
        return result
    }


    abstract suspend fun internalFetchAll(sql: String): Result<ResultSet>
    override suspend fun fetchAll(sql: String): Result<ResultSet> {
        hook.publish { Driver.BeforeFetchAllHook(this, sql) }
        val result = internalFetchAll(sql)
        hook.publish { Driver.AfterFetchAllHook(this, sql, result) }
        return result
    }


    abstract suspend fun internalClose(): Result<Unit>
    override suspend fun close(): Result<Unit> {
        hook.publish { Driver.BeforeCloseHook(this) }
        val result = internalClose()
        hook.publish { Driver.AfterCloseHook(this, result) }
        return result
    }
}

