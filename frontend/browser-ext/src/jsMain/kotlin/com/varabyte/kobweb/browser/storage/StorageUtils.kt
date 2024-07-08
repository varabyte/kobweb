package com.varabyte.kobweb.browser.storage

import org.w3c.dom.Storage
import kotlin.enums.EnumEntries

/**
 * A key which can be used to read and write type-safe values to / from a [Storage] object.
 */
abstract class StorageKey<T>(val name: String, val defaultValue: T? = null) {
    abstract fun convertToString(value: T): String
    abstract fun convertFromString(value: String): T?
}

class StringStorageKey(name: String, defaultValue: String? = null) : StorageKey<String>(name, defaultValue) {
    override fun convertToString(value: String) = value
    override fun convertFromString(value: String) = value
}

class BooleanStorageKey(name: String, defaultValue: Boolean? = null) : StorageKey<Boolean>(name, defaultValue) {
    override fun convertToString(value: Boolean) = value.toString()
    override fun convertFromString(value: String): Boolean? = value.toBooleanStrictOrNull()
}

class ByteStorageKey(name: String, defaultValue: Byte? = null) : StorageKey<Byte>(name, defaultValue) {
    override fun convertToString(value: Byte) = value.toString()
    override fun convertFromString(value: String): Byte? = value.toByteOrNull()
}

class ShortStorageKey(name: String, defaultValue: Short? = null) : StorageKey<Short>(name, defaultValue) {
    override fun convertToString(value: Short) = value.toString()
    override fun convertFromString(value: String): Short? = value.toShortOrNull()
}

class IntStorageKey(name: String, defaultValue: Int? = null) : StorageKey<Int>(name, defaultValue) {
    override fun convertToString(value: Int) = value.toString()
    override fun convertFromString(value: String): Int? = value.toIntOrNull()
}

class LongStorageKey(name: String, defaultValue: Long? = null) : StorageKey<Long>(name, defaultValue) {
    override fun convertToString(value: Long) = value.toString()
    override fun convertFromString(value: String): Long? = value.toLongOrNull()
}

class FloatStorageKey(name: String, defaultValue: Float? = null) : StorageKey<Float>(name, defaultValue) {
    override fun convertToString(value: Float) = value.toString()
    override fun convertFromString(value: String): Float? = value.toFloatOrNull()
}

class DoubleStorageKey(name: String, defaultValue: Double? = null) : StorageKey<Double>(name, defaultValue) {
    override fun convertToString(value: Double) = value.toString()
    override fun convertFromString(value: String): Double? = value.toDoubleOrNull()
}

class EnumStorageKey<T : Enum<T>>(name: String, private val entries: EnumEntries<T>, defaultValue: T? = null) :
    StorageKey<T>(name, defaultValue) {
    override fun convertToString(value: T) = value.name
    override fun convertFromString(value: String): T? = entries.firstOrNull { it.name == value }
}

fun <T : Enum<T>> EnumEntries<T>.createStorageKey(name: String, defaultValue: T? = null): EnumStorageKey<T> {
    return EnumStorageKey(name, this, defaultValue)
}

fun Storage.removeItem(key: StorageKey<*>) {
    removeItem(key.name)
}

fun <T> Storage.getItem(key: StorageKey<T>): T? {
    return getItem(key.name)?.let { key.convertFromString(it) } ?: return key.defaultValue
}

fun <T> Storage.setItem(key: StorageKey<T>, value: T) {
    setItem(key.name, key.convertToString(value))
}
