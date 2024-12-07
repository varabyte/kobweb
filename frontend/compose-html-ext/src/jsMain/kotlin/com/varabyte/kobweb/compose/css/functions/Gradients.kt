package com.varabyte.kobweb.compose.css.functions

import com.varabyte.kobweb.browser.util.titleCamelCaseToKebabCase
import com.varabyte.kobweb.compose.css.*
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

    internal sealed class Base<T>(
        dir: T?,
        interpolation: ColorInterpolationMethod?,
        vararg entries: LengthColorStopsBuilderEntry
    ) : LinearGradient(buildString {
        append(listOfNotNull(dir, interpolation).joinToString(" "))
        if (this.isNotEmpty()) {
            append(", ")
        }
        append(entries.joinToString())
    })

    internal class ByDirection(
        dir: Direction?,
        interpolation: ColorInterpolationMethod?,
        vararg entries: LengthColorStopsBuilderEntry
    ) : Base<Direction>(dir, interpolation, *entries)

    internal class ByAngle(
        angle: CSSAngleNumericValue?,
        interpolation: ColorInterpolationMethod?,
        vararg entries: LengthColorStopsBuilderEntry
    ) : Base<CSSAngleNumericValue>(angle, interpolation, *entries)
}

fun linearGradient(
    dir: LinearGradient.Direction?,
    interpolation: ColorInterpolationMethod? = null,
    init: LengthColorStopsBuilder.() -> Unit
): LinearGradient {
    return LengthColorStopsBuilder().apply(init).let {
        LinearGradient.ByDirection(dir, interpolation, *it.verifiedEntries())
    }
}

fun linearGradient(
    angle: CSSAngleNumericValue?,
    interpolation: ColorInterpolationMethod? = null,
    init: LengthColorStopsBuilder.() -> Unit
): LinearGradient {
    return LengthColorStopsBuilder().apply(init).let {
        LinearGradient.ByAngle(angle, interpolation, *it.verifiedEntries())
    }
}

fun linearGradient(
    interpolation: ColorInterpolationMethod? = null,
    init: LengthColorStopsBuilder.() -> Unit
): LinearGradient {
    return LengthColorStopsBuilder().apply(init).let {
        LinearGradient.ByDirection(null, interpolation, *it.verifiedEntries())
    }
}

// Using the builder is flexible, but provide overloads for the common 2-color case

fun linearGradient(
    from: CSSColorValue,
    to: CSSColorValue,
    dir: LinearGradient.Direction? = null,
    interpolation: ColorInterpolationMethod? = null,
) = linearGradient(dir, interpolation) {
    add(from)
    add(to)
}

fun linearGradient(
    from: CSSColorValue,
    to: CSSColorValue,
    angle: CSSAngleNumericValue? = null,
    interpolation: ColorInterpolationMethod? = null,
) = linearGradient(angle, interpolation) {
    add(from)
    add(to)
}

// This overload is provided to avoid ambiguity issues between the previous two
fun linearGradient(
    from: CSSColorValue,
    to: CSSColorValue,
    interpolation: ColorInterpolationMethod? = null,
) = linearGradient(interpolation) {
    add(from)
    add(to)
}

@Deprecated(
    "The arguments have been reordered to accept the colors first, as this allows passing all of the optional arguments as well.",
    ReplaceWith("linearGradient(from, to, dir)")
)
fun linearGradient(dir: LinearGradient.Direction, from: CSSColorValue, to: CSSColorValue) =
    linearGradient(from, to, dir)

@Deprecated(
    "The arguments have been reordered to accept the colors first, as this allows passing all of the optional arguments as well.",
    ReplaceWith("linearGradient(from, to, angle)")
)
fun linearGradient(angle: CSSAngleNumericValue, from: CSSColorValue, to: CSSColorValue) =
    linearGradient(from, to, angle)

// endregion

// region radial gradient: https://developer.mozilla.org/en-US/docs/Web/CSS/gradient/radial-gradient

class RadialGradient private constructor(private val gradientStr: String) : Gradient {
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

    internal constructor(
        shape: Shape?,
        position: CSSPosition?,
        interpolation: ColorInterpolationMethod?,
        vararg entries: LengthColorStopsBuilderEntry
    ) : this(buildString {
        append(listOfNotNull(shape, position?.let { "at $it" }, interpolation).joinToString(" "))
        if (this.isNotEmpty()) {
            append(", ")
        }
        append(entries.joinToString())
    })
}

