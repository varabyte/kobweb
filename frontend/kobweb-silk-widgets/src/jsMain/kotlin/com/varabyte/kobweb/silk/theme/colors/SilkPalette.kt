package com.varabyte.kobweb.silk.theme.colors

import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.graphics.lightened
import org.jetbrains.compose.web.css.CSSColorValue

// Misc. general color vars not used directly by Silk but provided for users for their own widgets if they want to use
// them.

val BackgroundColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val ColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val BorderColorVar by StyleVariable<CSSColorValue>(prefix = "silk")

/**
 * Colors used by all widgets in the Silk library.
 *
 * In order to retheme Silk widgets, you can always override their styles, but changing the palette is perhaps an easier
 * way to affect all Silk widgets globally with minimal effort. Of course, it can make sense to do both -- modify the
 * palette to match your own branding while overriding themes for one-off widget adjustments.
 */
interface SilkPalette {
    /** Color used for the background of HTML elements on this page. */
    val background: Color
    /** Color used for the foreground (e.g. text) of HTML elements on this page. */
    val color: Color
    val link: Link
    val button: Button
    /** Color used for Silk borders, i.e. lines drawn around or between elements. */
    val border: Color
    val overlay: Color
    val tooltip: Tooltip

    interface Link {
        /** Color used for links that the user has never clicked on before. */
        val default: Color
        /** Color used for links that have been visited before. */
        val visited: Color
    }

    interface Button {
        /** Color used for buttons in a normal state. */
        val default: Color
        /** Color used for buttons when the mouse is over the button (but not clicked). */
        val hover: Color
        /** Color used for the outline of buttons that have been focused on (e.g. by keyboard nav). */
        val focus: Color
        /** Color used for buttons when they are being depressed by the user. */
        val pressed: Color
    }

    interface Tooltip {
        val background: Color
        val color: Color
    }
}

class MutableSilkPalette(
    override var background: Color,
    override var color: Color,
    override var link: Link,
    override var button: Button,
    override var border: Color = color,
    // Intentionally invert backdrop from normal background
    override var overlay: Color = color.toRgb().copyf(alpha = 0.5f),
    override var tooltip: Tooltip = Tooltip(
        // Intentionally inversed from main colors, for contrast.
        background = color,
        color = background,
    ),
) : SilkPalette {
    class Link(
        override var default: Color,
        override var visited: Color,
    ) : SilkPalette.Link

    class Button(
        override var default: Color,
        override var hover: Color,
        override var focus: Color,
        override var pressed: Color,
    ) : SilkPalette.Button

    class Tooltip(
        override var background: Color,
        override var color: Color,
    ) : SilkPalette.Tooltip
}

interface SilkPalettes {
    val light: SilkPalette
    val dark: SilkPalette

    operator fun get(colorMode: ColorMode) = when (colorMode) {
        ColorMode.LIGHT -> light
        ColorMode.DARK -> dark
    }
}

class MutableSilkPalettes(
    override val light: MutableSilkPalette = run {
        val buttonBase = Colors.White.darkened(byPercent = 0.2f)
        MutableSilkPalette(
            background = Colors.White,
            color = Colors.Black,
            link = MutableSilkPalette.Link(
                default = Colors.Blue,
                visited = Colors.Purple,
            ),
            button = MutableSilkPalette.Button(
                default = buttonBase,
                hover = buttonBase.darkened(byPercent = 0.2f),
                focus = Colors.CornflowerBlue,
                pressed = buttonBase.darkened(byPercent = 0.4f)
            )
        )
    },
    override val dark: MutableSilkPalette = run {
        val buttonBase = Colors.Black.lightened(byPercent = 0.2f)
        MutableSilkPalette(
            background = Colors.Black,
            color = Colors.White,
            link = MutableSilkPalette.Link(
                default = Colors.Cyan,
                visited = Colors.Violet,
            ),
            button = MutableSilkPalette.Button(
                default = buttonBase,
                hover = buttonBase.lightened(byPercent = 0.2f),
                focus = Colors.LightSkyBlue,
                pressed = buttonBase.lightened(byPercent = 0.4f)
            )
        )
    }
) : SilkPalettes
