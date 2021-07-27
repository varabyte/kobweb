package nekt.ui.config

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.Color

data class Config(
    var initialColorMode: ColorMode = ColorMode.LIGHT
)

data class Palette(
    val fg: CSSColorValue,
    val bg: CSSColorValue,
    val link: CSSColorValue,
)

data class Colors(
    val light: Palette,
    val dark: Palette,
) {
    @Composable
    fun getActivePalette(): Palette {
        return when (getColorMode()) {
            ColorMode.LIGHT -> light
            ColorMode.DARK -> dark
        }
    }
}

private val DEFAULT_COLORS = Colors(
    light = Palette(
        fg = Color.black,
        bg = Color.white,
        link = Color("#0000ff"),
    ),
    dark = Palette(
        fg = Color.white,
        bg = Color.black,
        link = Color("#287bde"),
    )
)

object Theme {
    val config: Config = Config()
    var colors: Colors = DEFAULT_COLORS
        internal set
}

@Composable
fun Theme(colors: Colors = Theme.colors, content: @Composable () -> Unit) {
    val prevColors = Theme.colors
    Theme.colors = colors

    content()

    Theme.colors = prevColors
}

