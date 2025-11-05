package io.github.smyrgeorge.sqlx4k.annotation

import kotlin.reflect.KClass

/**
 * Annotation used to define a database query for repository methods.
 *
 * @property value The SQL query string to be executed.
 * @property checkSyntax Indicates whether the syntax of the query should be validated.
 * @property checkSchema Indicates whether the schema compliance of the query should be validated.
 * @property explicitDependentTables Provide explicit tables that the query is dependent upon. For CRUD operations those tables will be invalidated. Select statements will listen for the invalidation if the `...Flow` suffix is used.
 * @property independent Indicates that this query should not invalidate or listen for invalidations. Note that [explicitDependentTables] must be empty and you mustn't use the `...Flow` suffix.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Query(
    val value: String,
    val checkSyntax: Boolean = true,
    val checkSchema: Boolean = true,
    val explicitDependentTables: Array<KClass<*>> = [],
    val isIndependent: Boolean = false
)
