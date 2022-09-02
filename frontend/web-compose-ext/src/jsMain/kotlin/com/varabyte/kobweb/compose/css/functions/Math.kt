package com.varabyte.kobweb.compose.css.functions

import org.jetbrains.compose.web.css.*

// Code initially copied from:
// https://github.com/Ayfri/Ayfri.github.io/blob/master/src/main/kotlin/style/utils/CSSFunctions.kt
// Thank you @ https://github.com/Ayfri !

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/clamp
data class CSSClamp<T : CSSUnit>(
    val min: CSSNumericValue<out T>,
    val value: CSSNumericValue<out T>,
    val max: CSSNumericValue<out T>,
) : CSSNumericValue<T> {
    override fun toString() = "clamp($min, $value, $max)"
}
fun clamp(
    min: CSSNumericValue<out CSSUnit>,
    value: CSSNumericValue<out CSSUnit>,
    max: CSSNumericValue<out CSSUnit>,
) = CSSClamp(min, value, max)


// See: https://developer.mozilla.org/en-US/docs/Web/CSS/min
class CSSMin<T : CSSUnit>(vararg val values: CSSNumericValue<out T>) : CSSNumericValue<T> {
    override fun toString() = "min(${values.joinToString(", ")})"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CSSMin<*>) return false

        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode() = values.contentHashCode()
}
fun min(vararg values: CSSNumericValue<out CSSUnit>) = CSSMin(*values)

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/min
class CSSMax<T : CSSUnit>(vararg val values: CSSNumericValue<out T>) : CSSNumericValue<T> {
    override fun toString() = "max(${values.joinToString(", ")})"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CSSMax<*>) return false

        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode() = values.contentHashCode()
}
fun max(vararg values: CSSNumericValue<out CSSUnit>) = CSSMax(*values)
