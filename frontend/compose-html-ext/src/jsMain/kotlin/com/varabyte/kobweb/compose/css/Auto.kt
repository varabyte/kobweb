package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

/**
 * A CSS value that represents the "auto" keyword and can be used anywhere that accepts a numeric argument.
 *
 * For example:
 *
 * ```
 * Modifier.margin(top = numericAuto, bottom = 1.cssRem)
 * ```

 * Note that this can fail silently if you try to use it in a context that doesn't support it.
 */
inline val numericAuto: CSSNumeric
    get() = object : CSSNumericValue<CSSUnit> {
        override fun toString(): String = "auto"
    }
