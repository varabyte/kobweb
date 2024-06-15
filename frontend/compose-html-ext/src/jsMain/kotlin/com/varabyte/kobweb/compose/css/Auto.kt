package com.varabyte.kobweb.compose.css

/**
 * A CSS value that represents the "auto" keyword and can be used anywhere that accepts a length argument.
 *
 * For example:
 *
 * ```
 * margin(top = autoLength, bottom = 1.cssRem)
 * ```
 *
 * Note that this can fail silently if you try to use it in a context that doesn't support it.
 */
inline val autoLength: CSSLengthNumericValue
    get() = "auto".unsafeCast<CSSLengthNumericValue>()
