package com.varabyte.kobweb.silk.theme.colors

import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.graphics.lightened

/**
 * Colors used by all widgets in the Silk library.
 *
 * In order to retheme Silk widgets, you can always override their styles, but changing the palette is perhaps an easier
 * way to affect all Silk widgets globally with minimal effort. Of course, it can make sense to do both -- modify the
 * palette to match your own branding while overriding themes for one-off widget adjustments.
 *
 * @param background Color used for the background of HTML elements on this page
 * @param color Color used for the foreground (e.g. text) of HTML elements on this page
 * @param link Colors related to Silk links. See also: [Link]
 * @param button Colors related to Silk buttons. See also: [Button]
 * @param border Color used for Silk borders, i.e. lines drawn around or between elements
 */
data class SilkPalette(
    val background: Color,
    val color: Color,
    val link: Link,
    val button: Button,
    val border: Color = color,
    val modal: Modal = Modal(
        backdrop = color.toRgb().copyf(alpha = 0.5f),
        background = background
    ),
    val tooltip: Tooltip = Tooltip(
        // Intentionally inversed from main colors, for contrast.
        background = color,
        color = background,
    )
) {
    /**
     * Silk link related colors.
     *
     * @param default Color used for links that the user has never clicked on before.
     * @param visited Color used for links that have been visited before.
     */
    data class Link(
        val default: Color,
        val visited: Color,
    )

    /**
     * Silk button related colors.
     *
     * @param default Color used for buttons in a normal state
     * @param hover Color used for buttons when the mouse is over the button (but not clicked)
     * @param pressed Color used for buttons when they are being depressed by the user.
     */
    data class Button(
        val default: Color,
        val hover: Color,
        val pressed: Color,
    )

    /**
     * Silk modal dialog related colors.
     *
     * @param backdrop The color of the fullscreen shade behind the modal popup, useful for visually
     *   de-emphasizing background content.
     * @param background The color of the dialog that more tightly wraps modal content.
     */
    data class Modal(
        val backdrop: Color,
        val background: Color,
    )

    /**
     * Silk tooltip related colors.
     */
    data class Tooltip(
        val background: Color,
        val color: Color,
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
    val buttonBase = Colors.White.darkened(byPercent = 0.2f)
    SilkPalette(
        background = Colors.White,
        color = Colors.Black,
        link = SilkPalette.Link(
            default = Colors.Blue,
            visited = Colors.Purple,
        ),
        button = SilkPalette.Button(
            default = buttonBase,
            hover = buttonBase.darkened(byPercent = 0.2f),
            pressed = buttonBase.darkened(byPercent = 0.4f)
        )
    )
}

val DarkSilkPalette = run {
    val buttonBase = Colors.Black.lightened(byPercent = 0.2f)
    SilkPalette(
        background = Colors.Black,
        color = Colors.White,
        link = SilkPalette.Link(
            default = Colors.Cyan,
            visited = Colors.Violet,
        ),
        button = SilkPalette.Button(
            default = buttonBase,
            hover = buttonBase.lightened(byPercent = 0.2f),
            pressed = buttonBase.lightened(byPercent = 0.4f)
        )
    )
}