package com.varabyte.kobweb.compose.ui.graphics

import com.varabyte.kobweb.compose.css.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.Color as JbColor
import kotlin.math.abs
import kotlin.math.roundToInt

private fun Float.toColorInt() = (this.coerceIn(0f, 1f) * 255.0f).toInt()
private fun Int.toColorFloat() = this.and(0xFF) / 255.0f

/**
 * A base class for colors which provides additional functionality on top of the color class included in Compose HTML.
 */
sealed interface Color : CSSColorValue {
    fun inverted(): Color

    /**
     * Darken this color by some target percent value.
     *
     * @param byPercent A value between 0 (no change) and 1 (will result in black). Otherwise, darken the current
     *   color rgb values by some percent amount (meaning the final result depends upon the initial values).
     */
    fun darkened(byPercent: Float = DEFAULT_SHIFTING_PERCENT): Color

    fun toRgb(): Rgb
    fun toHsl(): Hsl

    interface Rgb : Color {
        val value: Int
        val red: Int
        val green: Int
        val blue: Int
        val alpha: Int

        val redf: Float
        val greenf: Float
        val bluef: Float
        val alphaf: Float

        fun copy(red: Int = this.red, green: Int = this.green, blue: Int = this.blue, alpha: Int = this.alpha): Rgb
        fun copyf(red: Float = redf, green: Float = this.greenf, blue: Float = this.bluef, alpha: Float = this.alphaf): Rgb
    }

    /**
     * @property value A hex value representing this color as AARRGGBB, e.g. 0xFFFF0000 is red and 0xFF0000FF is blue.
     */
    class RawRgb internal constructor(override val value: Int) : Rgb {
        override val red: Int get() = value.shr(16).and(0xFF)
        override val green: Int get() = value.shr(8).and(0xFF)
        override val blue: Int get() = value.shr(0).and(0xFF)
        override val alpha: Int get() = value.shr(24).and(0xFF)

        override val redf: Float get() = red.toColorFloat()
        override val greenf: Float get() = green.toColorFloat()
        override val bluef: Float get() = blue.toColorFloat()
        override val alphaf: Float get() = alpha.toColorFloat()

        override fun inverted(): Color = rgba(255 - red, 255 - green, 255 - blue, alpha)
        override fun darkened(byPercent: Float): Color {
            require(byPercent in (0f..1f)) { "Invalid color shifting percent. Expected between 0 and 1, got $byPercent" }
            if (byPercent == 0f) return this

            val darkeningMultiplier = 1.0f - byPercent // e.g. reduce by 20% means take 80% of the current value
            return rgba(redf * darkeningMultiplier, greenf * darkeningMultiplier, bluef * darkeningMultiplier, alphaf)
        }

        override fun copy(red: Int, green: Int, blue: Int, alpha: Int) =
            rgba(red, green, blue, alpha)

        override fun copyf(red: Float, green: Float, blue: Float, alpha: Float) =
            rgba(red, green, blue, alpha)

        override fun toRgb(): Rgb {
            return this
        }

        override fun toHsl(): Hsl {
            // https://en.wikipedia.org/wiki/HSL_and_HSV#Color_conversion_formulae
            val chromaMax = maxOf(redf, greenf, bluef)
            val chromaMin = minOf(redf, greenf, bluef)
            val chromaDelta = chromaMax - chromaMin

            val lightness = (chromaMin + chromaMax) / 2f
            val saturation = chromaDelta / (1f - abs(2f * lightness - 1f))
            val hue = if (chromaDelta == 0f) {
                0f
            } else {
                60f * when {
                    chromaMax == redf -> ((greenf - bluef) / chromaDelta) % 6
                    chromaMax == greenf -> ((bluef - redf) / chromaDelta) + 2f
                    chromaMax == bluef -> ((redf - greenf) / chromaDelta) + 4f
                    else -> error("Unexpected chromaMax value $chromaMax")
                }
            }

            return hsla(hue, saturation, lightness, alphaf)
        }

        override fun toString(): String {
            return if (alpha == 0xFF) "rgb($red, $green, $blue)" else "rgba($red, $green, $blue, $alphaf)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Rgb && red == other.red && green == other.green && blue == other.blue && alpha == other.alpha
        }

        override fun hashCode(): Int {
            var result = red.hashCode()
            result = 31 * result + green.hashCode()
            result = 31 * result + blue.hashCode()
            result = 31 * result + alpha.hashCode()
            return result
        }
    }

