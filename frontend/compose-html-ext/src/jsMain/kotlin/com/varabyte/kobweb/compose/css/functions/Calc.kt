package com.varabyte.kobweb.compose.css.functions

import com.varabyte.kobweb.compose.css.*
import org.jetbrains.compose.web.css.*
import kotlin.experimental.ExperimentalTypeInference

// While operations with CSS numerics are already supported, the existing implementation does not properly handle using
// StyleVariables (CSS Variables) in calculations. So, we implement explicit calc() functions for handling these cases.

/**
 * Expresses a CSS calculation for a [CSSNumericValue], particularly useful when it involves a [StyleVariable].
 *
 * For example:
 * ```
 * val PaddingVar by StyleVariable(1.cssRem)
 * Modifier.padding(calc { (BasePaddingVar.value() + 2.px) * 2 })
 * ```
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
fun <T : CSSNumericValue<*>> calc(action: CalcScope.() -> T): T = with(CalcScopeInstance, action)

/**
 * Expresses a CSS calculation for a [Number], particularly useful when it involves a [StyleVariable]. To be used in
 * operations, numbers (including those derived from StyleVariables) must be wrapped in a call to [CalcScope.num].
 *
 * For example:
 * ```
 * val BaseLineHeightVar by StyleVariable(1.5)
 * Modifier.lineHeight(calc { num(2) * num(BaseLineHeightVar.value()) })
 * ```
 */
fun <V : Number, T : CalcScope.CalcNum<V>> calc(action: CalcScope.() -> T): V =
    with(CalcScopeInstance, action).unsafeCast<V>()

private object CalcScopeInstance : CalcScope

sealed interface CalcScope {
    // override the "smart" operations from org.jetbrains.compose.web.css.CSSOperations.kt to use calc() instead
    operator fun <T : CSSUnit> CSSNumericValue<T>.times(b: Number): CSSNumericValue<T> =
        "calc($this * $b)".unsafeCast<CSSNumericValue<T>>()

    operator fun <T : CSSUnit> Number.times(unit: CSSNumericValue<T>): CSSNumericValue<T> =
        "calc($this * $unit)".unsafeCast<CSSNumericValue<T>>()

    operator fun <T : CSSUnit> CSSNumericValue<T>.div(num: Number): CSSNumericValue<T> =
        "calc($this / $num)".unsafeCast<CSSNumericValue<T>>()

    operator fun <T : CSSUnit> CSSNumericValue<T>.plus(b: CSSNumericValue<T>): CSSNumericValue<T> =
        "calc($this + $b)".unsafeCast<CSSNumericValue<T>>()

    operator fun <T : CSSUnit> CSSNumericValue<T>.minus(b: CSSNumericValue<T>): CSSNumericValue<T> =
        "calc($this - $b)".unsafeCast<CSSNumericValue<T>>()

    operator fun <T : CSSUnit> CSSNumericValue<T>.unaryMinus(): CSSNumericValue<T> =
        "calc(-1 * $this)".unsafeCast<CSSNumericValue<T>>()

    operator fun <T : CSSUnit> CSSNumericValue<T>.unaryPlus(): CSSNumericValue<T> =
        "calc(1 * $this)".unsafeCast<CSSNumericValue<T>>()

    /** An extension of [Number] which uses CSS's calc() function to represent operations. */
    class CalcNum<T : Number> internal constructor(private val value: String) : Number() {
        override fun toString(): String = value

        override fun toInt(): Int = value.unsafeCast<Int>()
        override fun toLong(): Long = value.unsafeCast<Long>()
        override fun toFloat(): Float = value.unsafeCast<Float>()
        override fun toDouble(): Double = value.unsafeCast<Double>()
        override fun toByte(): Byte = throw UnsupportedOperationException()
        override fun toShort(): Short = throw UnsupportedOperationException()

        // keep the type when the operation involves the same type
        operator fun plus(b: CalcNum<T>): CalcNum<T> = CalcNum("calc($this + $b)")
        operator fun minus(b: CalcNum<T>): CalcNum<T> = CalcNum("calc($this - $b)")
        operator fun times(b: CalcNum<T>): CalcNum<T> = CalcNum("calc($this * $b)")
        operator fun div(b: CalcNum<T>): CalcNum<T> = CalcNum("calc($this / $b)")
        operator fun unaryMinus(): CalcNum<T> = CalcNum("calc(-1 * $this)")
        operator fun unaryPlus(): CalcNum<T> = CalcNum("calc(1 * $this)")

        operator fun plus(b: CalcNum<*>): CalcNum<*> = CalcNum<T>("calc($this + $b)")
        operator fun minus(b: CalcNum<*>): CalcNum<*> = CalcNum<T>("calc($this - $b)")
        operator fun times(b: CalcNum<*>): CalcNum<*> = CalcNum<T>("calc($this * $b)")
        operator fun div(b: CalcNum<*>): CalcNum<*> = CalcNum<T>("calc($this / $b)")
    }

    fun <T : Number> num(num: T): CalcNum<T> = CalcNum(num.toString())
}
