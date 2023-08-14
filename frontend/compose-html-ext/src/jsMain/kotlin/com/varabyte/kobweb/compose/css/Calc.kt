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

    /** An extension of [Number] which uses CSS's calc() function to represent operations. */
    class CalcNum<T : Number> internal constructor(private val value: String) : Number() {
        override fun toString(): String = value

        override fun toInt(): Int = value.unsafeCast<Int>()
        override fun toLong(): Long = value.unsafeCast<Long>()
        override fun toFloat(): Float = value.unsafeCast<Float>()
        override fun toDouble(): Double = value.unsafeCast<Double>()
        override fun toByte(): Byte = throw UnsupportedOperationException()
        override fun toChar(): Char = throw UnsupportedOperationException()
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