    class NamedRgb private constructor(val name: String, rgb: RawRgb) : Rgb by rgb {
        internal constructor(name: String, value: Int) : this(name, RawRgb(value))

        override fun toString() = name
        override fun equals(other: Any?) = (this === other)
        override fun hashCode() = name.hashCode()
    }

    /**
     * A representation for a color specified via hue, saturation, and lightness values.
     *
     * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/color_value/hsl
     *
     * @property hue An angle (0-360) representing the color based on its location in a color wheel.
     * @property saturation A percentage value (0-1) representing how grey the color is.
     * @property lightness A percentage value (0-1) representing how bright the color is.
     * @property alpha A percentage value (0-1) representing how transparent the color is.
     */
    class Hsl internal constructor(val hue: Float, val saturation: Float, val lightness: Float, val alpha: Float) :
        Color {
        override fun inverted() = toRgb().inverted()
        override fun darkened(byPercent: Float) = toRgb().darkened(byPercent)

        fun copy(
            hue: Float = this.hue,
            saturation: Float = this.saturation,
            lightness: Float = this.lightness,
            alpha: Float = this.alpha
        ) =
            hsla(hue, saturation, lightness, alpha)

        override fun toRgb(): Rgb {
            // https://en.wikipedia.org/wiki/HSL_and_HSV#Color_conversion_formulae
            val chroma = (1 - abs(2 * lightness - 1)) * saturation
            val intermediateValue = chroma * (1 - abs(((hue / 60) % 2) - 1))
            val hueSection = (hue.toInt() % 360) / 60
            val r: Float
            val g: Float
            val b: Float
            when (hueSection) {
                0 -> {
                    r = chroma
                    g = intermediateValue
                    b = 0f
                }

                1 -> {
                    r = intermediateValue
                    g = chroma
                    b = 0f
                }

                2 -> {
                    r = 0f
                    g = chroma
                    b = intermediateValue
                }

                3 -> {
                    r = 0f
                    g = intermediateValue
                    b = chroma
                }

                4 -> {
                    r = intermediateValue
                    g = 0f
                    b = chroma
                }

                else -> {
                    check(hueSection == 5)
                    r = chroma
                    g = 0f
                    b = intermediateValue
                }
            }
            val lightnessAdjustment = lightness - chroma / 2

            return rgba(r + lightnessAdjustment, g + lightnessAdjustment, b + lightnessAdjustment, alpha)
        }

        override fun toHsl(): Hsl {
            return this
        }

        override fun toString(): String {
            // Make sure println doesn't show more than a single decimal point
            val hueRounded = (hue * 10).roundToInt() / 10f
            val saturationPercent = (saturation * 1000).roundToInt() / 10f
            val lightnessPercent = (lightness * 1000).roundToInt() / 10f
            return if (alpha == 1.0f)
                "hsl($hueRounded, $saturationPercent%, $lightnessPercent%)"
            else
                "hsla($hueRounded, $saturationPercent%, $lightnessPercent%, $alpha)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Hsl && hue == other.hue && saturation == other.saturation && lightness == other.lightness && alpha == other.alpha
        }

        override fun hashCode(): Int {
            var result = hue.hashCode()
            result = 31 * result + saturation.hashCode()
            result = 31 * result + lightness.hashCode()
            result = 31 * result + alpha.hashCode()
            return result
        }
    }

