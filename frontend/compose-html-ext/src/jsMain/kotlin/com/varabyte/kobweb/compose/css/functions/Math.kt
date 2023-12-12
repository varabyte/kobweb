package com.varabyte.kobweb.compose.css.functions

import org.jetbrains.compose.web.css.*

// Code initially copied (with permission) from:
// https://github.com/Ayfri/Ayfri.github.io/blob/master/src/main/kotlin/style/utils/CSSFunctions.kt
// Thank you @ https://github.com/Ayfri !

/**
 * See: https://developer.mozilla.org/en-US/docs/Web/CSS/clamp
 */
data class CSSClamp<T : CSSUnit>(
    val min: CSSNumericValue<out T>,
    val value: CSSNumericValue<out T>,
    val max: CSSNumericValue<out T>,
) : CSSNumericValue<T> {
    override fun toString() = "clamp($min, $value, $max)"
}

fun <T : CSSUnit> clamp(
    min: CSSNumericValue<out T>,
    value: CSSNumericValue<out T>,
    max: CSSNumericValue<out T>,
) = CSSClamp(min, value, max)


/**
 * See: https://developer.mozilla.org/en-US/docs/Web/CSS/min
 */
class CSSMin<T : CSSUnit>(vararg val values: CSSNumericValue<out T>) : CSSNumericValue<T> {
    override fun toString() = "min(${values.joinToString(", ")})"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CSSMin<*>) return false

        return values.contentEquals(other.values)
    }

    override fun hashCode() = values.contentHashCode()
}

fun <T : CSSUnit> min(vararg values: CSSNumericValue<out T>) = CSSMin(*values)

/**
 * See: https://developer.mozilla.org/en-US/docs/Web/CSS/max
 */
class CSSMax<T : CSSUnit>(vararg val values: CSSNumericValue<out T>) : CSSNumericValue<T> {
    override fun toString() = "max(${values.joinToString(", ")})"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CSSMax<*>) return false

        return values.contentEquals(other.values)
    }

    override fun hashCode() = values.contentHashCode()
}

fun <T : CSSUnit> max(vararg values: CSSNumericValue<out T>) = CSSMax(*values)
