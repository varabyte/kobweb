package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.internal.keyword
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
interface CssGlobalValues<T : StylePropertyValue> {
    val Inherit get() = keyword<T>("inherit")
    val Initial get() = keyword<T>("initial")
    val Revert get() = keyword<T>("revert")
    val RevertLayer get() = keyword<T>("revert-layer")
    val Unset get() = keyword<T>("unset")
}