    // NOTE: argb versions provided for convenience, as Android devs are used to that order
    companion object {
        const val DEFAULT_SHIFTING_PERCENT = 0.3f

        private fun Long.toInRangeInt(): Int {
            // We accept Long for colors because Kotlin treats 0xFF______ values as a Long, even though that can
            // still fit into 4 bytes and be represented by an Int. Therefore, let's accept Longs but fail at runtime if
            // the user passes in something with a higher bit set.
            check(0xFFFFFFFF.inv().and(this) == 0L) {
                "Got an invalid hex color (0x${
                    this.toString(16).uppercase()
                }) value larger than 0xFFFFFFFF"
            }
            return this.toInt()
        }

        fun rgb(value: Int): Rgb = RawRgb(0xFF.shl(24).or(value))
        fun argb(value: Int): Rgb = RawRgb(value)
        fun argb(value: Long) = argb(value.toInRangeInt())

        fun rgba(value: Int) = run {
            // Convert RRGGBBAA to AARRGGBB
            val alpha = value.and(0xFF).shl(24)
            val rgb = value.shr(8)
            argb(alpha.or(rgb))
        }
        fun rgba(value: Long) = rgba(value.toInRangeInt())

        fun rgb(r: Int, g: Int, b: Int) = rgba(r, g, b, 0xFF)
        fun rgba(r: Int, g: Int, b: Int, a: Int) = RawRgb(
            r.and(0xFF).shl(16)
                .or(g.and(0xFF).shl(8))
                .or(b.and(0xFF).shl(0))
                .or(a.and(0xFF).shl(24))
        )

        fun argb(a: Int, r: Int, g: Int, b: Int) = rgba(r, g, b, a)

        fun rgb(r: Float, g: Float, b: Float) = rgb(r.toColorInt(), g.toColorInt(), b.toColorInt())
        fun rgba(r: Float, g: Float, b: Float, a: Float) =
            rgba(r.toColorInt(), g.toColorInt(), b.toColorInt(), a.toColorInt())

        fun argb(a: Float, r: Float, g: Float, b: Float) = rgba(r, g, b, a)

        fun rgba(r: Int, g: Int, b: Int, a: Float) = rgba(r, g, b, a.toColorInt())
        fun argb(a: Float, r: Int, g: Int, b: Int) = rgba(r, g, b, a)

        fun hsl(h: Int, s: Float, l: Float) = hsl(h.toFloat(), s, l)
        fun hsla(h: Int, s: Float, l: Float, a: Float) = hsla(h.toFloat(), s, l, a)
        fun hsl(h: Float, s: Float, l: Float) = Hsl(h, s, l, 1f)
        fun hsla(h: Float, s: Float, l: Float, a: Float) = Hsl(h, s, l, a)

        // Provide convenience methods for CSS value parameter types

        fun rgb(r: CSSPercentageValue, g: CSSPercentageValue, b: CSSPercentageValue) = rgba(r, g, b, 1f)
        fun rgba(r: CSSPercentageValue, g: CSSPercentageValue, b: CSSPercentageValue, a: CSSPercentageValue) =
            rgba(r.value / 100f, g.value / 100f, b.value / 100f, a.value / 100f)

        fun rgba(r: CSSPercentageValue, g: CSSPercentageValue, b: CSSPercentageValue, a: Float) =
            rgba(r.value / 100f, g.value / 100f, b.value / 100f, a)

        fun argb(a: CSSPercentageValue, r: CSSPercentageValue, g: CSSPercentageValue, b: CSSPercentageValue) =
            argb(a.value / 100f, r.value / 100f, g.value / 100f, b.value / 100f)

        fun argb(a: Float, r: CSSPercentageValue, g: CSSPercentageValue, b: CSSPercentageValue) =
            argb(a, r.value / 100f, g.value / 100f, b.value / 100f)

        fun hsl(h: CSSAngleValue, s: CSSPercentageValue, l: CSSPercentageValue) = hsla(h, s, l, 1f)
        fun hsla(h: CSSAngleValue, s: CSSPercentageValue, l: CSSPercentageValue, alpha: Float) =
            hsla(h.toDegrees(), s.value / 100f, l.value / 100f, alpha)

        fun hsla(h: CSSAngleValue, s: CSSPercentageValue, l: CSSPercentageValue, alpha: CSSPercentageValue) =
            hsla(h, s, l, alpha.value / 100f)
    }
}

/**
 * Lighten this color by some target percent value.
 *
 * @param byPercent A value between 0 (no change) and 1 (will result in white). Otherwise, lighten the current
 *   color rgb values by some percent amount (so the final result depends upon the initial values).
 */
fun Color.lightened(byPercent: Float = Color.DEFAULT_SHIFTING_PERCENT) = inverted().darkened(byPercent).inverted()

/**
 * Calculate a color's luminance, which is a calculation for how bright it is perceived to be by the human eye.
 */
val Color.luminance: Float
    // See also: https://www.w3.org/TR/AERT/#color-contrast
    get() = this.toRgb()
        .let { rgb -> (rgb.redf * 0.299f) + (rgb.greenf * 0.587f) + (rgb.bluef * 0.114f) }

/**
 * Check if a color is perceived bright or not.
 *
 * This can be useful if deciding to overlay dark or light text on top of it, for example.
 */
val Color.isBright: Boolean get() = this.luminance > 0.5f

object Colors {
    private fun NamedRgb(jbColor: CSSColorValue, value: Int) = Color.NamedRgb(jbColor.toString(), value)

    val Transparent get() = NamedRgb(JbColor.transparent, Color.rgba(0, 0, 0, 0).value)

