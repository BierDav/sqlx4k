package io.github.smyrgeorge.sqlx4k

interface TableInvalidationScopeProvider {
    /**
     * Represents the scope for invalidating tables.
     * This is used to trigger invalidation events for tables when their data changes.
     */
    val invalidationScope: TableInvalidationScope
}