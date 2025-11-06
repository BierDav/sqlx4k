package io.github.smyrgeorge.sqlx4k.impl.hook

typealias HookEventBus = EventBus<HookEvent<*>>
typealias MutableHookEventBus = MutableEventBus<HookEvent<*>>
interface HookApi {
    val hook: HookEventBus
}