    // From https://www.w3schools.com/colors/colors_names.asp
    val AliceBlue: Color.Rgb get() = NamedRgb(JbColor.aliceblue, Color.rgb(0xF0, 0xF8, 0xFF).value)
    val AntiqueWhite: Color.Rgb get() = NamedRgb(JbColor.antiquewhite, Color.rgb(0xFA, 0xEB, 0xD7).value)
    val Aqua: Color.Rgb get() = Color.NamedRgb("aqua", Color.rgb(0x00, 0xFF, 0xFF).value)
    val Aquamarine: Color.Rgb get() = NamedRgb(JbColor.aquamarine, Color.rgb(0x7F, 0xFF, 0xD4).value)
    val Azure: Color.Rgb get() = NamedRgb(JbColor.azure, Color.rgb(0xF0, 0xFF, 0xFF).value)
    val Beige: Color.Rgb get() = NamedRgb(JbColor.beige, Color.rgb(0xF5, 0xF5, 0xDC).value)
    val Bisque: Color.Rgb get() = NamedRgb(JbColor.bisque, Color.rgb(0xFF, 0xE4, 0xC4).value)
    val Black: Color.Rgb get() = NamedRgb(JbColor.black, Color.rgb(0x00, 0x00, 0x00).value)
    val BlanchedAlmond: Color.Rgb get() = NamedRgb(JbColor.blanchedalmond, Color.rgb(0xFF, 0xEB, 0xCD).value)
    val Blue: Color.Rgb get() = NamedRgb(JbColor.blue, Color.rgb(0x00, 0x00, 0xFF).value)
    val BlueViolet: Color.Rgb get() = NamedRgb(JbColor.blueviolet, Color.rgb(0x8A, 0x2B, 0xE2).value)
    val Brown: Color.Rgb get() = NamedRgb(JbColor.brown, Color.rgb(0xA5, 0x2A, 0x2A).value)
    val BurlyWood: Color.Rgb get() = NamedRgb(JbColor.burlywood, Color.rgb(0xDE, 0xB8, 0x87).value)
    val CadetBlue: Color.Rgb get() = NamedRgb(JbColor.cadetblue, Color.rgb(0x5F, 0x9E, 0xA0).value)
    val Chartreuse: Color.Rgb get() = NamedRgb(JbColor.chartreuse, Color.rgb(0x7F, 0xFF, 0x00).value)
    val Chocolate: Color.Rgb get() = NamedRgb(JbColor.chocolate, Color.rgb(0xD2, 0x69, 0x1E).value)
    val Coral: Color.Rgb get() = Color.NamedRgb("coral", Color.rgb(0xFF, 0x7F, 0x50).value)
    val CornflowerBlue: Color.Rgb get() = NamedRgb(JbColor.cornflowerblue, Color.rgb(0x64, 0x95, 0xED).value)
    val Cornsilk: Color.Rgb get() = NamedRgb(JbColor.cornsilk, Color.rgb(0xFF, 0xF8, 0xDC).value)
    val Crimson: Color.Rgb get() = NamedRgb(JbColor.crimson, Color.rgb(0xDC, 0x14, 0x3C).value)
    val Cyan: Color.Rgb get() = NamedRgb(JbColor.cyan, Color.rgb(0x00, 0xFF, 0xFF).value)
    val DarkBlue: Color.Rgb get() = NamedRgb(JbColor.darkblue, Color.rgb(0x00, 0x00, 0x8B).value)
    val DarkCyan: Color.Rgb get() = NamedRgb(JbColor.darkcyan, Color.rgb(0x00, 0x8B, 0x8B).value)
    val DarkGoldenRod: Color.Rgb get() = NamedRgb(JbColor.darkgoldenrod, Color.rgb(0xB8, 0x86, 0x0B).value)
    val DarkGray: Color.Rgb get() = NamedRgb(JbColor.darkgray, Color.rgb(0xA9, 0xA9, 0xA9).value)
    val DarkGrey: Color.Rgb get() = Color.NamedRgb("darkgrey", DarkGray.value)
    val DarkGreen: Color.Rgb get() = NamedRgb(JbColor.darkgreen, Color.rgb(0x00, 0x64, 0x00).value)
    val DarkKhaki: Color.Rgb get() = NamedRgb(JbColor.darkkhaki, Color.rgb(0xBD, 0xB7, 0x6B).value)
    val DarkMagenta: Color.Rgb get() = NamedRgb(JbColor.darkmagenta, Color.rgb(0x8B, 0x00, 0x8B).value)
    val DarkOliveGreen: Color.Rgb get() = NamedRgb(JbColor.darkolivegreen, Color.rgb(0x55, 0x6B, 0x2F).value)
    val DarkOrange: Color.Rgb get() = NamedRgb(JbColor.darkorange, Color.rgb(0xFF, 0x8C, 0x00).value)
    val DarkOrchid: Color.Rgb get() = NamedRgb(JbColor.darkorchid, Color.rgb(0x99, 0x32, 0xCC).value)
    val DarkRed: Color.Rgb get() = NamedRgb(JbColor.darkred, Color.rgb(0x8B, 0x00, 0x00).value)
    val DarkSalmon: Color.Rgb get() = NamedRgb(JbColor.darksalmon, Color.rgb(0xE9, 0x96, 0x7A).value)
    val DarkSeaGreen: Color.Rgb get() = Color.NamedRgb("darkseagreen", Color.rgb(0x8F, 0xBC, 0x8F).value)
    val DarkSlateBlue: Color.Rgb get() = NamedRgb(JbColor.darkslateblue, Color.rgb(0x48, 0x3D, 0x8B).value)
    val DarkSlateGray: Color.Rgb get() = NamedRgb(JbColor.darkslategray, Color.rgb(0x2F, 0x4F, 0x4F).value)
    val DarkSlateGrey: Color.Rgb get() = Color.NamedRgb("darkslategrey", DarkSlateGray.value)
    val DarkTurquoise: Color.Rgb get() = NamedRgb(JbColor.darkturquoise, Color.rgb(0x00, 0xCE, 0xD1).value)
    val DarkViolet: Color.Rgb get() = NamedRgb(JbColor.darkviolet, Color.rgb(0x94, 0x00, 0xD3).value)
    val DeepPink: Color.Rgb get() = NamedRgb(JbColor.deeppink, Color.rgb(0xFF, 0x14, 0x93).value)
    val DeepSkyBlue: Color.Rgb get() = NamedRgb(JbColor.deepskyblue, Color.rgb(0x00, 0xBF, 0xFF).value)
    val DimGray: Color.Rgb get() = NamedRgb(JbColor.dimgray, Color.rgb(0x69, 0x69, 0x69).value)
    val DimGrey: Color.Rgb get() = Color.NamedRgb("dimgrey", DimGray.value)
    val DodgerBlue: Color.Rgb get() = NamedRgb(JbColor.dodgerblue, Color.rgb(0x1E, 0x90, 0xFF).value)
    val FireBrick: Color.Rgb get() = NamedRgb(JbColor.firebrick, Color.rgb(0xB2, 0x22, 0x22).value)
    val FloralWhite: Color.Rgb get() = NamedRgb(JbColor.floralwhite, Color.rgb(0xFF, 0xFA, 0xF0).value)
    val ForestGreen: Color.Rgb get() = NamedRgb(JbColor.forestgreen, Color.rgb(0x22, 0x8B, 0x22).value)
    val Fuchsia: Color.Rgb get() = NamedRgb(JbColor.fuchsia, Color.rgb(0xFF, 0x00, 0xFF).value)
    val Gainsboro: Color.Rgb get() = NamedRgb(JbColor.gainsboro, Color.rgb(0xDC, 0xDC, 0xDC).value)
    val GhostWhite: Color.Rgb get() = NamedRgb(JbColor.ghostwhite, Color.rgb(0xF8, 0xF8, 0xFF).value)
    val Gold: Color.Rgb get() = NamedRgb(JbColor.gold, Color.rgb(0xFF, 0xD7, 0x00).value)
    val GoldenRod: Color.Rgb get() = NamedRgb(JbColor.goldenrod, Color.rgb(0xDA, 0xA5, 0x20).value)
    val Gray: Color.Rgb get() = NamedRgb(JbColor.gray, Color.rgb(0x80, 0x80, 0x80).value)
    val Grey: Color.Rgb get() = Color.NamedRgb("grey", Gray.value)
    val Green: Color.Rgb get() = NamedRgb(JbColor.green, Color.rgb(0x00, 0x80, 0x00).value)
    val GreenYellow: Color.Rgb get() = NamedRgb(JbColor.greenyellow, Color.rgb(0xAD, 0xFF, 0x2F).value)
    val HoneyDew: Color.Rgb get() = NamedRgb(JbColor.honeydew, Color.rgb(0xF0, 0xFF, 0xF0).value)
    val HotPink: Color.Rgb get() = NamedRgb(JbColor.hotpink, Color.rgb(0xFF, 0x69, 0xB4).value)
    val IndianRed: Color.Rgb get() = NamedRgb(JbColor.indianred, Color.rgb(0xCD, 0x5C, 0x5C).value)
    val Indigo: Color.Rgb get() = NamedRgb(JbColor.indigo, Color.rgb(0x4B, 0x00, 0x82).value)
    val Ivory: Color.Rgb get() = NamedRgb(JbColor.ivory, Color.rgb(0xFF, 0xFF, 0xF0).value)
    val Khaki: Color.Rgb get() = NamedRgb(JbColor.khaki, Color.rgb(0xF0, 0xE6, 0x8C).value)
    val Lavender: Color.Rgb get() = NamedRgb(JbColor.lavender, Color.rgb(0xE6, 0xE6, 0xFA).value)
    val LavenderBlush: Color.Rgb get() = NamedRgb(JbColor.lavenderblush, Color.rgb(0xFF, 0xF0, 0xF5).value)
    val LawnGreen: Color.Rgb get() = NamedRgb(JbColor.lawngreen, Color.rgb(0x7C, 0xFC, 0x00).value)
    val LemonChiffon: Color.Rgb get() = NamedRgb(JbColor.lemonchiffon, Color.rgb(0xFF, 0xFA, 0xCD).value)
    val LightBlue: Color.Rgb get() = NamedRgb(JbColor.lightblue, Color.rgb(0xAD, 0xD8, 0xE6).value)
    val LightCoral: Color.Rgb get() = NamedRgb(JbColor.lightcoral, Color.rgb(0xF0, 0x80, 0x80).value)
    val LightCyan: Color.Rgb get() = NamedRgb(JbColor.lightcyan, Color.rgb(0xE0, 0xFF, 0xFF).value)
    val LightGoldenRodYellow: Color.Rgb get() = NamedRgb(JbColor.lightgoldenrodyellow, Color.rgb(0xFA, 0xFA, 0xD2).value)
    val LightGray: Color.Rgb get() = NamedRgb(JbColor.lightgray, Color.rgb(0xD3, 0xD3, 0xD3).value)
    val LightGrey: Color.Rgb get() = Color.NamedRgb("lightgrey", LightGray.value)
    val LightGreen: Color.Rgb get() = NamedRgb(JbColor.lightgreen, Color.rgb(0x90, 0xEE, 0x90).value)
    val LightPink: Color.Rgb get() = NamedRgb(JbColor.lightpink, Color.rgb(0xFF, 0xB6, 0xC1).value)
    val LightSalmon: Color.Rgb get() = NamedRgb(JbColor.lightsalmon, Color.rgb(0xFF, 0xA0, 0x7A).value)
    val LightSeaGreen: Color.Rgb get() = NamedRgb(JbColor.lightseagreen, Color.rgb(0x20, 0xB2, 0xAA).value)
    val LightSkyBlue: Color.Rgb get() = NamedRgb(JbColor.lightskyblue, Color.rgb(0x87, 0xCE, 0xFA).value)
    val LightSlateGray: Color.Rgb get() = NamedRgb(JbColor.lightslategray, Color.rgb(0x77, 0x88, 0x99).value)
    val LightSlateGrey: Color.Rgb get() = Color.NamedRgb("lightslategrey", LightSlateGray.value)
    val LightSteelBlue: Color.Rgb get() = NamedRgb(JbColor.lightsteelblue, Color.rgb(0xB0, 0xC4, 0xDE).value)
    val LightYellow: Color.Rgb get() = NamedRgb(JbColor.lightyellow, Color.rgb(0xFF, 0xFF, 0xE0).value)
    val Lime: Color.Rgb get() = NamedRgb(JbColor.lime, Color.rgb(0x00, 0xFF, 0x00).value)
    val LimeGreen: Color.Rgb get() = NamedRgb(JbColor.limegreen, Color.rgb(0x32, 0xCD, 0x32).value)
    val Linen: Color.Rgb get() = NamedRgb(JbColor.linen, Color.rgb(0xFA, 0xF0, 0xE6).value)
    val Magenta: Color.Rgb get() = NamedRgb(JbColor.magenta, Color.rgb(0xFF, 0x00, 0xFF).value)
    val Maroon: Color.Rgb get() = NamedRgb(JbColor.maroon, Color.rgb(0x80, 0x00, 0x00).value)
    val MediumAquaMarine: Color.Rgb get() = NamedRgb(JbColor.mediumaquamarine, Color.rgb(0x66, 0xCD, 0xAA).value)
    val MediumBlue: Color.Rgb get() = NamedRgb(JbColor.mediumblue, Color.rgb(0x00, 0x00, 0xCD).value)
    val MediumOrchid: Color.Rgb get() = NamedRgb(JbColor.mediumorchid, Color.rgb(0xBA, 0x55, 0xD3).value)
    val MediumPurple: Color.Rgb get() = NamedRgb(JbColor.mediumpurple, Color.rgb(0x93, 0x70, 0xDB).value)
    val MediumSeaGreen: Color.Rgb get() = NamedRgb(JbColor.mediumseagreen, Color.rgb(0x3C, 0xB3, 0x71).value)
    val MediumSlateBlue: Color.Rgb get() = NamedRgb(JbColor.mediumslateblue, Color.rgb(0x7B, 0x68, 0xEE).value)
    val MediumSpringGreen: Color.Rgb get() = NamedRgb(JbColor.mediumspringgreen, Color.rgb(0x00, 0xFA, 0x9A).value)
    val MediumTurquoise: Color.Rgb get() = NamedRgb(JbColor.mediumturquoise, Color.rgb(0x48, 0xD1, 0xCC).value)
    val MediumVioletRed: Color.Rgb get() = NamedRgb(JbColor.mediumvioletred, Color.rgb(0xC7, 0x15, 0x85).value)
    val MidnightBlue: Color.Rgb get() = NamedRgb(JbColor.midnightblue, Color.rgb(0x19, 0x19, 0x70).value)
    val MintCream: Color.Rgb get() = NamedRgb(JbColor.mintcream, Color.rgb(0xF5, 0xFF, 0xFA).value)
    val MistyRose: Color.Rgb get() = NamedRgb(JbColor.mistyrose, Color.rgb(0xFF, 0xE4, 0xE1).value)
    val Moccasin: Color.Rgb get() = NamedRgb(JbColor.moccasin, Color.rgb(0xFF, 0xE4, 0xB5).value)
    val NavajoWhite: Color.Rgb get() = NamedRgb(JbColor.navajowhite, Color.rgb(0xFF, 0xDE, 0xAD).value)
    val Navy: Color.Rgb get() = Color.NamedRgb("navy", Color.rgb(0x00, 0x00, 0x80).value)
    val OldLace: Color.Rgb get() = NamedRgb(JbColor.oldlace, Color.rgb(0xFD, 0xF5, 0xE6).value)
    val Olive: Color.Rgb get() = NamedRgb(JbColor.olive, Color.rgb(0x80, 0x80, 0x00).value)
    val OliveDrab: Color.Rgb get() = NamedRgb(JbColor.olivedrab, Color.rgb(0x6B, 0x8E, 0x23).value)
    val Orange: Color.Rgb get() = NamedRgb(JbColor.orange, Color.rgb(0xFF, 0xA5, 0x00).value)
    val OrangeRed: Color.Rgb get() = NamedRgb(JbColor.orangered, Color.rgb(0xFF, 0x45, 0x00).value)
    val Orchid: Color.Rgb get() = NamedRgb(JbColor.orchid, Color.rgb(0xDA, 0x70, 0xD6).value)
    val PaleGoldenRod: Color.Rgb get() = NamedRgb(JbColor.palegoldenrod, Color.rgb(0xEE, 0xE8, 0xAA).value)
    val PaleGreen: Color.Rgb get() = NamedRgb(JbColor.palegreen, Color.rgb(0x98, 0xFB, 0x98).value)
    val PaleTurquoise: Color.Rgb get() = NamedRgb(JbColor.paleturquoise, Color.rgb(0xAF, 0xEE, 0xEE).value)
    val PaleVioletRed: Color.Rgb get() = NamedRgb(JbColor.palevioletred, Color.rgb(0xDB, 0x70, 0x93).value)
    val PapayaWhip: Color.Rgb get() = NamedRgb(JbColor.papayawhip, Color.rgb(0xFF, 0xEF, 0xD5).value)
    val PeachPuff: Color.Rgb get() = NamedRgb(JbColor.peachpuff, Color.rgb(0xFF, 0xDA, 0xB9).value)
    val Peru: Color.Rgb get() = NamedRgb(JbColor.peru, Color.rgb(0xCD, 0x85, 0x3F).value)
    val Pink: Color.Rgb get() = NamedRgb(JbColor.pink, Color.rgb(0xFF, 0xC0, 0xCB).value)
    val Plum: Color.Rgb get() = NamedRgb(JbColor.plum, Color.rgb(0xDD, 0xA0, 0xDD).value)
    val PowderBlue: Color.Rgb get() = NamedRgb(JbColor.powderblue, Color.rgb(0xB0, 0xE0, 0xE6).value)
    val Purple: Color.Rgb get() = NamedRgb(JbColor.purple, Color.rgb(0x80, 0x00, 0x80).value)
    val RebeccaPurple: Color.Rgb get() = NamedRgb(JbColor.rebeccapurple, Color.rgb(0x66, 0x33, 0x99).value)
    val Red: Color.Rgb get() = NamedRgb(JbColor.red, Color.rgb(0xFF, 0x00, 0x00).value)
    val RosyBrown: Color.Rgb get() = NamedRgb(JbColor.rosybrown, Color.rgb(0xBC, 0x8F, 0x8F).value)
    val RoyalBlue: Color.Rgb get() = NamedRgb(JbColor.royalblue, Color.rgb(0x41, 0x69, 0xE1).value)
    val SaddleBrown: Color.Rgb get() = NamedRgb(JbColor.saddlebrown, Color.rgb(0x8B, 0x45, 0x13).value)
    val Salmon: Color.Rgb get() = NamedRgb(JbColor.salmon, Color.rgb(0xFA, 0x80, 0x72).value)
    val SandyBrown: Color.Rgb get() = NamedRgb(JbColor.sandybrown, Color.rgb(0xF4, 0xA4, 0x60).value)
    val SeaGreen: Color.Rgb get() = NamedRgb(JbColor.seagreen, Color.rgb(0x2E, 0x8B, 0x57).value)
    val SeaShell: Color.Rgb get() = NamedRgb(JbColor.seashell, Color.rgb(0xFF, 0xF5, 0xEE).value)
    val Sienna: Color.Rgb get() = NamedRgb(JbColor.sienna, Color.rgb(0xA0, 0x52, 0x2D).value)
    val Silver: Color.Rgb get() = NamedRgb(JbColor.silver, Color.rgb(0xC0, 0xC0, 0xC0).value)
    val SkyBlue: Color.Rgb get() = NamedRgb(JbColor.skyblue, Color.rgb(0x87, 0xCE, 0xEB).value)
    val SlateBlue: Color.Rgb get() = NamedRgb(JbColor.slateblue, Color.rgb(0x6A, 0x5A, 0xCD).value)
    val SlateGray: Color.Rgb get() = NamedRgb(JbColor.slategray, Color.rgb(0x70, 0x80, 0x90).value)
    val SlateGrey: Color.Rgb get() = Color.NamedRgb("slategrey", SlateGray.value)
    val Snow: Color.Rgb get() = NamedRgb(JbColor.snow, Color.rgb(0xFF, 0xFA, 0xFA).value)
    val SpringGreen: Color.Rgb get() = NamedRgb(JbColor.springgreen, Color.rgb(0x00, 0xFF, 0x7F).value)
    val SteelBlue: Color.Rgb get() = NamedRgb(JbColor.steelblue, Color.rgb(0x46, 0x82, 0xB4).value)
    val Tan: Color.Rgb get() = Color.NamedRgb("tan", Color.rgb(0xD2, 0xB4, 0x8C).value)
    val Teal: Color.Rgb get() = NamedRgb(JbColor.teal, Color.rgb(0x00, 0x80, 0x80).value)
    val Thistle: Color.Rgb get() = NamedRgb(JbColor.thistle, Color.rgb(0xD8, 0xBF, 0xD8).value)
    val Tomato: Color.Rgb get() = NamedRgb(JbColor.tomato, Color.rgb(0xFF, 0x63, 0x47).value)
    val Turquoise: Color.Rgb get() = NamedRgb(JbColor.turquoise, Color.rgb(0x40, 0xE0, 0xD0).value)
    val Violet: Color.Rgb get() = NamedRgb(JbColor.violet, Color.rgb(0xEE, 0x82, 0xEE).value)
    val Wheat: Color.Rgb get() = NamedRgb(JbColor.wheat, Color.rgb(0xF5, 0xDE, 0xB3).value)
    val White: Color.Rgb get() = NamedRgb(JbColor.white, Color.rgb(0xFF, 0xFF, 0xFF).value)
    val WhiteSmoke: Color.Rgb get() = NamedRgb(JbColor.whitesmoke, Color.rgb(0xF5, 0xF5, 0xF5).value)
    val Yellow: Color.Rgb get() = NamedRgb(JbColor.yellow, Color.rgb(0xFF, 0xFF, 0x00).value)
    val YellowGreen: Color.Rgb get() = NamedRgb(JbColor.yellowgreen, Color.rgb(0x9A, 0xCD, 0x32).value)
}
