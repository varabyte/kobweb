package com.varabyte.kobweb.compose.ui.graphics

import org.jetbrains.compose.web.css.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt

private fun Float.toColorInt() = (this.coerceIn(0f, 1f) * 255.0f).toInt()
private fun Int.toColorFloat() = this.and(0xFF) / 255.0f

private fun CSSAngleValue.toDegrees() = when (this.unit.toString()) {
    "deg" -> value
    "grad" -> (value * 0.9f)
    "rad" -> (value * 180f / PI.toFloat())
    "turn" -> (value * 360f)
    else -> error("Unexpected unit type ${this.unit}")
} % 360f

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

    /**
     * @property value A hex value representing this color as AARRGGBB, e.g. 0xFFFF0000 is red and 0xFF0000FF is blue.
     */
    class Rgb internal constructor(val value: Int) : Color {
        val red: Int get() = value.shr(16).and(0xFF)
        val green: Int get() = value.shr(8).and(0xFF)
        val blue: Int get() = value.shr(0).and(0xFF)
        val alpha: Int get() = value.shr(24).and(0xFF)

        val redf: Float get() = red.toColorFloat()
        val greenf: Float get() = green.toColorFloat()
        val bluef: Float get() = blue.toColorFloat()
        val alphaf: Float get() = alpha.toColorFloat()

        override fun inverted(): Color = rgba(255 - red, 255 - green, 255 - blue, alpha)
        override fun darkened(byPercent: Float): Color {
            require(byPercent in (0f..1f)) { "Invalid color shifting percent. Expected between 0 and 1, got $byPercent" }
            if (byPercent == 0f) return this

            val darkeningMultiplier = 1.0f - byPercent // e.g. reduce by 20% means take 80% of the current value
            return rgba(redf * darkeningMultiplier, greenf * darkeningMultiplier, bluef * darkeningMultiplier, alphaf)
        }

        fun copy(red: Int = this.red, green: Int = this.green, blue: Int = this.blue, alpha: Int = this.alpha) =
            rgba(red, green, blue, alpha)

        fun copyf(red: Float = redf, green: Float = this.greenf, blue: Float = this.bluef, alpha: Float = this.alphaf) =
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
            val r: Float;
            val g: Float;
            val b: Float;
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

        fun rgb(value: Int) = Rgb(0xFF.shl(24).or(value))
        fun argb(value: Int) = Rgb(value)
        fun argb(value: Long) = argb(value.toInRangeInt())

        fun rgba(value: Int) = run {
            // Convert RRGGBBAA to AARRGGBB
            val alpha = value.and(0xFF).shl(24)
            val rgb = value.shr(8)
            argb(alpha.or(rgb))
        }
        fun rgba(value: Long) = rgba(value.toInRangeInt())

        fun rgb(r: Int, g: Int, b: Int) = rgba(r, g, b, 0xFF)
        fun rgba(r: Int, g: Int, b: Int, a: Int) = Rgb(
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
    val Transparent get() = Color.rgba(0, 0, 0, 0)

    // From https://www.w3schools.com/colors/colors_names.asp
    val AliceBlue get() = Color.rgb(0xF0, 0xF8, 0xFF)
    val AntiqueWhite get() = Color.rgb(0xFA, 0xEB, 0xD7)
    val Aqua get() = Color.rgb(0x00, 0xFF, 0xFF)
    val Aquamarine get() = Color.rgb(0x7F, 0xFF, 0xD4)
    val Azure get() = Color.rgb(0xF0, 0xFF, 0xFF)
    val Beige get() = Color.rgb(0xF5, 0xF5, 0xDC)
    val Bisque get() = Color.rgb(0xFF, 0xE4, 0xC4)
    val Black get() = Color.rgb(0x00, 0x00, 0x00)
    val BlanchedAlmond get() = Color.rgb(0xFF, 0xEB, 0xCD)
    val Blue get() = Color.rgb(0x00, 0x00, 0xFF)
    val BlueViolet get() = Color.rgb(0x8A, 0x2B, 0xE2)
    val Brown get() = Color.rgb(0xA5, 0x2A, 0x2A)
    val BurlyWood get() = Color.rgb(0xDE, 0xB8, 0x87)
    val CadetBlue get() = Color.rgb(0x5F, 0x9E, 0xA0)
    val Chartreuse get() = Color.rgb(0x7F, 0xFF, 0x00)
    val Chocolate get() = Color.rgb(0xD2, 0x69, 0x1E)
    val Coral get() = Color.rgb(0xFF, 0x7F, 0x50)
    val CornflowerBlue get() = Color.rgb(0x64, 0x95, 0xED)
    val Cornsilk get() = Color.rgb(0xFF, 0xF8, 0xDC)
    val Crimson get() = Color.rgb(0xDC, 0x14, 0x3C)
    val Cyan get() = Color.rgb(0x00, 0xFF, 0xFF)
    val DarkBlue get() = Color.rgb(0x00, 0x00, 0x8B)
    val DarkCyan get() = Color.rgb(0x00, 0x8B, 0x8B)
    val DarkGoldenRod get() = Color.rgb(0xB8, 0x86, 0x0B)
    val DarkGray get() = Color.rgb(0xA9, 0xA9, 0xA9)
    val DarkGrey get() = Color.rgb(0xA9, 0xA9, 0xA9)
    val DarkGreen get() = Color.rgb(0x00, 0x64, 0x00)
    val DarkKhaki get() = Color.rgb(0xBD, 0xB7, 0x6B)
    val DarkMagenta get() = Color.rgb(0x8B, 0x00, 0x8B)
    val DarkOliveGreen get() = Color.rgb(0x55, 0x6B, 0x2F)
    val DarkOrange get() = Color.rgb(0xFF, 0x8C, 0x00)
    val DarkOrchid get() = Color.rgb(0x99, 0x32, 0xCC)
    val DarkRed get() = Color.rgb(0x8B, 0x00, 0x00)
    val DarkSalmon get() = Color.rgb(0xE9, 0x96, 0x7A)
    val DarkSeaGreen get() = Color.rgb(0x8F, 0xBC, 0x8F)
    val DarkSlateBlue get() = Color.rgb(0x48, 0x3D, 0x8B)
    val DarkSlateGray get() = Color.rgb(0x2F, 0x4F, 0x4F)
    val DarkSlateGrey get() = Color.rgb(0x2F, 0x4F, 0x4F)
    val DarkTurquoise get() = Color.rgb(0x00, 0xCE, 0xD1)
    val DarkViolet get() = Color.rgb(0x94, 0x00, 0xD3)
    val DeepPink get() = Color.rgb(0xFF, 0x14, 0x93)
    val DeepSkyBlue get() = Color.rgb(0x00, 0xBF, 0xFF)
    val DimGray get() = Color.rgb(0x69, 0x69, 0x69)
    val DimGrey get() = Color.rgb(0x69, 0x69, 0x69)
    val DodgerBlue get() = Color.rgb(0x1E, 0x90, 0xFF)
    val FireBrick get() = Color.rgb(0xB2, 0x22, 0x22)
    val FloralWhite get() = Color.rgb(0xFF, 0xFA, 0xF0)
    val ForestGreen get() = Color.rgb(0x22, 0x8B, 0x22)
    val Fuchsia get() = Color.rgb(0xFF, 0x00, 0xFF)
    val Gainsboro get() = Color.rgb(0xDC, 0xDC, 0xDC)
    val GhostWhite get() = Color.rgb(0xF8, 0xF8, 0xFF)
    val Gold get() = Color.rgb(0xFF, 0xD7, 0x00)
    val GoldenRod get() = Color.rgb(0xDA, 0xA5, 0x20)
    val Gray get() = Color.rgb(0x80, 0x80, 0x80)
    val Grey get() = Color.rgb(0x80, 0x80, 0x80)
    val Green get() = Color.rgb(0x00, 0x80, 0x00)
    val GreenYellow get() = Color.rgb(0xAD, 0xFF, 0x2F)
    val HoneyDew get() = Color.rgb(0xF0, 0xFF, 0xF0)
    val HotPink get() = Color.rgb(0xFF, 0x69, 0xB4)
    val IndianRed get() = Color.rgb(0xCD, 0x5C, 0x5C)
    val Indigo get() = Color.rgb(0x4B, 0x00, 0x82)
    val Ivory get() = Color.rgb(0xFF, 0xFF, 0xF0)
    val Khaki get() = Color.rgb(0xF0, 0xE6, 0x8C)
    val Lavender get() = Color.rgb(0xE6, 0xE6, 0xFA)
    val LavenderBlush get() = Color.rgb(0xFF, 0xF0, 0xF5)
    val LawnGreen get() = Color.rgb(0x7C, 0xFC, 0x00)
    val LemonChiffon get() = Color.rgb(0xFF, 0xFA, 0xCD)
    val LightBlue get() = Color.rgb(0xAD, 0xD8, 0xE6)
    val LightCoral get() = Color.rgb(0xF0, 0x80, 0x80)
    val LightCyan get() = Color.rgb(0xE0, 0xFF, 0xFF)
    val LightGoldenRodYellow get() = Color.rgb(0xFA, 0xFA, 0xD2)
    val LightGray get() = Color.rgb(0xD3, 0xD3, 0xD3)
    val LightGrey get() = Color.rgb(0xD3, 0xD3, 0xD3)
    val LightGreen get() = Color.rgb(0x90, 0xEE, 0x90)
    val LightPink get() = Color.rgb(0xFF, 0xB6, 0xC1)
    val LightSalmon get() = Color.rgb(0xFF, 0xA0, 0x7A)
    val LightSeaGreen get() = Color.rgb(0x20, 0xB2, 0xAA)
    val LightSkyBlue get() = Color.rgb(0x87, 0xCE, 0xFA)
    val LightSlateGray get() = Color.rgb(0x77, 0x88, 0x99)
    val LightSlateGrey get() = Color.rgb(0x77, 0x88, 0x99)
    val LightSteelBlue get() = Color.rgb(0xB0, 0xC4, 0xDE)
    val LightYellow get() = Color.rgb(0xFF, 0xFF, 0xE0)
    val Lime get() = Color.rgb(0x00, 0xFF, 0x00)
    val LimeGreen get() = Color.rgb(0x32, 0xCD, 0x32)
    val Linen get() = Color.rgb(0xFA, 0xF0, 0xE6)
    val Magenta get() = Color.rgb(0xFF, 0x00, 0xFF)
    val Maroon get() = Color.rgb(0x80, 0x00, 0x00)
    val MediumAquaMarine get() = Color.rgb(0x66, 0xCD, 0xAA)
    val MediumBlue get() = Color.rgb(0x00, 0x00, 0xCD)
    val MediumOrchid get() = Color.rgb(0xBA, 0x55, 0xD3)
    val MediumPurple get() = Color.rgb(0x93, 0x70, 0xDB)
    val MediumSeaGreen get() = Color.rgb(0x3C, 0xB3, 0x71)
    val MediumSlateBlue get() = Color.rgb(0x7B, 0x68, 0xEE)
    val MediumSpringGreen get() = Color.rgb(0x00, 0xFA, 0x9A)
    val MediumTurquoise get() = Color.rgb(0x48, 0xD1, 0xCC)
    val MediumVioletRed get() = Color.rgb(0xC7, 0x15, 0x85)
    val MidnightBlue get() = Color.rgb(0x19, 0x19, 0x70)
    val MintCream get() = Color.rgb(0xF5, 0xFF, 0xFA)
    val MistyRose get() = Color.rgb(0xFF, 0xE4, 0xE1)
    val Moccasin get() = Color.rgb(0xFF, 0xE4, 0xB5)
    val NavajoWhite get() = Color.rgb(0xFF, 0xDE, 0xAD)
    val Navy get() = Color.rgb(0x00, 0x00, 0x80)
    val OldLace get() = Color.rgb(0xFD, 0xF5, 0xE6)
    val Olive get() = Color.rgb(0x80, 0x80, 0x00)
    val OliveDrab get() = Color.rgb(0x6B, 0x8E, 0x23)
    val Orange get() = Color.rgb(0xFF, 0xA5, 0x00)
    val OrangeRed get() = Color.rgb(0xFF, 0x45, 0x00)
    val Orchid get() = Color.rgb(0xDA, 0x70, 0xD6)
    val PaleGoldenRod get() = Color.rgb(0xEE, 0xE8, 0xAA)
    val PaleGreen get() = Color.rgb(0x98, 0xFB, 0x98)
    val PaleTurquoise get() = Color.rgb(0xAF, 0xEE, 0xEE)
    val PaleVioletRed get() = Color.rgb(0xDB, 0x70, 0x93)
    val PapayaWhip get() = Color.rgb(0xFF, 0xEF, 0xD5)
    val PeachPuff get() = Color.rgb(0xFF, 0xDA, 0xB9)
    val Peru get() = Color.rgb(0xCD, 0x85, 0x3F)
    val Pink get() = Color.rgb(0xFF, 0xC0, 0xCB)
    val Plum get() = Color.rgb(0xDD, 0xA0, 0xDD)
    val PowderBlue get() = Color.rgb(0xB0, 0xE0, 0xE6)
    val Purple get() = Color.rgb(0x80, 0x00, 0x80)
    val RebeccaPurple get() = Color.rgb(0x66, 0x33, 0x99)
    val Red get() = Color.rgb(0xFF, 0x00, 0x00)
    val RosyBrown get() = Color.rgb(0xBC, 0x8F, 0x8F)
    val RoyalBlue get() = Color.rgb(0x41, 0x69, 0xE1)
    val SaddleBrown get() = Color.rgb(0x8B, 0x45, 0x13)
    val Salmon get() = Color.rgb(0xFA, 0x80, 0x72)
    val SandyBrown get() = Color.rgb(0xF4, 0xA4, 0x60)
    val SeaGreen get() = Color.rgb(0x2E, 0x8B, 0x57)
    val SeaShell get() = Color.rgb(0xFF, 0xF5, 0xEE)
    val Sienna get() = Color.rgb(0xA0, 0x52, 0x2D)
    val Silver get() = Color.rgb(0xC0, 0xC0, 0xC0)
    val SkyBlue get() = Color.rgb(0x87, 0xCE, 0xEB)
    val SlateBlue get() = Color.rgb(0x6A, 0x5A, 0xCD)
    val SlateGray get() = Color.rgb(0x70, 0x80, 0x90)
    val SlateGrey get() = Color.rgb(0x70, 0x80, 0x90)
    val Snow get() = Color.rgb(0xFF, 0xFA, 0xFA)
    val SpringGreen get() = Color.rgb(0x00, 0xFF, 0x7F)
    val SteelBlue get() = Color.rgb(0x46, 0x82, 0xB4)
    val Tan get() = Color.rgb(0xD2, 0xB4, 0x8C)
    val Teal get() = Color.rgb(0x00, 0x80, 0x80)
    val Thistle get() = Color.rgb(0xD8, 0xBF, 0xD8)
    val Tomato get() = Color.rgb(0xFF, 0x63, 0x47)
    val Turquoise get() = Color.rgb(0x40, 0xE0, 0xD0)
    val Violet get() = Color.rgb(0xEE, 0x82, 0xEE)
    val Wheat get() = Color.rgb(0xF5, 0xDE, 0xB3)
    val White get() = Color.rgb(0xFF, 0xFF, 0xFF)
    val WhiteSmoke get() = Color.rgb(0xF5, 0xF5, 0xF5)
    val Yellow get() = Color.rgb(0xFF, 0xFF, 0x00)
    val YellowGreen get() = Color.rgb(0x9A, 0xCD, 0x32)
}
