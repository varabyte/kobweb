package com.varabyte.kobweb.compose.ui.graphics

import com.varabyte.kobweb.compose.css.*
import org.jetbrains.compose.web.css.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

private fun Float.toColorInt() = (this.coerceIn(0f, 1f) * 255.0f).toInt()
private fun Int.toColorFloat() = this.and(0xFF) / 255.0f

private fun Float.roundTo(decimalPlaces: Int): Float {
    val factor = 10f.pow(decimalPlaces)
    return ((this * factor).roundToInt()) / factor
}

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
            return if (alpha == 0xFF) "rgb($red, $green, $blue)" else "rgba($red, $green, $blue, ${alphaf.roundTo(decimalPlaces = 2)})"
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
            // One decimal point should be enough precision while avoiding messy hsl strings
            val hueRounded = hue.roundTo(decimalPlaces = 1)
            val saturationPercent = (saturation * 100).roundTo(decimalPlaces = 1)
            val lightnessPercent = (lightness * 100).roundTo(decimalPlaces = 1)
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
        fun argb(value: Long): Rgb = argb(value.toInRangeInt())

        fun rgba(value: Int): Rgb = run {
            // Convert RRGGBBAA to AARRGGBB
            val alpha = value.and(0xFF).shl(24)
            val rgb = value.shr(8)
            argb(alpha.or(rgb))
        }
        fun rgba(value: Long): Rgb = rgba(value.toInRangeInt())

        fun rgb(r: Int, g: Int, b: Int): Rgb = rgba(r, g, b, 0xFF)
        fun rgba(r: Int, g: Int, b: Int, a: Int): Rgb = RawRgb(
            r.and(0xFF).shl(16)
                .or(g.and(0xFF).shl(8))
                .or(b.and(0xFF).shl(0))
                .or(a.and(0xFF).shl(24))
        )

        fun argb(a: Int, r: Int, g: Int, b: Int): Rgb = rgba(r, g, b, a)

        fun rgb(r: Float, g: Float, b: Float): Rgb = rgb(r.toColorInt(), g.toColorInt(), b.toColorInt())
        fun rgba(r: Float, g: Float, b: Float, a: Float): Rgb =
            rgba(r.toColorInt(), g.toColorInt(), b.toColorInt(), a.toColorInt())

        fun argb(a: Float, r: Float, g: Float, b: Float): Rgb = rgba(r, g, b, a)

        fun rgba(r: Int, g: Int, b: Int, a: Float): Rgb = rgba(r, g, b, a.toColorInt())
        fun argb(a: Float, r: Int, g: Int, b: Int): Rgb = rgba(r, g, b, a)

        fun hsl(h: Int, s: Float, l: Float): Hsl = hsl(h.toFloat(), s, l)
        fun hsla(h: Int, s: Float, l: Float, a: Float): Hsl = hsla(h.toFloat(), s, l, a)
        fun hsl(h: Float, s: Float, l: Float): Hsl = Hsl(h, s, l, 1f)
        fun hsla(h: Float, s: Float, l: Float, a: Float): Hsl = Hsl(h, s, l, a)

        // Provide convenience methods for CSS value parameter types

        fun rgb(r: CSSPercentageValue, g: CSSPercentageValue, b: CSSPercentageValue): Rgb = rgba(r, g, b, 1f)
        fun rgba(r: CSSPercentageValue, g: CSSPercentageValue, b: CSSPercentageValue, a: CSSPercentageValue): Rgb =
            rgba(r.value / 100f, g.value / 100f, b.value / 100f, a.value / 100f)

        fun rgba(r: CSSPercentageValue, g: CSSPercentageValue, b: CSSPercentageValue, a: Float): Rgb =
            rgba(r.value / 100f, g.value / 100f, b.value / 100f, a)

        fun argb(a: CSSPercentageValue, r: CSSPercentageValue, g: CSSPercentageValue, b: CSSPercentageValue): Rgb =
            argb(a.value / 100f, r.value / 100f, g.value / 100f, b.value / 100f)

        fun argb(a: Float, r: CSSPercentageValue, g: CSSPercentageValue, b: CSSPercentageValue): Rgb =
            argb(a, r.value / 100f, g.value / 100f, b.value / 100f)

        fun hsl(h: CSSAngleValue, s: CSSPercentageValue, l: CSSPercentageValue): Hsl = hsla(h, s, l, 1f)
        fun hsla(h: CSSAngleValue, s: CSSPercentageValue, l: CSSPercentageValue, alpha: Float): Hsl =
            hsla(h.toDegrees(), s.value / 100f, l.value / 100f, alpha)

        fun hsla(h: CSSAngleValue, s: CSSPercentageValue, l: CSSPercentageValue, alpha: CSSPercentageValue): Hsl =
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
    private fun NamedRgb(name: String, rgb: Color.Rgb) = Color.NamedRgb(name, rgb.value)

    val Transparent: Color.Rgb get() = NamedRgb("transparent", Color.rgba(0, 0, 0, 0))

    // From https://www.w3schools.com/colors/colors_names.asp
    val AliceBlue: Color.Rgb get() = NamedRgb("aliceblue", Color.rgb(0xF0, 0xF8, 0xFF))
    val AntiqueWhite: Color.Rgb get() = NamedRgb("antiquewhite", Color.rgb(0xFA, 0xEB, 0xD7))
    val Aqua: Color.Rgb get() = NamedRgb("aqua", Color.rgb(0x00, 0xFF, 0xFF))
    val Aquamarine: Color.Rgb get() = NamedRgb("aquamarine", Color.rgb(0x7F, 0xFF, 0xD4))
    val Azure: Color.Rgb get() = NamedRgb("azure", Color.rgb(0xF0, 0xFF, 0xFF))
    val Beige: Color.Rgb get() = NamedRgb("beige", Color.rgb(0xF5, 0xF5, 0xDC))
    val Bisque: Color.Rgb get() = NamedRgb("bisque", Color.rgb(0xFF, 0xE4, 0xC4))
    val Black: Color.Rgb get() = NamedRgb("black", Color.rgb(0x00, 0x00, 0x00))
    val BlanchedAlmond: Color.Rgb get() = NamedRgb("blanchedalmond", Color.rgb(0xFF, 0xEB, 0xCD))
    val Blue: Color.Rgb get() = NamedRgb("blue", Color.rgb(0x00, 0x00, 0xFF))
    val BlueViolet: Color.Rgb get() = NamedRgb("blueviolet", Color.rgb(0x8A, 0x2B, 0xE2))
    val Brown: Color.Rgb get() = NamedRgb("brown", Color.rgb(0xA5, 0x2A, 0x2A))
    val BurlyWood: Color.Rgb get() = NamedRgb("burlywood", Color.rgb(0xDE, 0xB8, 0x87))
    val CadetBlue: Color.Rgb get() = NamedRgb("cadetblue", Color.rgb(0x5F, 0x9E, 0xA0))
    val Chartreuse: Color.Rgb get() = NamedRgb("chartreuse", Color.rgb(0x7F, 0xFF, 0x00))
    val Chocolate: Color.Rgb get() = NamedRgb("chocolate", Color.rgb(0xD2, 0x69, 0x1E))
    val Coral: Color.Rgb get() = NamedRgb("coral", Color.rgb(0xFF, 0x7F, 0x50))
    val CornflowerBlue: Color.Rgb get() = NamedRgb("cornflowerblue", Color.rgb(0x64, 0x95, 0xED))
    val Cornsilk: Color.Rgb get() = NamedRgb("cornsilk", Color.rgb(0xFF, 0xF8, 0xDC))
    val Crimson: Color.Rgb get() = NamedRgb("crimson", Color.rgb(0xDC, 0x14, 0x3C))
    val Cyan: Color.Rgb get() = NamedRgb("cyan", Color.rgb(0x00, 0xFF, 0xFF))
    val DarkBlue: Color.Rgb get() = NamedRgb("darkblue", Color.rgb(0x00, 0x00, 0x8B))
    val DarkCyan: Color.Rgb get() = NamedRgb("darkcyan", Color.rgb(0x00, 0x8B, 0x8B))
    val DarkGoldenRod: Color.Rgb get() = NamedRgb("darkgoldenrod", Color.rgb(0xB8, 0x86, 0x0B))
    val DarkGray: Color.Rgb get() = NamedRgb("darkgray", Color.rgb(0xA9, 0xA9, 0xA9))
    val DarkGrey: Color.Rgb get() = NamedRgb("darkgrey", Color.rgb(0xA9, 0xA9, 0xA9))
    val DarkGreen: Color.Rgb get() = NamedRgb("darkgreen", Color.rgb(0x00, 0x64, 0x00))
    val DarkKhaki: Color.Rgb get() = NamedRgb("darkkhaki", Color.rgb(0xBD, 0xB7, 0x6B))
    val DarkMagenta: Color.Rgb get() = NamedRgb("darkmagenta", Color.rgb(0x8B, 0x00, 0x8B))
    val DarkOliveGreen: Color.Rgb get() = NamedRgb("darkolivegreen", Color.rgb(0x55, 0x6B, 0x2F))
    val DarkOrange: Color.Rgb get() = NamedRgb("darkorange", Color.rgb(0xFF, 0x8C, 0x00))
    val DarkOrchid: Color.Rgb get() = NamedRgb("darkorchid", Color.rgb(0x99, 0x32, 0xCC))
    val DarkRed: Color.Rgb get() = NamedRgb("darkred", Color.rgb(0x8B, 0x00, 0x00))
    val DarkSalmon: Color.Rgb get() = NamedRgb("darksalmon", Color.rgb(0xE9, 0x96, 0x7A))
    val DarkSeaGreen: Color.Rgb get() = NamedRgb("darkseagreen", Color.rgb(0x8F, 0xBC, 0x8F))
    val DarkSlateBlue: Color.Rgb get() = NamedRgb("darkslateblue", Color.rgb(0x48, 0x3D, 0x8B))
    val DarkSlateGray: Color.Rgb get() = NamedRgb("darkslategray", Color.rgb(0x2F, 0x4F, 0x4F))
    val DarkSlateGrey: Color.Rgb get() = NamedRgb("darkslategrey", Color.rgb(0x2F, 0x4F, 0x4F))
    val DarkTurquoise: Color.Rgb get() = NamedRgb("darkturquoise", Color.rgb(0x00, 0xCE, 0xD1))
    val DarkViolet: Color.Rgb get() = NamedRgb("darkviolet", Color.rgb(0x94, 0x00, 0xD3))
    val DeepPink: Color.Rgb get() = NamedRgb("deeppink", Color.rgb(0xFF, 0x14, 0x93))
    val DeepSkyBlue: Color.Rgb get() = NamedRgb("deepskyblue", Color.rgb(0x00, 0xBF, 0xFF))
    val DimGray: Color.Rgb get() = NamedRgb("dimgray", Color.rgb(0x69, 0x69, 0x69))
    val DimGrey: Color.Rgb get() = NamedRgb("dimgrey", Color.rgb(0x69, 0x69, 0x69))
    val DodgerBlue: Color.Rgb get() = NamedRgb("dodgerblue", Color.rgb(0x1E, 0x90, 0xFF))
    val FireBrick: Color.Rgb get() = NamedRgb("firebrick", Color.rgb(0xB2, 0x22, 0x22))
    val FloralWhite: Color.Rgb get() = NamedRgb("floralwhite", Color.rgb(0xFF, 0xFA, 0xF0))
    val ForestGreen: Color.Rgb get() = NamedRgb("forestgreen", Color.rgb(0x22, 0x8B, 0x22))
    val Fuchsia: Color.Rgb get() = NamedRgb("fuchsia", Color.rgb(0xFF, 0x00, 0xFF))
    val Gainsboro: Color.Rgb get() = NamedRgb("gainsboro", Color.rgb(0xDC, 0xDC, 0xDC))
    val GhostWhite: Color.Rgb get() = NamedRgb("ghostwhite", Color.rgb(0xF8, 0xF8, 0xFF))
    val Gold: Color.Rgb get() = NamedRgb("gold", Color.rgb(0xFF, 0xD7, 0x00))
    val GoldenRod: Color.Rgb get() = NamedRgb("goldenrod", Color.rgb(0xDA, 0xA5, 0x20))
    val Gray: Color.Rgb get() = NamedRgb("gray", Color.rgb(0x80, 0x80, 0x80))
    val Grey: Color.Rgb get() = NamedRgb("grey", Color.rgb(0x80, 0x80, 0x80))
    val Green: Color.Rgb get() = NamedRgb("green", Color.rgb(0x00, 0x80, 0x00))
    val GreenYellow: Color.Rgb get() = NamedRgb("greenyellow", Color.rgb(0xAD, 0xFF, 0x2F))
    val HoneyDew: Color.Rgb get() = NamedRgb("honeydew", Color.rgb(0xF0, 0xFF, 0xF0))
    val HotPink: Color.Rgb get() = NamedRgb("hotpink", Color.rgb(0xFF, 0x69, 0xB4))
    val IndianRed: Color.Rgb get() = NamedRgb("indianred", Color.rgb(0xCD, 0x5C, 0x5C))
    val Indigo: Color.Rgb get() = NamedRgb("indigo", Color.rgb(0x4B, 0x00, 0x82))
    val Ivory: Color.Rgb get() = NamedRgb("ivory", Color.rgb(0xFF, 0xFF, 0xF0))
    val Khaki: Color.Rgb get() = NamedRgb("khaki", Color.rgb(0xF0, 0xE6, 0x8C))
    val Lavender: Color.Rgb get() = NamedRgb("lavender", Color.rgb(0xE6, 0xE6, 0xFA))
    val LavenderBlush: Color.Rgb get() = NamedRgb("lavenderblush", Color.rgb(0xFF, 0xF0, 0xF5))
    val LawnGreen: Color.Rgb get() = NamedRgb("lawngreen", Color.rgb(0x7C, 0xFC, 0x00))
    val LemonChiffon: Color.Rgb get() = NamedRgb("lemonchiffon", Color.rgb(0xFF, 0xFA, 0xCD))
    val LightBlue: Color.Rgb get() = NamedRgb("lightblue", Color.rgb(0xAD, 0xD8, 0xE6))
    val LightCoral: Color.Rgb get() = NamedRgb("lightcoral", Color.rgb(0xF0, 0x80, 0x80))
    val LightCyan: Color.Rgb get() = NamedRgb("lightcyan", Color.rgb(0xE0, 0xFF, 0xFF))
    val LightGoldenRodYellow: Color.Rgb get() = NamedRgb("lightgoldenrodyellow", Color.rgb(0xFA, 0xFA, 0xD2))
    val LightGray: Color.Rgb get() = NamedRgb("lightgray", Color.rgb(0xD3, 0xD3, 0xD3))
    val LightGrey: Color.Rgb get() = NamedRgb("lightgrey", Color.rgb(0xD3, 0xD3, 0xD3))
    val LightGreen: Color.Rgb get() = NamedRgb("lightgreen", Color.rgb(0x90, 0xEE, 0x90))
    val LightPink: Color.Rgb get() = NamedRgb("lightpink", Color.rgb(0xFF, 0xB6, 0xC1))
    val LightSalmon: Color.Rgb get() = NamedRgb("lightsalmon", Color.rgb(0xFF, 0xA0, 0x7A))
    val LightSeaGreen: Color.Rgb get() = NamedRgb("lightseagreen", Color.rgb(0x20, 0xB2, 0xAA))
    val LightSkyBlue: Color.Rgb get() = NamedRgb("lightskyblue", Color.rgb(0x87, 0xCE, 0xFA))
    val LightSlateGray: Color.Rgb get() = NamedRgb("lightslategray", Color.rgb(0x77, 0x88, 0x99))
    val LightSlateGrey: Color.Rgb get() = NamedRgb("lightslategrey", Color.rgb(0x77, 0x88, 0x99))
    val LightSteelBlue: Color.Rgb get() = NamedRgb("lightsteelblue", Color.rgb(0xB0, 0xC4, 0xDE))
    val LightYellow: Color.Rgb get() = NamedRgb("lightyellow", Color.rgb(0xFF, 0xFF, 0xE0))
    val Lime: Color.Rgb get() = NamedRgb("lime", Color.rgb(0x00, 0xFF, 0x00))
    val LimeGreen: Color.Rgb get() = NamedRgb("limegreen", Color.rgb(0x32, 0xCD, 0x32))
    val Linen: Color.Rgb get() = NamedRgb("linen", Color.rgb(0xFA, 0xF0, 0xE6))
    val Magenta: Color.Rgb get() = NamedRgb("magenta", Color.rgb(0xFF, 0x00, 0xFF))
    val Maroon: Color.Rgb get() = NamedRgb("maroon", Color.rgb(0x80, 0x00, 0x00))
    val MediumAquaMarine: Color.Rgb get() = NamedRgb("mediumaquamarine", Color.rgb(0x66, 0xCD, 0xAA))
    val MediumBlue: Color.Rgb get() = NamedRgb("mediumblue", Color.rgb(0x00, 0x00, 0xCD))
    val MediumOrchid: Color.Rgb get() = NamedRgb("mediumorchid", Color.rgb(0xBA, 0x55, 0xD3))
    val MediumPurple: Color.Rgb get() = NamedRgb("mediumpurple", Color.rgb(0x93, 0x70, 0xDB))
    val MediumSeaGreen: Color.Rgb get() = NamedRgb("mediumseagreen", Color.rgb(0x3C, 0xB3, 0x71))
    val MediumSlateBlue: Color.Rgb get() = NamedRgb("mediumslateblue", Color.rgb(0x7B, 0x68, 0xEE))
    val MediumSpringGreen: Color.Rgb get() = NamedRgb("mediumspringgreen", Color.rgb(0x00, 0xFA, 0x9A))
    val MediumTurquoise: Color.Rgb get() = NamedRgb("mediumturquoise", Color.rgb(0x48, 0xD1, 0xCC))
    val MediumVioletRed: Color.Rgb get() = NamedRgb("mediumvioletred", Color.rgb(0xC7, 0x15, 0x85))
    val MidnightBlue: Color.Rgb get() = NamedRgb("midnightblue", Color.rgb(0x19, 0x19, 0x70))
    val MintCream: Color.Rgb get() = NamedRgb("mintcream", Color.rgb(0xF5, 0xFF, 0xFA))
    val MistyRose: Color.Rgb get() = NamedRgb("mistyrose", Color.rgb(0xFF, 0xE4, 0xE1))
    val Moccasin: Color.Rgb get() = NamedRgb("moccasin", Color.rgb(0xFF, 0xE4, 0xB5))
    val NavajoWhite: Color.Rgb get() = NamedRgb("navajowhite", Color.rgb(0xFF, 0xDE, 0xAD))
    val Navy: Color.Rgb get() = NamedRgb("navy", Color.rgb(0x00, 0x00, 0x80))
    val OldLace: Color.Rgb get() = NamedRgb("oldlace", Color.rgb(0xFD, 0xF5, 0xE6))
    val Olive: Color.Rgb get() = NamedRgb("olive", Color.rgb(0x80, 0x80, 0x00))
    val OliveDrab: Color.Rgb get() = NamedRgb("olivedrab", Color.rgb(0x6B, 0x8E, 0x23))
    val Orange: Color.Rgb get() = NamedRgb("orange", Color.rgb(0xFF, 0xA5, 0x00))
    val OrangeRed: Color.Rgb get() = NamedRgb("orangered", Color.rgb(0xFF, 0x45, 0x00))
    val Orchid: Color.Rgb get() = NamedRgb("orchid", Color.rgb(0xDA, 0x70, 0xD6))
    val PaleGoldenRod: Color.Rgb get() = NamedRgb("palegoldenrod", Color.rgb(0xEE, 0xE8, 0xAA))
    val PaleGreen: Color.Rgb get() = NamedRgb("palegreen", Color.rgb(0x98, 0xFB, 0x98))
    val PaleTurquoise: Color.Rgb get() = NamedRgb("paleturquoise", Color.rgb(0xAF, 0xEE, 0xEE))
    val PaleVioletRed: Color.Rgb get() = NamedRgb("palevioletred", Color.rgb(0xDB, 0x70, 0x93))
    val PapayaWhip: Color.Rgb get() = NamedRgb("papayawhip", Color.rgb(0xFF, 0xEF, 0xD5))
    val PeachPuff: Color.Rgb get() = NamedRgb("peachpuff", Color.rgb(0xFF, 0xDA, 0xB9))
    val Peru: Color.Rgb get() = NamedRgb("peru", Color.rgb(0xCD, 0x85, 0x3F))
    val Pink: Color.Rgb get() = NamedRgb("pink", Color.rgb(0xFF, 0xC0, 0xCB))
    val Plum: Color.Rgb get() = NamedRgb("plum", Color.rgb(0xDD, 0xA0, 0xDD))
    val PowderBlue: Color.Rgb get() = NamedRgb("powderblue", Color.rgb(0xB0, 0xE0, 0xE6))
    val Purple: Color.Rgb get() = NamedRgb("purple", Color.rgb(0x80, 0x00, 0x80))
    val RebeccaPurple: Color.Rgb get() = NamedRgb("rebeccapurple", Color.rgb(0x66, 0x33, 0x99))
    val Red: Color.Rgb get() = NamedRgb("red", Color.rgb(0xFF, 0x00, 0x00))
    val RosyBrown: Color.Rgb get() = NamedRgb("rosybrown", Color.rgb(0xBC, 0x8F, 0x8F))
    val RoyalBlue: Color.Rgb get() = NamedRgb("royalblue", Color.rgb(0x41, 0x69, 0xE1))
    val SaddleBrown: Color.Rgb get() = NamedRgb("saddlebrown", Color.rgb(0x8B, 0x45, 0x13))
    val Salmon: Color.Rgb get() = NamedRgb("salmon", Color.rgb(0xFA, 0x80, 0x72))
    val SandyBrown: Color.Rgb get() = NamedRgb("sandybrown", Color.rgb(0xF4, 0xA4, 0x60))
    val SeaGreen: Color.Rgb get() = NamedRgb("seagreen", Color.rgb(0x2E, 0x8B, 0x57))
    val SeaShell: Color.Rgb get() = NamedRgb("seashell", Color.rgb(0xFF, 0xF5, 0xEE))
    val Sienna: Color.Rgb get() = NamedRgb("sienna", Color.rgb(0xA0, 0x52, 0x2D))
    val Silver: Color.Rgb get() = NamedRgb("silver", Color.rgb(0xC0, 0xC0, 0xC0))
    val SkyBlue: Color.Rgb get() = NamedRgb("skyblue", Color.rgb(0x87, 0xCE, 0xEB))
    val SlateBlue: Color.Rgb get() = NamedRgb("slateblue", Color.rgb(0x6A, 0x5A, 0xCD))
    val SlateGray: Color.Rgb get() = NamedRgb("slategray", Color.rgb(0x70, 0x80, 0x90))
    val SlateGrey: Color.Rgb get() = NamedRgb("slategrey", Color.rgb(0x70, 0x80, 0x90))
    val Snow: Color.Rgb get() = NamedRgb("snow", Color.rgb(0xFF, 0xFA, 0xFA))
    val SpringGreen: Color.Rgb get() = NamedRgb("springgreen", Color.rgb(0x00, 0xFF, 0x7F))
    val SteelBlue: Color.Rgb get() = NamedRgb("steelblue", Color.rgb(0x46, 0x82, 0xB4))
    val Tan: Color.Rgb get() = NamedRgb("tan", Color.rgb(0xD2, 0xB4, 0x8C))
    val Teal: Color.Rgb get() = NamedRgb("teal", Color.rgb(0x00, 0x80, 0x80))
    val Thistle: Color.Rgb get() = NamedRgb("thistle", Color.rgb(0xD8, 0xBF, 0xD8))
    val Tomato: Color.Rgb get() = NamedRgb("tomato", Color.rgb(0xFF, 0x63, 0x47))
    val Turquoise: Color.Rgb get() = NamedRgb("turquoise", Color.rgb(0x40, 0xE0, 0xD0))
    val Violet: Color.Rgb get() = NamedRgb("violet", Color.rgb(0xEE, 0x82, 0xEE))
    val Wheat: Color.Rgb get() = NamedRgb("wheat", Color.rgb(0xF5, 0xDE, 0xB3))
    val White: Color.Rgb get() = NamedRgb("white", Color.rgb(0xFF, 0xFF, 0xFF))
    val WhiteSmoke: Color.Rgb get() = NamedRgb("whitesmoke", Color.rgb(0xF5, 0xF5, 0xF5))
    val Yellow: Color.Rgb get() = NamedRgb("yellow", Color.rgb(0xFF, 0xFF, 0x00))
    val YellowGreen: Color.Rgb get() = NamedRgb("yellowgreen", Color.rgb(0x9A, 0xCD, 0x32))
}
