package nekt.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.Color

data class Palette(
    val primary: CSSColorValue,
    val background: CSSColorValue,
    val accent: CSSColorValue = primary,
)

data class Colors(
    val light: Palette,
    val dark: Palette,
)

private val DEFAULT_COLORS = Colors(
    light = Palette(
        primary = Color.black,
        background = Color.white,
    ),
    dark = Palette(
        primary = Color.white,
        background = Color.black,
    )
)

object Theme {
    internal var _colors = DEFAULT_COLORS
    val colors: Colors get() = _colors
}

@Composable
fun Theme(colors: Colors = Theme.colors, content: @Composable () -> Unit) {
    val prevColors = Theme.colors
    Theme._colors = colors

    content()

    Theme._colors = prevColors
}

