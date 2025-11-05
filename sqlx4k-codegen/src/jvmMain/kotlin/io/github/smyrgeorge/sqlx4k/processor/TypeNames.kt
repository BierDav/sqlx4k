package io.github.smyrgeorge.sqlx4k.processor

import io.github.smyrgeorge.sqlx4k.annotation.Column
import io.github.smyrgeorge.sqlx4k.annotation.Id
import io.github.smyrgeorge.sqlx4k.annotation.Query
import io.github.smyrgeorge.sqlx4k.annotation.Repository
import io.github.smyrgeorge.sqlx4k.annotation.Table

/**
 * Centralized fully qualified names used by codegen processors to avoid magic strings.
 */
object TypeNames {
    // Annotations
    val REPOSITORY_ANNOTATION = Repository::class.qualifiedName!!
    val TABLE_ANNOTATION = Table::class.qualifiedName!!
    val ID_ANNOTATION = Id::class.qualifiedName!!
    val COLUMN_ANNOTATION = Column::class.qualifiedName!!
    val QUERY_ANNOTATION = Query::class.qualifiedName!!

    // Core interfaces/classes
    const val CRUD_REPOSITORY = "io.github.smyrgeorge.sqlx4k.CrudRepository"
    const val CONTEXT_CRUD_REPOSITORY = "io.github.smyrgeorge.sqlx4k.ContextCrudRepository"
    const val QUERY_EXECUTOR = "io.github.smyrgeorge.sqlx4k.QueryExecutor"
    const val STATEMENT = "io.github.smyrgeorge.sqlx4k.Statement"

    // Kotlin stdlib
    const val KOTLIN_RESULT = "kotlin.Result"
    const val KOTLIN_LIST = "kotlin.collections.List"
    const val KOTLIN_LONG = "kotlin.Long"
    const val KOTLIN_INT = "kotlin.Int"
}
