package io.github.smyrgeorge.sqlx4k

import kotlin.reflect.KClass

/**
 * Interface defining a contract for basic CRUD (Create, Read, Update, Delete) operations on a data source.
 *
 * This interface abstracts common operations to be implemented for handling entities of type [T].
 * Each operation is asynchronous and returns a [Result], encapsulating either the successful result
 * or an error in case of failure.
 *
 * @param T The type of the entity managed by the repository.
 */
interface CrudRepository<T> {
    /**
     * Inserts the given entity into the data source using the specified driver context.
     *
     * This method performs an asynchronous insert operation and returns the result.
     * If the operation is successful, the result will contain the inserted entity.
     * In case of failure, the result contains the error details.
     *
     * @param context The database driver context used to execute the insert operation.
     * @param entity The entity of type [T] to be inserted into the data source.
     * @return A [Result] containing the inserted entity of type [T] if the operation is successful,
     *         or an error if the operation fails.
     */
    suspend fun insert(context: QueryExecutor, entity: T): Result<T>

    /**
     * Updates the given entity in the data source using the specified driver context.
     *
     * This method performs an asynchronous update operation and returns the result.
     * If the operation is successful, the result will contain the updated entity.
     * In case of failure, the result contains the error details.
     *
     * @param context The database driver context used to execute the update operation.
     * @param entity The entity of type [T] to be updated in the data source.
     * @return A [Result] containing the updated entity of type [T] if the operation is successful,
     *         or an error if the operation fails.
     */
    suspend fun update(context: QueryExecutor, entity: T): Result<T>

    /**
     * Deletes the given entity from the data source using the specified driver context.
     *
     * This method performs an asynchronous delete operation and returns the result.
     * If the operation is successful, the result will contain a successful unit value.
     * In case of failure, the result contains the error details.
     *
     * @param context The database driver context used to execute the delete operation.
     * @param entity The entity of type [T] to be deleted from the data source.
     * @return A [Result] containing a [Unit] value if the operation is successful,
     *         or an error if the operation fails.
     */
    suspend fun delete(context: QueryExecutor, entity: T): Result<Unit>

    /**
     * Saves the given entity to the data source using the specified driver context.
     *
     * This method determines whether to perform an insert or update operation based on the entity's state.
     * If the operation is successful, the result will contain the saved entity.
     * In case of failure, the result contains the error details.
     *
     * @param context The database driver context used to execute the save operation.
     * @param entity The entity of type [T] to be saved in the data source.
     * @return A [Result] containing the saved entity of type [T] if the operation is successful,
     *         or an error if the operation fails.
     */
    suspend fun save(context: QueryExecutor, entity: T): Result<T>


    // Insert
    class BeforeInsertHook<T : Any>(
        val entity: T,
        override val dependentTables: List<KClass<*>>,
        override val source: QueryExecutor
    ) : Hooks.BeforeRepoStatement<QueryExecutor>, Hooks.BeforeCrudRepoStatement<QueryExecutor>

    class AfterInsertHook<T : Any>(
        val entity: T,
        override val dependentTables: List<KClass<*>>,
        override val result: Result<T>,
        override val source: QueryExecutor
    ) : Hooks.AfterRepoStatement<QueryExecutor, T>, Hooks.AfterCrudRepoStatement<QueryExecutor, T>

    // Update
    class BeforeUpdateHook<T : Any>(
        val entity: T,
        override val dependentTables: List<KClass<*>>,
        override val source: QueryExecutor
    ) : Hooks.BeforeRepoStatement<QueryExecutor>, Hooks.BeforeCrudRepoStatement<QueryExecutor>

    class AfterUpdateHook<T : Any>(
        val entity: T,
        override val dependentTables: List<KClass<*>>,
        override val result: Result<T>,
        override val source: QueryExecutor
    ) : Hooks.AfterRepoStatement<QueryExecutor, T>, Hooks.AfterCrudRepoStatement<QueryExecutor, T>

    // Delete
    class BeforeDeleteHook<T : Any>(
        val entity: T,
        override val dependentTables: List<KClass<*>>,
        override val source: QueryExecutor
    ) : Hooks.BeforeRepoStatement<QueryExecutor>, Hooks.BeforeCrudRepoStatement<QueryExecutor>

    class AfterDeleteHook<T : Any>(
        val entity: T,
        override val dependentTables: List<KClass<*>>,
        override val result: Result<Unit>,
        override val source: QueryExecutor,
    ) : Hooks.AfterRepoStatement<QueryExecutor, Unit>, Hooks.AfterCrudRepoStatement<QueryExecutor, Unit>
}