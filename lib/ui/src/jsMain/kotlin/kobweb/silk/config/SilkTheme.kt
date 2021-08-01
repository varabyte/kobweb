package kobweb.silk.config

import androidx.compose.runtime.Composable
import kobweb.silk.css.withTransitionDefaults
import org.jetbrains.compose.web.css.*

private class ThemeStyleSheet(colorMode: ColorMode) : StyleSheet() {
    init {
        val palette = SilkTheme.colors.getPalette(colorMode)

        "#root" {
            backgroundColor(palette.bg)
            color(palette.fg)
            withTransitionDefaults("background-color", "color")
        }
        "a" style {
            color(palette.link)
            withTransitionDefaults("color")
        }
    }
}

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
    fun getPalette(colorMode: ColorMode): Palette {
        return when (colorMode) {
            ColorMode.LIGHT -> light
            ColorMode.DARK -> dark
        }
    }

    @Composable
    fun getActivePalette(): Palette = getPalette(getColorMode())
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

object SilkTheme {
    val config: Config = Config()
    var colors: Colors = DEFAULT_COLORS
        internal set
}

@Composable
fun SilkTheme(colors: Colors = SilkTheme.colors, content: @Composable () -> Unit) {
    val prevColors = SilkTheme.colors
    SilkTheme.colors = colors

    Style(ThemeStyleSheet(getColorMode()))
    content()

    SilkTheme.colors = prevColors
}

