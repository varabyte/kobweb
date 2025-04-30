package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.StylePropertyValue

/**
 * Provides the global values present on EVERY CSS property.
 *
 * The pattern for every CSS property class should include it as a base class to their
 * companion object:
 *
 * ```
 * class ExampleStyle : StylePropertyValue {
 *    companion object : CssGlobalValues<ExampleStyle> {
 *       // remaining property values
 *    }
 * }
 * ```
 *
 * This will make `ExampleStyle.Inherit`, `ExampleStyle.Initial`, etc. available to the API.
 */
@Suppress("PropertyName")
internal interface CssGlobalValues<T : StylePropertyValue> {
    val Inherit get() = "inherit".unsafeCast<T>()
    val Initial get() = "initial".unsafeCast<T>()
    val Revert get() = "revert".unsafeCast<T>()
    val RevertLayer get() = "revert-layer".unsafeCast<T>()
    val Unset get() = "unset".unsafeCast<T>()
}