package kobweb.silk.theme.colors

import androidx.compose.runtime.Composable
import org.jetbrains.compose.common.core.graphics.Color

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
