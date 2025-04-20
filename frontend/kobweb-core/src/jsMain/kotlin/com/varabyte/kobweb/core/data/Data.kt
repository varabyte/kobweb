package com.varabyte.kobweb.core.data

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.layout.Layout
import kotlin.reflect.KClass

/**
 * Read-only access to a [MutableData] store.
 *
 * See the header comment for that class for more information.
 */
interface Data {
    operator fun <T : Any> get(key: KClass<T>): T?
}

fun <T : Any> Data.getValue(key: KClass<T>): T = this[key]!!
inline fun <reified T : Any> Data.get(): T? = this[T::class]
inline fun <reified T : Any> Data.getValue(): T = getValue(T::class)

/**
 * A mutable in-memory data store providing access to values using the
 * [Service Locator pattern](https://en.wikipedia.org/wiki/Service_locator_pattern)
 *
 * Users will be able to modify this data store inside methods annotated with [InitRoute], and then access a read-only
 * version of the data inside a [Page] or a [Layout].
 */
@Suppress("UNCHECKED_CAST")
class MutableData : Data {
    private val map = mutableStateMapOf<KClass<*>, Any>()

    operator fun <T : Any> set(key: KClass<T>, value: T) {
        map[key] = value
    }

    override operator fun <T : Any> get(key: KClass<T>): T? {
        return map[key] as? T
    }

    fun clear() {
        map.clear()
    }
}

inline fun <reified T : Any> MutableData.add(value: T) {
    this[T::class] = value
}

inline fun <reified T : Any> MutableData.addIfAbsent(value: () -> T) {
    if (this[T::class] != null) return
    this[T::class] = value.invoke()
}
