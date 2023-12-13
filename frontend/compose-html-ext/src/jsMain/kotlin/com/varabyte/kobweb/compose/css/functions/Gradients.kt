package com.varabyte.kobweb.compose.css.functions

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.util.titleCamelCaseToKebabCase
import org.jetbrains.compose.web.css.*

interface Gradient : CSSStyleValue {
    class ColorStopsBuilder<T : CSSNumeric> {
        internal sealed class Entry<T : CSSNumeric>(private val entryStr: String) {
            override fun toString() = entryStr
            sealed class Color<T : CSSNumeric>(val value: String) : Entry<T>(value) {
                class Simple<T : CSSNumeric>(value: CSSColorValue) : Color<T>("$value")
                class Stop<T : CSSNumeric>(color: CSSColorValue, stop: T) : Color<T>("$color $stop")
                class StopRange<T : CSSNumeric>(color: CSSColorValue, from: T, to: T) :
                    Color<T>("$color $from $to")
            }

            class Hint<T : CSSNumeric>(val value: T) : Entry<T>("$value")
        }

        private val entries = mutableListOf<Entry<T>>()
        internal fun verifiedEntries(): Array<Entry<T>> {
            check(entries.count { it is Entry.Color } >= 2) { "A gradient should consistent of at least two color entries (an initial color and an end color)" }
            entries.forEachIndexed { i, entry ->
                if (entry is Entry.Hint) {
                    check(entries.getOrNull(i - 1) is Entry.Color && entries.getOrNull(i + 1) is Entry.Color) {
                        "A gradient color midpoint must only be added between two colors"
                    }
                }
            }
            return entries.toTypedArray()
        }

        fun add(color: CSSColorValue) = entries.add(Entry.Color.Simple(color))
        fun add(color: CSSColorValue, stop: T) = entries.add(Entry.Color.Stop(color, stop))
        fun add(color: CSSColorValue, from: T, to: T) = entries.add(Entry.Color.StopRange(color, from, to))

        fun setMidpoint(hint: T) = entries.add(Entry.Hint(hint))
    }
}

typealias LengthColorStopsBuilder = Gradient.ColorStopsBuilder<CSSLengthOrPercentageNumericValue>
internal typealias LengthColorStopsBuilderEntry = Gradient.ColorStopsBuilder.Entry<CSSLengthOrPercentageNumericValue>
typealias AngleColorStopsBuilder = Gradient.ColorStopsBuilder<CSSAngleNumericValue>
internal typealias AngleColorStopsBuilderEntry = Gradient.ColorStopsBuilder.Entry<CSSAngleNumericValue>

// region linear gradient: https://developer.mozilla.org/en-US/docs/Web/CSS/gradient/linear-gradient

sealed class LinearGradient(private val gradientStr: String) : Gradient {
    enum class Direction {
        ToTop,
        ToTopRight,
        ToRight,
        ToBottomRight,
        ToBottom,
        ToBottomLeft,
        ToLeft,
        ToTopLeft;

        override fun toString() = when (this) {
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

    override fun toString() = "linear-gradient($gradientStr)"

    internal class Default internal constructor(vararg entries: LengthColorStopsBuilderEntry) :
        LinearGradient(entries.joinToString())

    internal class ByDirection internal constructor(dir: Direction, vararg entries: LengthColorStopsBuilderEntry) :
        LinearGradient("$dir, ${entries.joinToString()}")

    internal class ByAngle internal constructor(
        angle: CSSAngleNumericValue,
        vararg entries: LengthColorStopsBuilderEntry
    ) :
        LinearGradient("$angle, ${entries.joinToString()}")
}

fun linearGradient(dir: LinearGradient.Direction, init: LengthColorStopsBuilder.() -> Unit): LinearGradient {
    return LengthColorStopsBuilder().apply(init).let {
        LinearGradient.ByDirection(dir, *it.verifiedEntries())
    }
}

fun linearGradient(angle: CSSAngleNumericValue, init: LengthColorStopsBuilder.() -> Unit): LinearGradient {
    return LengthColorStopsBuilder().apply(init).let {
        LinearGradient.ByAngle(angle, *it.verifiedEntries())
    }
}

fun linearGradient(init: LengthColorStopsBuilder.() -> Unit): LinearGradient {
    return LengthColorStopsBuilder().apply(init).let {
        LinearGradient.Default(*it.verifiedEntries())
    }
}

// Using the builder is flexible, but provide some useful defaults for common cases

fun linearGradient(dir: LinearGradient.Direction, from: CSSColorValue, to: CSSColorValue) = linearGradient(dir) {
    add(from)
    add(to)
}

fun linearGradient(angle: CSSAngleNumericValue, from: CSSColorValue, to: CSSColorValue) = linearGradient(angle) {
    add(from)
    add(to)
}

fun linearGradient(from: CSSColorValue, to: CSSColorValue) = linearGradient {
    add(from)
    add(to)
}

// endregion

// region radial gradient: https://developer.mozilla.org/en-US/docs/Web/CSS/gradient/radial-gradient

sealed class RadialGradient(private val gradientStr: String) : Gradient {
    sealed class Shape(private val value: String) {
        override fun toString() = value

