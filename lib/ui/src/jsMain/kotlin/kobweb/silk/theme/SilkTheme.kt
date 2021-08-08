package kobweb.silk.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import kobweb.silk.theme.colors.ColorMode
import kobweb.silk.theme.colors.Colors
import kobweb.silk.theme.colors.Palette
import kobweb.silk.theme.shapes.Rect
import kobweb.silk.theme.shapes.Shapes
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.ui.unit.dp

data class Config(
    var initialColorMode: ColorMode = ColorMode.LIGHT
)

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

private val DEFAULT_SHAPES = Shapes(
    small = Rect(8.dp),
    medium = Rect(8.dp),
    large = Rect(),
)

val SilkConfig = Config()
val SilkColors = compositionLocalOf { DEFAULT_COLORS }
val SilkShapes = compositionLocalOf { DEFAULT_SHAPES }

object SilkTheme {
    val colors: Colors
        @Composable
        get() = SilkColors.current

    val shapes: Shapes
        @Composable
        get() = SilkShapes.current
}

@Composable
fun SilkTheme(colors: Colors = SilkTheme.colors, shapes: Shapes = SilkTheme.shapes, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        SilkColors provides colors,
        SilkShapes provides shapes) {
        content()
    }
}

