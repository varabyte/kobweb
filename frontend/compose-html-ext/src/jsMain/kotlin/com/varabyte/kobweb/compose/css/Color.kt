// Sealed class private constructors are useful, actually!
@file:Suppress("RedundantVisibilityModifier")

package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*


// https://developer.mozilla.org/en-US/docs/Web/CSS/accent-color
class AccentColor private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword values
        val Auto get() = AccentColor("auto")

        // <color> values
        fun of(color: CSSColorValue) = AccentColor(color.toString())

        // Global values
        val Inherit get() = AccentColor("inherit");
        val Initial get() = AccentColor("initial")
        val Revert get() = AccentColor("revert")

        //        val RevertLayer get() = AccentColor("revert-layer")
        val Unset get() = AccentColor("unset")
    }
}

fun StyleScope.accentColor(accentColor: AccentColor) {
    property("accent-color", accentColor)
}

fun StyleScope.accentColor(color: CSSColorValue) {
    property("accent-color", AccentColor.of(color))
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/color-scheme
class ColorScheme private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword values
        val Normal get() = ColorScheme("normal")
        val Light get() = ColorScheme("light")
        val Dark get() = ColorScheme("dark")
        val LightDark get() = ColorScheme("light dark")
        val DarkLight get() = ColorScheme("dark light")
        val OnlyLight get() = ColorScheme("only light")
        val OnlyDark get() = ColorScheme("only dark")

        // Global values
        val Inherit get() = ColorScheme("inherit")
        val Initial get() = ColorScheme("initial")
        val Revert get() = ColorScheme("revert")
        val Unset get() = ColorScheme("unset")
    }
}

fun StyleScope.colorScheme(colorScheme: ColorScheme) {
    property("color-scheme", colorScheme)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/color
// Named CSSColor to avoid ambiguity with org.jetbrains.compose.web.css.Color
class CSSColor private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keywords
        val CurrentColor get() = CSSColor("currentColor")

        // Global values
        val Inherit get() = CSSColor("inherit")
        val Initial get() = CSSColor("initial")
        val Revert get() = CSSColor("revert")
        val Unset get() = CSSColor("unset")
    }
}

fun StyleScope.color(color: CSSColor) {
    color(color.toString())
}

fun StyleScope.color(value: String) {
    property("color", value)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/hue-interpolation-method
class HueInterpolationMethod private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = "$value hue"

    companion object {
        val Shorter get() = HueInterpolationMethod("shorter")
        val Longer get() = HueInterpolationMethod("longer")
        val Increasing get() = HueInterpolationMethod("increasing")
        val Decreasing get() = HueInterpolationMethod("decreasing")
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/color-interpolation-method
sealed class ColorInterpolationMethod private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class RectangularColorSpace(space: String) : ColorInterpolationMethod("in $space")

    class PolarColorSpace internal constructor(
        private val space: String,
        hue: HueInterpolationMethod? = null,
    ) : ColorInterpolationMethod("in $space${if (hue != null) " $hue" else ""}") {
        internal fun withHue(hue: HueInterpolationMethod?): ColorInterpolationMethod = PolarColorSpace(space, hue)

        // These are provided as a convenience over the `of()` builder
        // Instead of `ColorInterpolationMethod.of(ColorInterpolationMethod.Hsl, HueInterpolationMethod.Shorter)`,
        // this allows writing `ColorInterpolationMethod.Hsl.ShorterHue`
        val ShorterHue get() = withHue(HueInterpolationMethod.Shorter)
        val LongerHue get() = withHue(HueInterpolationMethod.Longer)
        val IncreasingHue get() = withHue(HueInterpolationMethod.Increasing)
        val DecreasingHue get() = withHue(HueInterpolationMethod.Decreasing)
    }

    companion object {
        val Srgb: ColorInterpolationMethod get() = RectangularColorSpace("srgb")
        val SrgbLinear: ColorInterpolationMethod get() = RectangularColorSpace("srgb-linear")
        val DisplayP3: ColorInterpolationMethod get() = RectangularColorSpace("display-p3")
        val A98Rgb: ColorInterpolationMethod get() = RectangularColorSpace("a98-rgb")
        val ProphotoRgb: ColorInterpolationMethod get() = RectangularColorSpace("prophoto-rgb")
        val Rec2020: ColorInterpolationMethod get() = RectangularColorSpace("rec2020")
        val Lab: ColorInterpolationMethod get() = RectangularColorSpace("lab")
        val Oklab: ColorInterpolationMethod get() = RectangularColorSpace("oklab")
        val Xyz: ColorInterpolationMethod get() = RectangularColorSpace("xyz")
        val XyzD50: ColorInterpolationMethod get() = RectangularColorSpace("xyz-d50")
        val XyzD65: ColorInterpolationMethod get() = RectangularColorSpace("xyz-d65")

        val Hsl get() = PolarColorSpace("hsl")
        val Hwb get() = PolarColorSpace("hwb")
        val Lch get() = PolarColorSpace("lch")
        val Oklch get() = PolarColorSpace("oklch")

        fun of(space: PolarColorSpace, hueInterpolationMethod: HueInterpolationMethod? = null) =
            space.withHue(hueInterpolationMethod)
    }
}
