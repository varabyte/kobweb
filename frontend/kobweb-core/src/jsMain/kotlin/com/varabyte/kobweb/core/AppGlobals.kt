package com.varabyte.kobweb.core

import androidx.compose.runtime.*

/**
 * A list of globals for this app.
 *
 * You can register them in your build script, for example:
 *
 * ```
 * kobweb {
 *   app {
 *     globals.putAll(
 *       "author" to "bitspittle",
 *       "version" to "v1234.5678"
 *     )
 *   )
 * }
 * ```
 */
val AppGlobals
    @Composable
    @ReadOnlyComposable
    get() = AppGlobalsLocal.current

val AppGlobalsLocal = compositionLocalOf<Map<String, String>> { mapOf() }
