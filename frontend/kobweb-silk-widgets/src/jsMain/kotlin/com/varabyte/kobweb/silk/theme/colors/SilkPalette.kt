package com.varabyte.kobweb.silk.theme.colors

import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.graphics.lightened

data class SilkPalette(
    val background: Color,
    val color: Color,
    val link: Link,
    val button: Button,
) {
    data class Link(
        val default: Color,
        val visited: Color,
    )

    data class Button(
        val default: Color,
        val hover: Color,
        val pressed: Color,
    )
}

data class SilkPalettes(
    val light: SilkPalette,
    val dark: SilkPalette,
) {
    operator fun get(colorMode: ColorMode) = when (colorMode) {
        ColorMode.LIGHT -> light
        ColorMode.DARK -> dark
    }
}

val LightSilkPalette = run {
    val buttonBase = ColorSchemes.White._900
    SilkPalette(
        background = Colors.White,
        color = Colors.Black,
        link = SilkPalette.Link(
            default = Colors.Blue,
            visited = Colors.Purple,
        ),
        button = SilkPalette.Button(
            default = buttonBase,
            hover = buttonBase.darkened(),
            pressed = buttonBase.darkened().darkened()
        )
    )
}

val DarkSilkPalette = run {
    val buttonBase = ColorSchemes.Black._900
    SilkPalette(
        background = Colors.Black,
        color = Colors.White,
        link = SilkPalette.Link(
            default = Colors.Blue.lightened(),
            visited = Colors.Purple.lightened(),
        ),
        button = SilkPalette.Button(
            default = buttonBase,
            hover = buttonBase.lightened(),
            pressed = buttonBase.lightened().lightened()
        )
    )
}