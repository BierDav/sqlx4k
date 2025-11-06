package io.github.smyrgeorge.sqlx4k.impl.hook

interface HookEvent<T> {
    val source: T
}


interface AfterHookEvent<T, K> : HookEvent<T> {
    val result: Result<K>
}