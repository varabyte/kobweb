package nekt.core

import org.jetbrains.compose.web.css.CSSColorValue

data class Palette(
    val primary: CSSColorValue,
    val background: CSSColorValue,
    val accent: CSSColorValue = primary,
)

data class Colors(
    val light: Palette,
    val dark: Palette,
)

data class Theme(
    val colors: Colors
)
