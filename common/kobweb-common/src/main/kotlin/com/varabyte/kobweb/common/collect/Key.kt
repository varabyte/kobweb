package com.varabyte.kobweb.common.collect

/**
 * A simple key that associates a type with a name that must be globally unique to your project.
 */
class Key<T> private constructor(val name: String) {
    companion object {
        private val registeredKeys = mutableMapOf<String, Key<*>>()
        fun <T> create(name: String): Key<T> {
            require(!registeredKeys.containsKey(name)) { "Attempting to create with a name that was already registered earlier: $name" }
            val key = Key<T>(name)
            registeredKeys[name] = key
            return key
        }
    }
}
