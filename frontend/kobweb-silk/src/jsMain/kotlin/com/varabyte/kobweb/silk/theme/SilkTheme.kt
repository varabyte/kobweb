package com.varabyte.kobweb.silk.theme

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.silk.components.ComponentStyles
import com.varabyte.kobweb.silk.components.KeyedStyle
import com.varabyte.kobweb.silk.components.MutableComponentStyles
import com.varabyte.kobweb.silk.components.forms.ButtonKey
import com.varabyte.kobweb.silk.components.forms.DefaultButtonStyle
import com.varabyte.kobweb.silk.components.navigation.DefaultLinkStyle
import com.varabyte.kobweb.silk.components.navigation.LinkKey
import com.varabyte.kobweb.silk.components.text.DefaultTextStyle
import com.varabyte.kobweb.silk.components.text.TextKey
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.ColorSchemes
import com.varabyte.kobweb.silk.theme.colors.Palette
import com.varabyte.kobweb.silk.theme.colors.Palettes
import com.varabyte.kobweb.silk.theme.colors.SystemPalettes

/**
 * The default palletes provided by Silk, exposed publicly in case sites want to tweak them using
 * [SystemPalettes.copy] instead of providing their own from scratch.
 */
val SYSTEM_PALLETES = SystemPalettes(
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

internal val SilkPalettes: ProvidableCompositionLocal<Palettes> = compositionLocalOf { SYSTEM_PALLETES }
internal val ComponentStyles: ProvidableCompositionLocal<MutableComponentStyles> = compositionLocalOf {
    MutableComponentStyles(null).apply {
        this[ButtonKey] = DefaultButtonStyle
        this[LinkKey] = DefaultLinkStyle
        this[TextKey] = DefaultTextStyle
    }
}

object SilkTheme {
    val palettes: Palettes
        @Composable
        @ReadOnlyComposable
        get() = SilkPalettes.current

    val palette: Palette
        @Composable
        @ReadOnlyComposable
        get() = palettes.getActivePalette()

    val componentStyles: ComponentStyles
        @Composable
        @ReadOnlyComposable
        get() = ComponentStyles.current
}

@Composable
fun SilkTheme(
    palettes: Palettes = SilkTheme.palettes,
    componentStyles: List<KeyedStyle<*, *>> = emptyList(),
    content: @Composable () -> Unit
) {
    val finalComponentStyles = if (componentStyles.isEmpty()) {
        ComponentStyles.current
    } else {
        MutableComponentStyles(ComponentStyles.current).apply {
            componentStyles.forEach { it.setOn(this) }
        }
    }

    CompositionLocalProvider(
        SilkPalettes provides palettes,
        ComponentStyles provides finalComponentStyles
    ) {
        content()
    }
}