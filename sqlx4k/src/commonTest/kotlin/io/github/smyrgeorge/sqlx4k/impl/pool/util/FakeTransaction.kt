package io.github.smyrgeorge.sqlx4k.impl.pool.util

import io.github.smyrgeorge.sqlx4k.ResultSet
import io.github.smyrgeorge.sqlx4k.RowMapper
import io.github.smyrgeorge.sqlx4k.Statement
import io.github.smyrgeorge.sqlx4k.Transaction
import io.github.smyrgeorge.sqlx4k.impl.invalidation.TrackOnlyTableInvalidationScope

class FakeTransaction : Transaction {
    override val invalidationScope = TrackOnlyTableInvalidationScope()
    override var status: Transaction.Status = Transaction.Status.Open

    override suspend fun commit(): Result<Unit> {
        status = Transaction.Status.Closed
        return Result.success(Unit)
    }

    override suspend fun rollback(): Result<Unit> {
        status = Transaction.Status.Closed
        return Result.success(Unit)
    }

    override suspend fun execute(sql: String): Result<Long> = Result.success(0)
    override suspend fun execute(statement: Statement): Result<Long> = Result.success(0)
    override suspend fun fetchAll(sql: String): Result<ResultSet> =
        Result.success(ResultSet(emptyList(), null, ResultSet.Metadata(emptyList())))

    override suspend fun fetchAll(statement: Statement): Result<ResultSet> =
        Result.success(ResultSet(emptyList(), null, ResultSet.Metadata(emptyList())))

    override suspend fun <T> fetchAll(statement: Statement, rowMapper: RowMapper<T>): Result<List<T>> =
        Result.success(emptyList())
}