        class Circle private constructor(args: String) : Shape(buildString {
            append("circle"); append(args)
        }) {
            constructor() : this("")
            constructor(radius: CSSLengthNumericValue) : this(buildString {
                append(' ')
                append(radius)
            })

            constructor(extent: Extent) : this(buildString {
                append(' ')
                append(extent)
            })
        }

        class Ellipse private constructor(args: String) : Shape(buildString {
            append("ellipse"); append(args)
        }) {
            constructor() : this("")
            constructor(
                radiusX: CSSLengthOrPercentageNumericValue,
                radiusY: CSSLengthOrPercentageNumericValue
            ) : this(buildString {
                append(' ')
                append(radiusX)
                append(' ')
                append(radiusY)
            })

            constructor(extent: Extent) : this(buildString {
                append(' ')
                append(extent)
            })
        }

        companion object {
            val Circle get() = Circle()
            val Ellipse get() = Ellipse()
        }
    }

    enum class Extent {
        ClosestSide,
        ClosestCorner,
        FarthestSide,
        FarthestCorner;

        override fun toString() = name.titleCamelCaseToKebabCase()
    }

    override fun toString() = "radial-gradient($gradientStr)"

    internal class Default internal constructor(position: CSSPosition?, vararg entries: LengthColorStopsBuilderEntry) :
        RadialGradient(buildString {
            if (position != null) {
                if (this.isNotEmpty()) append(' ')

                append("at $position")
            }
            if (this.isNotEmpty()) {
                append(", ")
            }
            append(entries.joinToString())
        })

    internal class ByShape internal constructor(
        shape: Shape,
        position: CSSPosition?,
        vararg entries: LengthColorStopsBuilderEntry
    ) :
        RadialGradient(buildString {
            append(shape.toString())
            if (position != null) {
                append(" at $position")
            }
            append(", ")
            append(entries.joinToString())
        })
}

fun radialGradient(
    shape: RadialGradient.Shape,
    position: CSSPosition? = null,
    init: LengthColorStopsBuilder.() -> Unit
): RadialGradient {
    return LengthColorStopsBuilder().apply(init).let {
        RadialGradient.ByShape(shape, position, *it.verifiedEntries())
    }
}

fun radialGradient(position: CSSPosition? = null, init: LengthColorStopsBuilder.() -> Unit): RadialGradient {
    return LengthColorStopsBuilder().apply(init).let {
        RadialGradient.Default(position, *it.verifiedEntries())
    }
}

// Using the builder is flexible, but provide some useful defaults for common cases

fun radialGradient(shape: RadialGradient.Shape, from: CSSColorValue, to: CSSColorValue, position: CSSPosition? = null) =
    radialGradient(shape, position) {
        add(from)
        add(to)
    }

fun radialGradient(from: CSSColorValue, to: CSSColorValue, position: CSSPosition? = null) = radialGradient(position) {
    add(from)
    add(to)
}

// endregion

// region conic gradient: https://developer.mozilla.org/en-US/docs/Web/CSS/gradient/conic-gradient

sealed class ConicGradient(private val gradientStr: String) : Gradient {
    override fun toString() = "conic-gradient($gradientStr)"

    internal class Default internal constructor(position: CSSPosition?, vararg entries: AngleColorStopsBuilderEntry) :
        ConicGradient(buildString {
            if (position != null) {
                append("at $position")
            }
            if (this.isNotEmpty()) {
                append(", ")
            }
            append(entries.joinToString())
        })

    internal class ByAngle internal constructor(
        angle: CSSAngleNumericValue,
        position: CSSPosition?,
        vararg entries: AngleColorStopsBuilderEntry
    ) :
        ConicGradient(buildString {
            append("from $angle")
            if (position != null) {
                append(" at $position")
            }
            append(", ")
            append(entries.joinToString())
        })
}

fun conicGradient(
    angle: CSSAngleNumericValue,
    position: CSSPosition? = null,
    init: AngleColorStopsBuilder.() -> Unit
): ConicGradient {
    return AngleColorStopsBuilder().apply(init).let {
        ConicGradient.ByAngle(angle, position, *it.verifiedEntries())
    }
}

fun conicGradient(position: CSSPosition? = null, init: AngleColorStopsBuilder.() -> Unit): ConicGradient {
    return AngleColorStopsBuilder().apply(init).let {
        ConicGradient.Default(position, *it.verifiedEntries())
    }
}

// Using the builder is flexible, but provide some useful defaults for common cases

fun conicGradient(angle: CSSAngleNumericValue, from: CSSColorValue, to: CSSColorValue, position: CSSPosition? = null) =
    conicGradient(angle, position) {
        add(from)
        add(to)
    }

fun conicGradient(from: CSSColorValue, to: CSSColorValue, position: CSSPosition? = null) = conicGradient(position) {
    add(from)
    add(to)
}

// endregion
