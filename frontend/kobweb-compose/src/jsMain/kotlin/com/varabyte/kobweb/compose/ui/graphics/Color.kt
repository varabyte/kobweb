package com.varabyte.kobweb.compose.ui.graphics

private fun Float.toColorInt() = (this.coerceIn(0f, 1f) * 255.0f).toInt()
private fun Int.toColorFloat() = this.and(0xFF) / 255.0f

/**
 * A base class for colors which provide additional functionality on top of the color class included in Compose for Web.
 *
 * Use [toCssColor] to convert this class to the Compose for Web version.
 */
sealed interface Color {
    fun inverted(): Color
    /**
     * Darken this color by some target percent value
     *
     * @param byPercent A value between 0 (no change) and 1 (will result in black). Otherwise, darken the current
     *   color rgb values by some percent amount (meaning the final result depends upon initial values)
     */
    fun darkened(byPercent: Float = DEFAULT_SHIFTING_PERCENT): Color

    fun toRgb(): Color.Rgb

    class Rgb internal constructor(val value: Int) : Color {
        val red: Int get() = value.shr(16).and(0xFF)
        val green: Int get() = value.shr(8).and(0xFF)
        val blue: Int get() = value.shr(0).and(0xFF)
        val alpha: Int get() = value.shr(24).and(0xFF)

        val redf: Float get() = red.toColorFloat()
        val greenf: Float get() = green.toColorFloat()
        val bluef: Float get() = blue.toColorFloat()
        val alphaf: Float get() = alpha.toColorFloat()

        override fun inverted() = rgba(255 - red, 255 - green, 255 - blue, alpha)
        override fun darkened(byPercent: Float): Rgb {
            require(byPercent in (0f..1f)) { "Invalid color shifting percent. Expected between 0 and 1, got $byPercent"}
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

        override fun toString(): String {
            return if (alpha == 0xFF) "rgb(r=$red g=$green b=$blue)" else "rgba(r=$red g=$green b=$blue a=$alpha)"
        }
    }

    companion object {
        const val DEFAULT_SHIFTING_PERCENT = 0.3f

        fun rgb(value: Int) = Rgb(0xFF.shl(24).or(value))
        fun rgba(value: Int) = Rgb(value)

        fun rgb(r: Int, g: Int, b: Int) = rgba(r, g, b, 0xFF)
        fun rgba(r: Int, g: Int, b: Int, a: Int) = Rgb(
            r.and(0xFF).shl(16)
                .or(g.and(0xFF).shl(8))
                .or(b.and(0xFF).shl(0))
                .or(a.and(0xFF).shl(24))
        )

        fun rgb(r: Float, g: Float, b: Float) = rgb(r.toColorInt(), g.toColorInt(), b.toColorInt())
        fun rgba(r: Float, g: Float, b: Float, a: Float) =
            rgba(r.toColorInt(), g.toColorInt(), b.toColorInt(), a.toColorInt())

        fun rgba(r: Int, g: Int, b: Int, a: Float) = rgba(r, g, b, a.toColorInt())
    }
}

/**
 * Lighten this color by some target percent value
 *
 * @param byPercent A value between 0 (no change) and 1 (will result in white). Otherwise, lighten the current
 *   color rgb values by some percent amount (so the final result depends upon initial values)
 */
fun Color.lightened(byPercent: Float = Color.DEFAULT_SHIFTING_PERCENT) = inverted().darkened(byPercent).inverted()

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