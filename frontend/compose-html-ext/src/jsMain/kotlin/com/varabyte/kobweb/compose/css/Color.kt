// Sealed class private constructors are useful, actually!
@file:Suppress("RedundantVisibilityModifier")

package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/accent-color
sealed interface AccentColor : StylePropertyValue {
    companion object : CssGlobalValues<AccentColor> {
        // Keyword values
        val Auto get() = "auto".unsafeCast<AccentColor>()

        // <color> values
        fun of(color: CSSColorValue) = color.toString().unsafeCast<AccentColor>()
    }
}

fun StyleScope.accentColor(accentColor: AccentColor) {
    property("accent-color", accentColor)
}

@Deprecated("Use accentColor(AccentColor.of(color)) instead.", ReplaceWith("accentColor(AccentColor.of(color))"))
fun StyleScope.accentColor(color: CSSColorValue) {
    accentColor(AccentColor.of(color))
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/color-scheme
sealed interface ColorScheme : StylePropertyValue {
    companion object : CssGlobalValues<ColorScheme> {
        // Keyword values
        val Normal get() = "normal".unsafeCast<ColorScheme>()
        val Light get() = "light".unsafeCast<ColorScheme>()
        val Dark get() = "dark".unsafeCast<ColorScheme>()
        val LightDark get() = "light dark".unsafeCast<ColorScheme>()
        val DarkLight get() = "dark light".unsafeCast<ColorScheme>()
        val OnlyLight get() = "only light".unsafeCast<ColorScheme>()
        val OnlyDark get() = "only dark".unsafeCast<ColorScheme>()
    }
}

fun StyleScope.colorScheme(colorScheme: ColorScheme) {
    property("color-scheme", colorScheme)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/color
// Named CSSColor to avoid ambiguity with org.jetbrains.compose.web.css.Color
sealed interface CSSColor : StylePropertyValue {
    companion object : CssGlobalValues<CSSColor> {
        // Keywords
        val CurrentColor get() = "currentColor".unsafeCast<CSSColor>()
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
