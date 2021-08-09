package kobweb.compose.ui.graphics

import org.jetbrains.compose.common.core.graphics.Color as JbColor

fun JbColor.toSilkColor() = Color(red, green, blue)
fun Color.toJbColor() = JbColor(red, green, blue)

private fun Float.toColorInt() = (this.coerceIn(0f, 1f) * 255.0f).toInt()
private fun Int.toColorFloat() = this.and(0xFF) / 255.0f

private val DARKENING_AMOUNT = 0.7f

/**
 * A color which, unlike the current color class provided by JB compose, also supports alpha.
 *
 * @param value A 32-bit encoding of this color: AARRGGBB
 */
class Color(val value: Int) {
    constructor(r: Int, g: Int, b: Int): this(r, g, b, 0xFF)
    constructor(r: Int, g: Int, b: Int, a: Int): this(
        r.and(0xFF).shl(16)
            .or(g.and(0xFF).shl(8))
            .or(b.and(0xFF).shl(0))
            .or(a.and(0xFF).shl(24))
    )

    constructor(r: Float, g: Float, b: Float): this(r.toColorInt(), g.toColorInt(), b.toColorInt())
    constructor(r: Float, g: Float, b: Float, a: Float): this(r.toColorInt(), g.toColorInt(), b.toColorInt(), a.toColorInt())
    constructor(r: Int, g: Int, b: Int, a: Float): this(r, g, b, a.toColorInt())

    val red: Int get() = value.shr(16).and(0xFF)
    val green: Int get() = value.shr(8).and(0xFF)
    val blue: Int get() = value.shr(0).and(0xFF)
    val alpha: Int get() = value.shr(24).and(0xFF)

    val redf: Float get() = red.toColorFloat()
    val greenf: Float get() = green.toColorFloat()
    val bluef: Float get() = blue.toColorFloat()
    val alphaf: Float get() = alpha.toColorFloat()

    fun inverted() = Color(255 - red, 255 - green, 255 - blue, alpha)
    fun darkened() = Color(redf * DARKENING_AMOUNT, greenf * DARKENING_AMOUNT, bluef * DARKENING_AMOUNT, alphaf)
    fun lightened() = inverted().darkened().inverted()

    fun copy(red: Int = this.red, green: Int = this.green, blue: Int = this.blue, alpha: Int = this.alpha) = Color(red, green, blue, alpha)
    fun copyf(red: Float = redf, green: Float = this.greenf, blue: Float = this.bluef, alpha: Float = this.alphaf) = Color(red, green, blue, alpha)

    companion object {
        val Transparent = Color(0, 0, 0, 0)
        val Black = Color(0, 0, 0)
        val DarkGray = Color(0x44, 0x44, 0x44)
        val Gray = Color(0x88, 0x88, 0x88)
        val LightGray = Color(0xCC, 0xCC, 0xCC)
        val White = Color(0xFF, 0xFF, 0xFF)
        val Red = Color(0xFF, 0, 0)
        val Green = Color(0, 0xFF, 0)
        val Blue = Color(0, 0, 0xFF)
        val Yellow = Color(0xFF, 0xFF, 0x00)
        val Cyan = Color(0, 0xFF, 0xFF)
        val Magenta = Color(0xFF, 0, 0xFF)
    }
}