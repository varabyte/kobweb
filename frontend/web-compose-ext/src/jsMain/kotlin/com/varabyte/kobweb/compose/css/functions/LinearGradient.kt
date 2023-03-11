package com.varabyte.kobweb.compose.css.functions

import org.jetbrains.compose.web.css.*

enum class GradientDirection {
    ToTop,
    ToTopRight,
    ToRight,
    ToBottomRight,
    ToBottom,
    ToBottomLeft,
    ToLeft,
    ToTopLeft;

    override fun toString() = when(this) {
        ToTop -> "to top"
        ToTopRight -> "to top right"
        ToRight -> "to right"
        ToBottomRight -> "to bottom right"
        ToBottom -> "to bottom"
        ToBottomLeft -> "to bottom left"
        ToLeft -> "to left"
        ToTopLeft -> "to top left"
    }
}

sealed class LinearGradient(private val paramsStr: String) : CSSStyleValue {
    override fun toString() = "linear-gradient($paramsStr)"

    internal sealed class Param(private val paramStr: String) {
        override fun toString() = paramStr
        open class Color(val value: String) : Param(value) {
            class Simple(value: CSSColorValue) : Color("$value")
            class Stop(color: CSSColorValue, stop: CSSLengthOrPercentageValue) : Color("$color $stop")
            class StopRange(color: CSSColorValue, from: CSSLengthOrPercentageValue, to: CSSLengthOrPercentageValue) :
                Color("$color $from $to")
        }
        class Hint(val value: CSSLengthOrPercentageValue) : Param("$value")
    }


    internal class Default internal constructor(vararg params: Param) : LinearGradient(params.joinToString())
    internal class ByDirection internal constructor(dir: GradientDirection, vararg params: Param) : LinearGradient("$dir, ${params.joinToString()}")
    internal class ByAngle internal constructor(angle: CSSAngleValue, vararg params: Param) : LinearGradient("$angle, ${params.joinToString()}")
}

class LinearGradientParamsBuilder {
    private val params = mutableListOf<LinearGradient.Param>()
    internal fun verifiedParams(): Array<LinearGradient.Param> {
        check(params.count { it is LinearGradient.Param.Color } >= 2) { "A linear gradient should consistent of at least three color entries"}
        params.forEachIndexed { i, param ->
            if (param is LinearGradient.Param.Hint) {
                check(params.getOrNull(i - 1) is LinearGradient.Param.Color && params.getOrNull(i + 1) is LinearGradient.Param.Color) {
                    "A gradient color midpoint must only be added between two colors"
                }
            }
        }
        return params.toTypedArray()
    }

    fun add(color: CSSColorValue) = params.add(LinearGradient.Param.Color.Simple(color))
    fun add(color: CSSColorValue, stop: CSSLengthOrPercentageValue) = params.add(LinearGradient.Param.Color.Stop(color, stop))
    fun add(color: CSSColorValue, from: CSSLengthOrPercentageValue, to: CSSLengthOrPercentageValue) = params.add(LinearGradient.Param.Color.StopRange(color, from, to))
    fun setMidpoint(hint: CSSLengthOrPercentageValue) = params.add(LinearGradient.Param.Hint(hint))
}

fun linearGradient(dir: GradientDirection, init: LinearGradientParamsBuilder.() -> Unit): LinearGradient {
    return LinearGradientParamsBuilder().apply(init).let {
        LinearGradient.ByDirection(dir, *it.verifiedParams())
    }
}

fun linearGradient(angle: CSSAngleValue, init: LinearGradientParamsBuilder.() -> Unit): LinearGradient {
    return LinearGradientParamsBuilder().apply(init).let {
        LinearGradient.ByAngle(angle, *it.verifiedParams())
    }
}

fun linearGradient(init: LinearGradientParamsBuilder.() -> Unit): LinearGradient {
    return LinearGradientParamsBuilder().apply(init).let {
        LinearGradient.Default(*it.verifiedParams())
    }
}

// Using the builder is flexible, but provide some useful defaults for common cases

fun linearGradient(dir: GradientDirection, from: CSSColorValue, to: CSSColorValue) = linearGradient(dir) {
    add(from)
    add(to)
}

fun linearGradient(angle: CSSAngleValue, from: CSSColorValue, to: CSSColorValue) = linearGradient(angle) {
    add(from)
    add(to)
}

fun linearGradient(from: CSSColorValue, to: CSSColorValue) = linearGradient {
    add(from)
    add(to)
}
