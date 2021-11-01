package com.varabyte.kobweb.silk.theme

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.silk.components.ComponentKey
import com.varabyte.kobweb.silk.components.ComponentModifier
import com.varabyte.kobweb.silk.components.ComponentModifiers
import com.varabyte.kobweb.silk.components.MutableComponentModifiers
import com.varabyte.kobweb.silk.components.NoOpComponentModifier
import com.varabyte.kobweb.silk.components.forms.ButtonKey
import com.varabyte.kobweb.silk.components.forms.DefaultButtonModifier
import com.varabyte.kobweb.silk.components.graphics.CanvasKey
import com.varabyte.kobweb.silk.components.layout.DefaultSurfaceModifier
import com.varabyte.kobweb.silk.components.layout.SurfaceKey
import com.varabyte.kobweb.silk.components.navigation.DefaultLinkModifier
import com.varabyte.kobweb.silk.components.navigation.LinkKey
import com.varabyte.kobweb.silk.components.text.DefaultTextModifier
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
internal val ComponentModifiers: ProvidableCompositionLocal<MutableComponentModifiers> = compositionLocalOf {
    MutableComponentModifiers(null).apply {
        this[ButtonKey] = DefaultButtonModifier
        this[CanvasKey] = NoOpComponentModifier
        this[LinkKey] = DefaultLinkModifier
        this[SurfaceKey] = DefaultSurfaceModifier
        this[TextKey] = DefaultTextModifier
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

    val componentModifiers: ComponentModifiers
        @Composable
        @ReadOnlyComposable
        get() = ComponentModifiers.current
}

@Composable
fun SilkTheme(
    palettes: Palettes = SilkTheme.palettes,
    componentModifiers: List<Pair<ComponentKey, ComponentModifier>> = emptyList(),
    content: @Composable () -> Unit
) {
    val finalModifiers = if (componentModifiers.isEmpty()) {
        ComponentModifiers.current
    } else {
        MutableComponentModifiers(ComponentModifiers.current).apply {
            componentModifiers.forEach { pair ->
                this[pair.first] = pair.second
            }
        }
    }

    CompositionLocalProvider(
        SilkPalettes provides palettes,
        ComponentModifiers provides finalModifiers
    ) {
        content()
    }
}