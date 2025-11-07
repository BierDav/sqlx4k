package io.github.smyrgeorge.sqlx4k.postgres.pgmq.impl

import io.github.smyrgeorge.sqlx4k.*
import io.github.smyrgeorge.sqlx4k.impl.metadata.MetadataStorage
import io.github.smyrgeorge.sqlx4k.postgres.IPostgresNotifications
import io.github.smyrgeorge.sqlx4k.postgres.IPostgresSQL
import io.github.smyrgeorge.sqlx4k.postgres.pgmq.PgMqDbAdapter

class PgMqDbAdapterImpl(private val pg: IPostgresSQL) : PgMqDbAdapter,
    QueryExecutor.Transactional by pg,
    IPostgresNotifications by pg {
    override val metadata: MetadataStorage = pg.metadata

    override suspend fun execute(sql: String): Result<Long> = pg.execute(sql)
    override suspend fun fetchAll(sql: String): Result<ResultSet> = pg.fetchAll(sql)
}