package kobweb.silk.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import kobweb.compose.ui.graphics.Color
import kobweb.silk.components.ComponentStyles
import kobweb.silk.components.SilkComponentStyles
import kobweb.silk.theme.colors.ColorMode
import kobweb.silk.theme.colors.Colors
import kobweb.silk.theme.colors.Palette
import kobweb.silk.theme.colors.SilkColors

val DEFAULT_COLORS = Colors(
    light = Palette(
        background = Color.White,
        surface = Color.White,
        primary = SilkColors.White._900,
        secondary = SilkColors.Blue._800,
        warning = Color.Yellow,
        error = Color.Red,
        onBackground = Color.Black,
        onSurface = Color.Black,
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onWarning = Color.Black,
        onError = Color.White,
    ),
    dark = Palette(
        background = Color.Black,
        surface = Color.Black,
        primary = SilkColors.Black._900,
        secondary = SilkColors.Blue._100,
        warning = Color.Yellow,
        error = Color.Red,
        onBackground = Color.White,
        onSurface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onWarning = Color.Black,
        onError = Color.White,
    ),
)

object SilkConfig {
    var initialColorMode: ColorMode = ColorMode.LIGHT
}

val SilkPallete = compositionLocalOf {
    DEFAULT_COLORS.getPalette(SilkConfig.initialColorMode)
}

@Composable
fun SilkTheme(
    palette: Palette = SilkPallete.current,
    componentStyles: ComponentStyles = SilkComponentStyles.current,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        SilkPallete provides palette,
        SilkComponentStyles provides componentStyles,
    ) {
        content()
    }
}
