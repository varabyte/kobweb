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
     *   color rgb values by some percent amount (so the final result depends upon initial values)
     */
    fun darkened(byPercent: Float = DEFAULT_SHIFTING_PERCENT): Color

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
    inline val Transparent get() = Color.rgba(0, 0, 0, 0)

    // From https://www.w3schools.com/colors/colors_names.asp
    inline val AliceBlue get() = Color.rgb(0xF0, 0xF8, 0xFF)
    inline val AntiqueWhite get() = Color.rgb(0xFA, 0xEB, 0xD7)
    inline val Aqua get() = Color.rgb(0x00, 0xFF, 0xFF)
    inline val Aquamarine get() = Color.rgb(0x7F, 0xFF, 0xD4)
    inline val Azure get() = Color.rgb(0xF0, 0xFF, 0xFF)
    inline val Beige get() = Color.rgb(0xF5, 0xF5, 0xDC)
    inline val Bisque get() = Color.rgb(0xFF, 0xE4, 0xC4)
    inline val Black get() = Color.rgb(0x00, 0x00, 0x00)
    inline val BlanchedAlmond get() = Color.rgb(0xFF, 0xEB, 0xCD)
    inline val Blue get() = Color.rgb(0x00, 0x00, 0xFF)
    inline val BlueViolet get() = Color.rgb(0x8A, 0x2B, 0xE2)
    inline val Brown get() = Color.rgb(0xA5, 0x2A, 0x2A)
    inline val BurlyWood get() = Color.rgb(0xDE, 0xB8, 0x87)
    inline val CadetBlue get() = Color.rgb(0x5F, 0x9E, 0xA0)
    inline val Chartreuse get() = Color.rgb(0x7F, 0xFF, 0x00)
    inline val Chocolate get() = Color.rgb(0xD2, 0x69, 0x1E)
    inline val Coral get() = Color.rgb(0xFF, 0x7F, 0x50)
    inline val CornflowerBlue get() = Color.rgb(0x64, 0x95, 0xED)
    inline val Cornsilk get() = Color.rgb(0xFF, 0xF8, 0xDC)
    inline val Crimson get() = Color.rgb(0xDC, 0x14, 0x3C)
    inline val Cyan get() = Color.rgb(0x00, 0xFF, 0xFF)
    inline val DarkBlue get() = Color.rgb(0x00, 0x00, 0x8B)
    inline val DarkCyan get() = Color.rgb(0x00, 0x8B, 0x8B)
    inline val DarkGoldenRod get() = Color.rgb(0xB8, 0x86, 0x0B)
    inline val DarkGray get() = Color.rgb(0xA9, 0xA9, 0xA9)
    inline val DarkGrey get() = Color.rgb(0xA9, 0xA9, 0xA9)
    inline val DarkGreen get() = Color.rgb(0x00, 0x64, 0x00)
    inline val DarkKhaki get() = Color.rgb(0xBD, 0xB7, 0x6B)
    inline val DarkMagenta get() = Color.rgb(0x8B, 0x00, 0x8B)
    inline val DarkOliveGreen get() = Color.rgb(0x55, 0x6B, 0x2F)
    inline val DarkOrange get() = Color.rgb(0xFF, 0x8C, 0x00)
    inline val DarkOrchid get() = Color.rgb(0x99, 0x32, 0xCC)
    inline val DarkRed get() = Color.rgb(0x8B, 0x00, 0x00)
    inline val DarkSalmon get() = Color.rgb(0xE9, 0x96, 0x7A)
    inline val DarkSeaGreen get() = Color.rgb(0x8F, 0xBC, 0x8F)
    inline val DarkSlateBlue get() = Color.rgb(0x48, 0x3D, 0x8B)
    inline val DarkSlateGray get() = Color.rgb(0x2F, 0x4F, 0x4F)
    inline val DarkSlateGrey get() = Color.rgb(0x2F, 0x4F, 0x4F)
    inline val DarkTurquoise get() = Color.rgb(0x00, 0xCE, 0xD1)
    inline val DarkViolet get() = Color.rgb(0x94, 0x00, 0xD3)
    inline val DeepPink get() = Color.rgb(0xFF, 0x14, 0x93)
    inline val DeepSkyBlue get() = Color.rgb(0x00, 0xBF, 0xFF)
    inline val DimGray get() = Color.rgb(0x69, 0x69, 0x69)
    inline val DimGrey get() = Color.rgb(0x69, 0x69, 0x69)
    inline val DodgerBlue get() = Color.rgb(0x1E, 0x90, 0xFF)
    inline val FireBrick get() = Color.rgb(0xB2, 0x22, 0x22)
    inline val FloralWhite get() = Color.rgb(0xFF, 0xFA, 0xF0)
    inline val ForestGreen get() = Color.rgb(0x22, 0x8B, 0x22)
    inline val Fuchsia get() = Color.rgb(0xFF, 0x00, 0xFF)
    inline val Gainsboro get() = Color.rgb(0xDC, 0xDC, 0xDC)
    inline val GhostWhite get() = Color.rgb(0xF8, 0xF8, 0xFF)
    inline val Gold get() = Color.rgb(0xFF, 0xD7, 0x00)
    inline val GoldenRod get() = Color.rgb(0xDA, 0xA5, 0x20)
    inline val Gray get() = Color.rgb(0x80, 0x80, 0x80)
    inline val Grey get() = Color.rgb(0x80, 0x80, 0x80)
    inline val Green get() = Color.rgb(0x00, 0x80, 0x00)
    inline val GreenYellow get() = Color.rgb(0xAD, 0xFF, 0x2F)
    inline val HoneyDew get() = Color.rgb(0xF0, 0xFF, 0xF0)
    inline val HotPink get() = Color.rgb(0xFF, 0x69, 0xB4)
    inline val IndianRed get() = Color.rgb(0xCD, 0x5C, 0x5C)
    inline val Indigo get() = Color.rgb(0x4B, 0x00, 0x82)
    inline val Ivory get() = Color.rgb(0xFF, 0xFF, 0xF0)
    inline val Khaki get() = Color.rgb(0xF0, 0xE6, 0x8C)
    inline val Lavender get() = Color.rgb(0xE6, 0xE6, 0xFA)
    inline val LavenderBlush get() = Color.rgb(0xFF, 0xF0, 0xF5)
    inline val LawnGreen get() = Color.rgb(0x7C, 0xFC, 0x00)
    inline val LemonChiffon get() = Color.rgb(0xFF, 0xFA, 0xCD)
    inline val LightBlue get() = Color.rgb(0xAD, 0xD8, 0xE6)
    inline val LightCoral get() = Color.rgb(0xF0, 0x80, 0x80)
    inline val LightCyan get() = Color.rgb(0xE0, 0xFF, 0xFF)
    inline val LightGoldenRodYellow get() = Color.rgb(0xFA, 0xFA, 0xD2)
    inline val LightGray get() = Color.rgb(0xD3, 0xD3, 0xD3)
    inline val LightGrey get() = Color.rgb(0xD3, 0xD3, 0xD3)
    inline val LightGreen get() = Color.rgb(0x90, 0xEE, 0x90)
    inline val LightPink get() = Color.rgb(0xFF, 0xB6, 0xC1)
    inline val LightSalmon get() = Color.rgb(0xFF, 0xA0, 0x7A)
    inline val LightSeaGreen get() = Color.rgb(0x20, 0xB2, 0xAA)
    inline val LightSkyBlue get() = Color.rgb(0x87, 0xCE, 0xFA)
    inline val LightSlateGray get() = Color.rgb(0x77, 0x88, 0x99)
    inline val LightSlateGrey get() = Color.rgb(0x77, 0x88, 0x99)
    inline val LightSteelBlue get() = Color.rgb(0xB0, 0xC4, 0xDE)
    inline val LightYellow get() = Color.rgb(0xFF, 0xFF, 0xE0)
    inline val Lime get() = Color.rgb(0x00, 0xFF, 0x00)
    inline val LimeGreen get() = Color.rgb(0x32, 0xCD, 0x32)
    inline val Linen get() = Color.rgb(0xFA, 0xF0, 0xE6)
    inline val Magenta get() = Color.rgb(0xFF, 0x00, 0xFF)
    inline val Maroon get() = Color.rgb(0x80, 0x00, 0x00)
    inline val MediumAquaMarine get() = Color.rgb(0x66, 0xCD, 0xAA)
    inline val MediumBlue get() = Color.rgb(0x00, 0x00, 0xCD)
    inline val MediumOrchid get() = Color.rgb(0xBA, 0x55, 0xD3)
    inline val MediumPurple get() = Color.rgb(0x93, 0x70, 0xDB)
    inline val MediumSeaGreen get() = Color.rgb(0x3C, 0xB3, 0x71)
    inline val MediumSlateBlue get() = Color.rgb(0x7B, 0x68, 0xEE)
    inline val MediumSpringGreen get() = Color.rgb(0x00, 0xFA, 0x9A)
    inline val MediumTurquoise get() = Color.rgb(0x48, 0xD1, 0xCC)
    inline val MediumVioletRed get() = Color.rgb(0xC7, 0x15, 0x85)
    inline val MidnightBlue get() = Color.rgb(0x19, 0x19, 0x70)
    inline val MintCream get() = Color.rgb(0xF5, 0xFF, 0xFA)
    inline val MistyRose get() = Color.rgb(0xFF, 0xE4, 0xE1)
    inline val Moccasin get() = Color.rgb(0xFF, 0xE4, 0xB5)
    inline val NavajoWhite get() = Color.rgb(0xFF, 0xDE, 0xAD)
    inline val Navy get() = Color.rgb(0x00, 0x00, 0x80)
    inline val OldLace get() = Color.rgb(0xFD, 0xF5, 0xE6)
    inline val Olive get() = Color.rgb(0x80, 0x80, 0x00)
    inline val OliveDrab get() = Color.rgb(0x6B, 0x8E, 0x23)
    inline val Orange get() = Color.rgb(0xFF, 0xA5, 0x00)
    inline val OrangeRed get() = Color.rgb(0xFF, 0x45, 0x00)
    inline val Orchid get() = Color.rgb(0xDA, 0x70, 0xD6)
    inline val PaleGoldenRod get() = Color.rgb(0xEE, 0xE8, 0xAA)
    inline val PaleGreen get() = Color.rgb(0x98, 0xFB, 0x98)
    inline val PaleTurquoise get() = Color.rgb(0xAF, 0xEE, 0xEE)
    inline val PaleVioletRed get() = Color.rgb(0xDB, 0x70, 0x93)
    inline val PapayaWhip get() = Color.rgb(0xFF, 0xEF, 0xD5)
    inline val PeachPuff get() = Color.rgb(0xFF, 0xDA, 0xB9)
    inline val Peru get() = Color.rgb(0xCD, 0x85, 0x3F)
    inline val Pink get() = Color.rgb(0xFF, 0xC0, 0xCB)
    inline val Plum get() = Color.rgb(0xDD, 0xA0, 0xDD)
    inline val PowderBlue get() = Color.rgb(0xB0, 0xE0, 0xE6)
    inline val Purple get() = Color.rgb(0x80, 0x00, 0x80)
    inline val RebeccaPurple get() = Color.rgb(0x66, 0x33, 0x99)
    inline val Red get() = Color.rgb(0xFF, 0x00, 0x00)
    inline val RosyBrown get() = Color.rgb(0xBC, 0x8F, 0x8F)
    inline val RoyalBlue get() = Color.rgb(0x41, 0x69, 0xE1)
    inline val SaddleBrown get() = Color.rgb(0x8B, 0x45, 0x13)
    inline val Salmon get() = Color.rgb(0xFA, 0x80, 0x72)
    inline val SandyBrown get() = Color.rgb(0xF4, 0xA4, 0x60)
    inline val SeaGreen get() = Color.rgb(0x2E, 0x8B, 0x57)
    inline val SeaShell get() = Color.rgb(0xFF, 0xF5, 0xEE)
    inline val Sienna get() = Color.rgb(0xA0, 0x52, 0x2D)
    inline val Silver get() = Color.rgb(0xC0, 0xC0, 0xC0)
    inline val SkyBlue get() = Color.rgb(0x87, 0xCE, 0xEB)
    inline val SlateBlue get() = Color.rgb(0x6A, 0x5A, 0xCD)
    inline val SlateGray get() = Color.rgb(0x70, 0x80, 0x90)
    inline val SlateGrey get() = Color.rgb(0x70, 0x80, 0x90)
    inline val Snow get() = Color.rgb(0xFF, 0xFA, 0xFA)
    inline val SpringGreen get() = Color.rgb(0x00, 0xFF, 0x7F)
    inline val SteelBlue get() = Color.rgb(0x46, 0x82, 0xB4)
    inline val Tan get() = Color.rgb(0xD2, 0xB4, 0x8C)
    inline val Teal get() = Color.rgb(0x00, 0x80, 0x80)
    inline val Thistle get() = Color.rgb(0xD8, 0xBF, 0xD8)
    inline val Tomato get() = Color.rgb(0xFF, 0x63, 0x47)
    inline val Turquoise get() = Color.rgb(0x40, 0xE0, 0xD0)
    inline val Violet get() = Color.rgb(0xEE, 0x82, 0xEE)
    inline val Wheat get() = Color.rgb(0xF5, 0xDE, 0xB3)
    inline val White get() = Color.rgb(0xFF, 0xFF, 0xFF)
    inline val WhiteSmoke get() = Color.rgb(0xF5, 0xF5, 0xF5)
    inline val Yellow get() = Color.rgb(0xFF, 0xFF, 0x00)
    inline val YellowGreen get() = Color.rgb(0x9A, 0xCD, 0x32)
}