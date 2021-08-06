package kobweb.silk.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import org.jetbrains.compose.common.core.graphics.Color

data class Config(
    var initialColorMode: ColorMode = ColorMode.LIGHT
)

data class Palette(
    val fg: Color,
    val bg: Color,
    val link: Color,
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
        fg = Color.Black,
        bg = Color.White,
        link = Color.Blue,
    ),
    dark = Palette(
        fg = Color.White,
        bg = Color.Black,
        link = Color(0x28, 0x7b, 0xde),
    )
)


val SilkColors = compositionLocalOf { DEFAULT_COLORS }
val SilkConfig = Config()

object SilkTheme {
    val colors: Colors
        @Composable
        get() = SilkColors.current
}

@Composable
fun SilkTheme(colors: Colors = SilkTheme.colors, content: @Composable () -> Unit) {
    CompositionLocalProvider(SilkColors provides colors) {
        content()
    }
}

