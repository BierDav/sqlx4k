package io.github.smyrgeorge.sqlx4k.impl.metadata

import co.touchlab.stately.collections.ConcurrentMutableMap
import kotlin.reflect.KClass

class MetadataStorage {
    private val data = ConcurrentMutableMap<KClass<*>, Any>()

    operator fun <T : Any> set(key: KClass<T>, value: T) {
        data[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(key: KClass<T>): T? = data[key] as T?

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getOrDefault(key: KClass<T>, default: T): T = data[key] as T? ?: default

    inline fun <reified T : Any> set(key: T) = set(T::class, key)
    inline fun <reified T : Any> get(): T? = get(T::class)
    inline fun <reified T : Any> getOrDefault(default: T): T = getOrDefault(T::class, default)
}