fun radialGradient(
    shape: RadialGradient.Shape?,
    position: CSSPosition? = null,
    interpolation: ColorInterpolationMethod? = null,
    init: LengthColorStopsBuilder.() -> Unit
): RadialGradient {
    return LengthColorStopsBuilder().apply(init).let {
        RadialGradient(shape, position, interpolation, *it.verifiedEntries())
    }
}

fun radialGradient(
    position: CSSPosition?,
    interpolation: ColorInterpolationMethod? = null,
    init: LengthColorStopsBuilder.() -> Unit
): RadialGradient {
    return LengthColorStopsBuilder().apply(init).let {
        RadialGradient(null, position, interpolation, *it.verifiedEntries())
    }
}

fun radialGradient(
    interpolation: ColorInterpolationMethod? = null,
    init: LengthColorStopsBuilder.() -> Unit
): RadialGradient {
    return LengthColorStopsBuilder().apply(init).let {
        RadialGradient(null, null, interpolation, *it.verifiedEntries())
    }
}

// Using the builder is flexible, but provide an overload for the common 2-color case

fun radialGradient(
    from: CSSColorValue,
    to: CSSColorValue,
    shape: RadialGradient.Shape? = null,
    position: CSSPosition? = null,
    interpolation: ColorInterpolationMethod? = null,
) = radialGradient(shape, position, interpolation) {
    add(from)
    add(to)
}

@Deprecated(
    "The arguments have been reordered to accept the colors first, as this allows passing all of the optional arguments as well.",
    ReplaceWith("radialGradient(from, to, shape, position)")
)
fun radialGradient(shape: RadialGradient.Shape, from: CSSColorValue, to: CSSColorValue, position: CSSPosition? = null) =
    radialGradient(from, to, shape, position)

// endregion

// region conic gradient: https://developer.mozilla.org/en-US/docs/Web/CSS/gradient/conic-gradient

class ConicGradient private constructor(private val gradientStr: String) : Gradient {
    override fun toString() = "conic-gradient($gradientStr)"

    internal constructor(
        angle: CSSAngleNumericValue?,
        position: CSSPosition?,
        interpolation: ColorInterpolationMethod?,
        vararg entries: AngleColorStopsBuilderEntry
    ) : this(buildString {
        append(listOfNotNull(angle, position?.let { "at $it" }, interpolation).joinToString(" "))
        if (this.isNotEmpty()) {
            append(", ")
        }
        append(entries.joinToString())
    })
}

fun conicGradient(
    angle: CSSAngleNumericValue?,
    position: CSSPosition? = null,
    interpolation: ColorInterpolationMethod? = null,
    init: AngleColorStopsBuilder.() -> Unit
): ConicGradient {
    return AngleColorStopsBuilder().apply(init).let {
        ConicGradient(angle, position, interpolation, *it.verifiedEntries())
    }
}

fun conicGradient(
    position: CSSPosition? = null,
    interpolation: ColorInterpolationMethod? = null,
    init: AngleColorStopsBuilder.() -> Unit
): ConicGradient {
    return AngleColorStopsBuilder().apply(init).let {
        ConicGradient(null, position, interpolation, *it.verifiedEntries())
    }
}

fun conicGradient(
    interpolation: ColorInterpolationMethod? = null,
    init: AngleColorStopsBuilder.() -> Unit
): ConicGradient {
    return AngleColorStopsBuilder().apply(init).let {
        ConicGradient(null, null, interpolation, *it.verifiedEntries())
    }
}

// Using the builder is flexible, but provide an overload for the common 2-color case

fun conicGradient(
    from: CSSColorValue,
    to: CSSColorValue,
    angle: CSSAngleNumericValue? = null,
    position: CSSPosition? = null,
    interpolation: ColorInterpolationMethod? = null,
) = conicGradient(angle, position, interpolation) {
    add(from)
    add(to)
}

@Deprecated(
    "The arguments have been reordered to accept the colors first, as this allows passing all of the optional arguments as well.",
    ReplaceWith("conicGradient(from, to, angle, position)")
)
fun conicGradient(angle: CSSAngleNumericValue, from: CSSColorValue, to: CSSColorValue, position: CSSPosition? = null) =
    conicGradient(from, to, angle, position)

// endregion
