package com.varabyte.kobweb.silk.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.silk.components.ComponentStyles
import com.varabyte.kobweb.silk.components.SilkComponentStyles
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.ColorSchemes
import com.varabyte.kobweb.silk.theme.colors.Palette
import com.varabyte.kobweb.silk.theme.colors.Palettes

val DEFAULT_PALETTES = Palettes(
    light = Palette(
        background = Color.White,
        surface = Color.White,
        primary = ColorSchemes.White._900,
        secondary = ColorSchemes.Blue._800,
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
        primary = ColorSchemes.Black._900,
        secondary = ColorSchemes.Blue._100,
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

val SilkPalette = compositionLocalOf {
    DEFAULT_PALETTES[SilkConfig.initialColorMode]
}

object SilkTheme {
    val palette: Palette
        @Composable
        @ReadOnlyComposable
        get() = SilkPalette.current

    val componentStyles: ComponentStyles
        @Composable
        @ReadOnlyComposable
        get() = SilkComponentStyles.current
}

@Composable
fun SilkTheme(
    palette: Palette = SilkTheme.palette,
    componentStyles: ComponentStyles = SilkTheme.componentStyles,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        SilkPalette provides palette,
        SilkComponentStyles provides componentStyles,
    ) {
        content()
    }
}
