package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*
import kotlin.experimental.ExperimentalTypeInference

// While operations with CSS numerics are already supported, the existing implementation does not properly handle using
// StyleVariables (CSS Variables) in calculations. So, we implement explicit calc() functions for handling these cases.

/** Expresses a CSS calculation for a [CSSNumericValue], particularly useful when it involves a [StyleVariable]. */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
fun <T : CSSNumericValue<*>> calc(action: CalcScope.() -> T): T = with(CalcScopeInstance, action)

/** Expresses a CSS calculation for a [Number], particularly useful when it involves a [StyleVariable]. */
fun <V : Number, T : CalcScope.CalcNum<V>> calc(action: CalcScope.() -> T): V =
    with(CalcScopeInstance, action).unsafeCast<V>()

/**
 * Expresses a CSS calculation for a [Number], particularly useful when it involves a [StyleVariable].
 *
 * @see [CalcScope.CalcNum.toInt]
 * @see [CalcScope.CalcNum.toLong]
 * @see [CalcScope.CalcNum.toFloat]
 * @see [CalcScope.CalcNum.toDouble]
 */
fun <T : CalcScope.CalcNum<*>> calc(action: CalcScope.() -> T): CalcScope.CalcNum<*> =
    with(CalcScopeInstance, action).unsafeCast<CalcScope.CalcNum<*>>()

private object CalcScopeInstance : CalcScope

sealed interface CalcScope {
    // override the "smart" operations from org.jetbrains.compose.web.css.CSSOperations.kt to use calc() instead
    operator fun <T : CSSUnit> CSSSizeValue<T>.times(b: Number): CSSSizeValue<T> =
        "calc($this * $b)".unsafeCast<CSSSizeValue<T>>()

    operator fun <T : CSSUnit> Number.times(unit: CSSSizeValue<T>): CSSSizeValue<T> =
        "calc($this * $unit)".unsafeCast<CSSSizeValue<T>>()

    operator fun <T : CSSUnit> CSSSizeValue<T>.div(num: Number): CSSSizeValue<T> =
        "calc($this / $num)".unsafeCast<CSSSizeValue<T>>()

    operator fun <T : CSSUnit> CSSSizeValue<T>.plus(b: CSSSizeValue<T>): CSSSizeValue<T> =
        "calc($this + $b)".unsafeCast<CSSSizeValue<T>>()

    operator fun <T : CSSUnit> CSSSizeValue<T>.minus(b: CSSSizeValue<T>): CSSSizeValue<T> =
        "calc($this - $b)".unsafeCast<CSSSizeValue<T>>()

    operator fun <T : CSSUnit> CSSSizeValue<T>.unaryMinus(): CSSSizeValue<T> =
        "calc(-1 * $this)".unsafeCast<CSSSizeValue<T>>()

    operator fun <T : CSSUnit> CSSSizeValue<T>.unaryPlus(): CSSSizeValue<T> =
        "calc(1 * $this)".unsafeCast<CSSSizeValue<T>>()

    // generic is used to preserve type during operations when possible
    // even though the generic in practice is bounded by Number, we don't explicitly bound it so that its type cannot
    // be assumed to be Number, preventing users from calling things like Number.toInt() that won't work
    /** A wrapper around a [Number] which allows for custom implementation of operations. */
    sealed interface CalcNum<@Suppress("unused") T>

    fun <T : Number> num(num: T): CalcNum<T> = num.unsafeCast<CalcNum<T>>()

    operator fun <T : Number> CalcNum<T>.plus(b: CalcNum<T>): CalcNum<T> = "calc($this + $b)".unsafeCast<CalcNum<T>>()
    operator fun <T : Number> CalcNum<T>.minus(b: CalcNum<T>): CalcNum<T> = "calc($this - $b)".unsafeCast<CalcNum<T>>()
    operator fun <T : Number> CalcNum<T>.times(b: CalcNum<T>): CalcNum<T> = "calc($this * $b)".unsafeCast<CalcNum<T>>()
    operator fun <T : Number> CalcNum<T>.div(b: CalcNum<T>): CalcNum<T> = "calc($this / $b)".unsafeCast<CalcNum<T>>()
    operator fun <T : Number> CalcNum<T>.unaryMinus(): CalcNum<T> = "calc(-1 * $this)".unsafeCast<CalcNum<T>>()
    operator fun <T : Number> CalcNum<T>.unaryPlus(): CalcNum<T> = "calc(1 * $this)".unsafeCast<CalcNum<T>>()

    operator fun CalcNum<*>.plus(b: CalcNum<*>): CalcNum<*> = "calc($this + $b)".unsafeCast<CalcNum<*>>()
    operator fun CalcNum<*>.minus(b: CalcNum<*>): CalcNum<*> = "calc($this - $b)".unsafeCast<CalcNum<*>>()
    operator fun CalcNum<*>.times(b: CalcNum<*>): CalcNum<*> = "calc($this * $b)".unsafeCast<CalcNum<*>>()
    operator fun CalcNum<*>.div(b: CalcNum<*>): CalcNum<*> = "calc($this / $b)".unsafeCast<CalcNum<*>>()

    operator fun <T : CSSUnit> CalcNum<*>.times(b: CSSSizeValue<T>): CSSSizeValue<T> =
        "calc($this * $b)".unsafeCast<CSSSizeValue<T>>()

    operator fun <T : CSSUnit> CSSNumericValue<T>.times(b: CalcNum<*>): CSSSizeValue<T> =
        "calc($this * $b)".unsafeCast<CSSSizeValue<T>>()

    operator fun <T : CSSUnit> CSSNumericValue<T>.div(b: CalcNum<*>): CSSSizeValue<T> =
        "calc($this / $b)".unsafeCast<CSSSizeValue<T>>()
}

fun CalcScope.CalcNum<*>.toInt() = this.unsafeCast<Int>()
fun CalcScope.CalcNum<*>.toLong() = this.unsafeCast<Long>()
fun CalcScope.CalcNum<*>.toFloat() = this.unsafeCast<Float>()
fun CalcScope.CalcNum<*>.toDouble() = this.unsafeCast<Double>()
