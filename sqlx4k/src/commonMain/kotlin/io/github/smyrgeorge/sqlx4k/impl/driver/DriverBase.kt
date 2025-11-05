package io.github.smyrgeorge.sqlx4k.impl.driver

import io.github.smyrgeorge.sqlx4k.Connection
import io.github.smyrgeorge.sqlx4k.Driver
import io.github.smyrgeorge.sqlx4k.TableInvalidationScopeProvider
import io.github.smyrgeorge.sqlx4k.Transaction
import io.github.smyrgeorge.sqlx4k.impl.invalidation.DefaultTableInvalidationScope
import io.github.smyrgeorge.sqlx4k.impl.invalidation.TransactionTableInvalidationScope

abstract class DriverBase : Driver {
    override val invalidationScope = DefaultTableInvalidationScope()

    abstract class CnBase(parentScopeProvider: TableInvalidationScopeProvider) : Connection,
        TableInvalidationScopeProvider by parentScopeProvider

    abstract class TxBase(parentInvalidationScopeProvider: TableInvalidationScopeProvider) : Transaction {
        override val invalidationScope = TransactionTableInvalidationScope(parentInvalidationScopeProvider)
    }
}