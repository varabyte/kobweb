package com.varabyte.kobweb.compose.css.functions

import com.varabyte.kobweb.compose.css.CSSPosition
import com.varabyte.kobweb.compose.util.titleCamelCaseToKebabCase
import org.jetbrains.compose.web.css.*

interface Gradient : CSSStyleValue

// region linear gradient: https://developer.mozilla.org/en-US/docs/Web/CSS/gradient/linear-gradient

sealed class LinearGradient(private val paramsStr: String) : Gradient {
    enum class Direction {
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

    class ParamsBuilder {
        private val params = mutableListOf<Param>()
        internal fun verifiedParams(): Array<Param> {
            check(params.count { it is Param.Color } >= 2) { "A linear gradient should consistent of at least two color entries (an initial color and an end color)"}
            params.forEachIndexed { i, param ->
                if (param is Param.Hint) {
                    check(params.getOrNull(i - 1) is Param.Color && params.getOrNull(i + 1) is Param.Color) {
                        "A gradient color midpoint must only be added between two colors"
                    }
                }
            }
            return params.toTypedArray()
        }

        fun add(color: CSSColorValue) = params.add(Param.Color.Simple(color))
        fun add(color: CSSColorValue, stop: CSSLengthOrPercentageValue) = params.add(Param.Color.Stop(color, stop))
        fun add(color: CSSColorValue, from: CSSLengthOrPercentageValue, to: CSSLengthOrPercentageValue) = params.add(Param.Color.StopRange(color, from, to))
        fun setMidpoint(hint: CSSLengthOrPercentageValue) = params.add(Param.Hint(hint))
    }

    internal class Default internal constructor(vararg params: Param) : LinearGradient(params.joinToString())
    internal class ByDirection internal constructor(dir: Direction, vararg params: Param) : LinearGradient("$dir, ${params.joinToString()}")
    internal class ByAngle internal constructor(angle: CSSAngleValue, vararg params: Param) : LinearGradient("$angle, ${params.joinToString()}")
}

fun linearGradient(dir: LinearGradient.Direction, init: LinearGradient.ParamsBuilder.() -> Unit): LinearGradient {
    return LinearGradient.ParamsBuilder().apply(init).let {
        LinearGradient.ByDirection(dir, *it.verifiedParams())
    }
}

fun linearGradient(angle: CSSAngleValue, init: LinearGradient.ParamsBuilder.() -> Unit): LinearGradient {
    return LinearGradient.ParamsBuilder().apply(init).let {
        LinearGradient.ByAngle(angle, *it.verifiedParams())
    }
}

fun linearGradient(init: LinearGradient.ParamsBuilder.() -> Unit): LinearGradient {
    return LinearGradient.ParamsBuilder().apply(init).let {
        LinearGradient.Default(*it.verifiedParams())
    }
}

// Using the builder is flexible, but provide some useful defaults for common cases

fun linearGradient(dir: LinearGradient.Direction, from: CSSColorValue, to: CSSColorValue) = linearGradient(dir) {
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

// endregion

// region linear gradient: https://developer.mozilla.org/en-US/docs/Web/CSS/gradient/radial-gradient

sealed class RadialGradient(private val paramsStr: String) : Gradient {
    sealed class Shape(private val value: String) {
        override fun toString() = value

        class Circle(radius: CSSLengthValue? = null) : Shape(buildString {
            append("circle")
            if (radius != null) {
                append(' ')
                append(radius)
            }
        })

        class Ellipse private constructor(
            radiusX: CSSLengthOrPercentageValue? = null,
            radiusY: CSSLengthOrPercentageValue? = null
        ) : Shape(buildString {
            append("ellipse")
            if (radiusX != null && radiusY != null) {
                append(' ')
                append(radiusX)
                append(' ')
                append(radiusY)
            }
        }) {
            @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE") // Cast is done so constructor avoids calling itself
            constructor(
                radiusX: CSSLengthOrPercentageValue,
                radiusY: CSSLengthOrPercentageValue
            ) : this(radiusX as CSSLengthOrPercentageValue?, radiusY as CSSLengthOrPercentageValue?)

            constructor() : this(null, null)
        }

        companion object {
            val Circle = Circle()
            val Ellipse = Ellipse()
        }
    }

    enum class Position {
        Top,
        Bottom,
        Left,
        Right,
        Center;

        override fun toString() = name.lowercase()
    }

    enum class Extent {
        ClosestSide,
        ClosestCorner,
        FarthestSide,
        FarthestCorner;

        override fun toString() = name.titleCamelCaseToKebabCase()
    }

    override fun toString() = "radial-gradient($paramsStr)"

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

    class ParamsBuilder {
        private val params = mutableListOf<Param>()
        internal fun verifiedParams(): Array<Param> {
            check(params.count { it is Param.Color } >= 2) { "A linear gradient should consistent of at least two color entries (an initial color and an end color)"}
            params.forEachIndexed { i, param ->
                if (param is Param.Hint) {
                    check(params.getOrNull(i - 1) is Param.Color && params.getOrNull(i + 1) is Param.Color) {
                        "A gradient color midpoint must only be added between two colors"
                    }
                }
            }
            return params.toTypedArray()
        }

        fun add(color: CSSColorValue) = params.add(Param.Color.Simple(color))
        fun add(color: CSSColorValue, stop: CSSLengthOrPercentageValue) = params.add(Param.Color.Stop(color, stop))
        fun add(color: CSSColorValue, from: CSSLengthOrPercentageValue, to: CSSLengthOrPercentageValue) = params.add(Param.Color.StopRange(color, from, to))
        fun setMidpoint(hint: CSSLengthOrPercentageValue) = params.add(Param.Hint(hint))
    }

    internal class Default internal constructor(position: CSSPosition?, extent: Extent?, vararg params: Param) :
        RadialGradient(buildString {
            if (extent != null) {
                append(extent.toString())
            }
            if (position != null) {
                if (this.isNotEmpty()) append(' ')

                append("at $position")
            }
            if (this.isNotEmpty()) {
                append(", ")
            }
            append(params.joinToString())
        })

    internal class ByShape internal constructor(shape: Shape, position: CSSPosition?, extent: Extent?, vararg params: Param) :
        RadialGradient(buildString {
            append(shape.toString())
            if (extent != null) {
                append(' ')
                append(extent.toString())
            }
            if (position != null) {
                append(" at $position")
            }
            append(", ")
            append(params.joinToString())
        })
}

fun radialGradient(shape: RadialGradient.Shape, position: CSSPosition? = null, extent: RadialGradient.Extent? = null, init: RadialGradient.ParamsBuilder.() -> Unit): RadialGradient {
    return RadialGradient.ParamsBuilder().apply(init).let {
        RadialGradient.ByShape(shape, position, extent, *it.verifiedParams())
    }
}

fun radialGradient(position: CSSPosition? = null, extent: RadialGradient.Extent? = null, init: RadialGradient.ParamsBuilder.() -> Unit): RadialGradient {
    return RadialGradient.ParamsBuilder().apply(init).let {
        RadialGradient.Default(position, extent, *it.verifiedParams())
    }
}

// Using the builder is flexible, but provide some useful defaults for common cases

fun radialGradient(shape: RadialGradient.Shape, from: CSSColorValue, to: CSSColorValue, position: CSSPosition? = null, extent: RadialGradient.Extent? = null) = radialGradient(shape, position, extent) {
    add(from)
    add(to)
}

fun radialGradient(from: CSSColorValue, to: CSSColorValue, position: CSSPosition? = null, extent: RadialGradient.Extent? = null) = radialGradient(position, extent) {
    add(from)
    add(to)
}

// endregion
