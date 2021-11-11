package com.varabyte.kobweb.silk.theme

import androidx.compose.runtime.*
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentStyleBuilder
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.DarkSilkPalette
import com.varabyte.kobweb.silk.theme.colors.LightSilkPalette
import com.varabyte.kobweb.silk.theme.colors.SilkPalette
import com.varabyte.kobweb.silk.theme.colors.SilkPalettes
import com.varabyte.kobweb.silk.theme.colors.getColorMode
import org.jetbrains.compose.web.css.StyleSheet

object SilkConfig {
    var initialColorMode: ColorMode = ColorMode.LIGHT
}

class MutableSilkTheme {
    internal val componentStyles = LinkedHashMap<String, ComponentStyleBuilder>() // Preserve insertion order

    var palettes = SilkPalettes(LightSilkPalette, DarkSilkPalette)

    fun registerComponentStyle(style: ComponentStyleBuilder) {
        componentStyles[style.name] = style
        style.variants.forEach { variant ->
            componentStyles[variant.style.name] = variant.style
        }
    }
}

class ImmutableSilkTheme(private val mutableSilkTheme: MutableSilkTheme) {
    val palettes = mutableSilkTheme.palettes

    val palette: SilkPalette
        @Composable
        @ReadOnlyComposable
        get() = palettes[getColorMode()]

    private val _componentStyles = mutableMapOf<String, ComponentStyle>()
    val componentStyles: Map<String, ComponentStyle> = _componentStyles

    internal fun registerStyles(componentStyleSheet: StyleSheet) {
        check(::SilkTheme.isInitialized) // Call only after SilkTheme is set
        mutableSilkTheme.componentStyles.values.forEach { styleBuilder ->
            styleBuilder.addStyles(componentStyleSheet)
            _componentStyles[styleBuilder.name] = ComponentStyle(styleBuilder.name)
        }
    }
}

lateinit var SilkTheme: ImmutableSilkTheme
    internal set