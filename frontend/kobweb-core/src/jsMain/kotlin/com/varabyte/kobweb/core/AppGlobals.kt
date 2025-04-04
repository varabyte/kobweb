package com.varabyte.kobweb.core

import kotlinx.browser.window
import org.w3c.dom.url.URLSearchParams

/**
 * A list of globals for this app.
 *
 * You can register them in your build script, for example:
 *
 * ```
 * kobweb {
 *   app {
 *     globals.putAll(mapOf(
 *       "author" to "bitspittle",
 *       "version" to "v1234.5678"
 *     ))
 *   )
 * }
 * ```
 *
 * And then fetch them in your own site:
 *
 * ```
 * AppGlobals.getValue("author") // "bitspittle"
 * ```
 *
 * Consider creating a type-safe extensions for your constants:
 *
 * ```
 * val AppGlobals.author get() = AppGlobals.getValue("author")
 * val AppGlobals.version get() = AppGlobals.getValue("version")
 * ```
 *
 * or, if you prefer, a wrapper object:
 *
 * ```
 * object SiteGlobals {
 *    val author = AppGlobals.getValue("author")
 *    val version = AppGlobals.getValue("version")
 * }
 * ```
 */
object AppGlobals {
    private lateinit var _values: Map<String, String>

    /**
     * Initialize the backing data values for this class.
     *
     * This method can only be called once and should not be called by users. It is called in the `main.kt` code
     * generated by the Kobweb Application plugin.
     */
    fun initialize(values: Map<String, String>) {
        check(!this::_values.isInitialized) { "Cannot initialize AppGlobals more than once" }
        _values = values
    }

    operator fun get(key: String): String? = _values[key]
    fun getValue(key: String): String = _values.getValue(key)
}

val AppGlobals.title get() = AppGlobals.getValue("title")

/**
 * A property which indicates if we are currently running this site as part of a Kobweb export.
 *
 * While it should be rare that you'll need to use it, it can be useful to check if you want to avoid doing some
 * side effect that shouldn't happen at export time, like sending page visit analytics to a server for example.
 */
val AppGlobals.isExporting get() = URLSearchParams(window.location.search).has("_kobwebIsExporting")