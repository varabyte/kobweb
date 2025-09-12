package com.varabyte.kobweb.api.data

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Read-only access to a [MutableData] store.
 *
 * See the header comment for that class for more information.
 */
interface Data {
    operator fun <T : Any> get(key: Class<T>): T?

    /**
     * Create a mutable copy of this data class.
     *
     * Note that adding and removing elements to the copy will have no effect on this source data instance.
     */
    fun toMutableData(): MutableData
}

fun <T : Any> Data.getValue(key: Class<T>): T = this[key]!!
inline fun <reified T : Any> Data.get(): T? = this[T::class.java]
inline fun <reified T : Any> Data.getValue(): T = getValue(T::class.java)

/**
 * A thread-safe in-memory data store providing access to values using the
 * [Service Locator pattern](https://en.wikipedia.org/wiki/Service_locator_pattern)
 */
@Suppress("UNCHECKED_CAST")
class MutableData private constructor(private val cache: MutableMap<Class<*>, Any>): Data {
    constructor() : this(mutableMapOf())

    private val lock = ReentrantLock()

    operator fun <T : Any> set(key: Class<T>, value: T) {
        lock.withLock { cache[key] = value }
    }

    override operator fun <T : Any> get(key: Class<T>): T? {
        return lock.withLock { cache[key] as? T }
    }

    override fun toMutableData(): MutableData {
        return MutableData(cache.toMutableMap())
    }
}

inline fun <reified T : Any> MutableData.add(value: T) {
    this[T::class.java] = value
}
