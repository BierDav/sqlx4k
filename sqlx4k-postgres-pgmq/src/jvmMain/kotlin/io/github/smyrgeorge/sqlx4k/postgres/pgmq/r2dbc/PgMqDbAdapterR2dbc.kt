package io.github.smyrgeorge.sqlx4k.postgres.pgmq.r2dbc

import io.github.smyrgeorge.sqlx4k.QueryExecutor
import io.github.smyrgeorge.sqlx4k.ResultSet
import io.github.smyrgeorge.sqlx4k.RowMapper
import io.github.smyrgeorge.sqlx4k.Statement
import io.github.smyrgeorge.sqlx4k.TableInvalidationScope
import io.github.smyrgeorge.sqlx4k.Transaction
import io.github.smyrgeorge.sqlx4k.postgres.IPostgresNotifications
import io.github.smyrgeorge.sqlx4k.postgres.Notification
import io.github.smyrgeorge.sqlx4k.postgres.PostgreSQLImpl
import io.github.smyrgeorge.sqlx4k.postgres.pgmq.PgMqDbAdapter
import io.r2dbc.pool.ConnectionPool
import io.r2dbc.postgresql.PostgresqlConnectionFactory

/**
 * Implementation of the `PgMqDbAdapter` interface using R2DBC for asynchronous
 * interaction with a PostgreSQL database.
 *
 * This class provides an abstraction layer to handle database operations such as
 * executing SQL statements, fetching results, managing transactions, and listening to
 * PostgreSQL notifications on specified channels.
 *
 * @param pool The connection pool used for acquiring database connections.
 * @param connectionFactory The factory for creating individual PostgreSQL connections.
 */
class PgMqDbAdapterR2dbc(
    private val connectionFactory: PostgresqlConnectionFactory,
    pool: ConnectionPool,
    private val adapter: PostgreSQLImpl = PostgreSQLImpl(connectionFactory, pool)
) : PgMqDbAdapter,
    QueryExecutor.Transactional by adapter,
    IPostgresNotifications by adapter {

    override suspend fun execute(sql: String): Result<Long> = adapter.execute(sql)

    override suspend fun fetchAll(sql: String): Result<ResultSet> = adapter.fetchAll(sql)

    override suspend fun <T> fetchAll(statement: Statement, rowMapper: RowMapper<T>): Result<List<T>> =
        adapter.fetchAll(statement, rowMapper)

    override val invalidationScope: TableInvalidationScope
        get() = adapter.invalidationScope
}
