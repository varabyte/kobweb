package com.varabyte.kobweb.silk.components.util.internal

import kotlin.reflect.KProperty

/**
 * A property delegate that caches its generated value so the same instance will be returned on subsequent calls when
 * delegated to by the same property over and over again.
 *
 * For example, ComponentStyle here is actually a function which returns a subclass of this caching provider. So when
 * you use it like this:
 *
 * ```
 * val MyStyle by ComponentStyle { /* ... */ }
 * ```
 *
 * you can be sure that "MyStyle" will always return the same instance, which is probably what people expect when they
 * see the above declaration anyway!
 *
 * This is especially important in the world of Compose, where otherwise, every time you passed a property declared
 * using this "by" syntax, you'd end up getting a new value each time, which would cause a recomposition each time!
 */
abstract class CacheByPropertyNameDelegate<T> {
    private val cache = mutableMapOf<String, T>()

    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): T {
        val name = property.name
        return cache.getOrPut(name) { create(name) }
    }

    protected abstract fun create(propertyName: String): T
}