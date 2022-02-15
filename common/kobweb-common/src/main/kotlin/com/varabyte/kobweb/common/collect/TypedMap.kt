package com.varabyte.kobweb.common.collect

/**
 * A map which can store heterogeneous values, where the type is constrained by the [Key].
 *
 * For example:
 *
 * ```
 * val IdKey = Key.create<MutableSet<String>>("ids")
 *
 * val data = TypedMap()
 * data[IdKey] = mutableSetOf()
 * data.getValue(IdKey).add("hello")
 * ```
 */
@Suppress("UNCHECKED_CAST") // Casting is safe because we always control the set
class TypedMap {
    private val innerMap = mutableMapOf<String, Any>()

    operator fun <T: Any> set(key: Key<T>, value: T) {
        innerMap[key.name] = value
    }

    fun <T: Any> computeIfAbsent(key: Key<T>, compute: () -> T): T {
        return innerMap.computeIfAbsent(key.name) { compute() } as T
    }

    operator fun <T: Any> get(key: Key<T>): T? {
        return innerMap[key.name] as? T
    }

    fun <T: Any> getValue(key: Key<T>): T = this[key]!!
}
