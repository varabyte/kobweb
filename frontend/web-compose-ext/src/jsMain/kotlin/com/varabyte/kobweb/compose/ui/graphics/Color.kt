package com.varabyte.kobweb.compose.ui.graphics

private fun Float.toColorInt() = (this.coerceIn(0f, 1f) * 255.0f).toInt()
private fun Int.toColorFloat() = this.and(0xFF) / 255.0f

private val DARKENING_AMOUNT = 0.7f

/**
 * A base class for colors which provide additional functionality on top of the color class included in Web Compose.
 *
 * Use [toCssColor] to convert between this class and the Web Compose version.
 */
sealed interface Color {
    fun inverted(): Color
    fun darkened(): Color

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
        override fun darkened() =
            rgba(redf * DARKENING_AMOUNT, greenf * DARKENING_AMOUNT, bluef * DARKENING_AMOUNT, alphaf)

        fun copy(red: Int = this.red, green: Int = this.green, blue: Int = this.blue, alpha: Int = this.alpha) =
            rgba(red, green, blue, alpha)

        fun copyf(red: Float = redf, green: Float = this.greenf, blue: Float = this.bluef, alpha: Float = this.alphaf) =
            rgba(red, green, blue, alpha)

        override fun toString(): String {
            return if (alpha == 0xFF) "rgb(r=$red g=$green b=$blue)" else "rgba(r=$red g=$green b=$blue a=$alpha)"
        }
    }

    companion object {
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

        val Transparent = rgba(0, 0, 0, 0)
        val Black = rgb(0, 0, 0)
        val DarkGray = rgb(0x44, 0x44, 0x44)
        val Gray = rgb(0x88, 0x88, 0x88)
        val LightGray = rgb(0xCC, 0xCC, 0xCC)
        val White = rgb(0xFF, 0xFF, 0xFF)
        val Red = rgb(0xFF, 0, 0)
        val Green = rgb(0, 0xFF, 0)
        val Blue = rgb(0, 0, 0xFF)
        val Yellow = rgb(0xFF, 0xFF, 0x00)
        val Cyan = rgb(0, 0xFF, 0xFF)
        val Magenta = rgb(0xFF, 0, 0xFF)

        val Purple = rgb(0x88, 0x00, 0x88)
    }
}

fun Color.lightened() = inverted().darkened().inverted()