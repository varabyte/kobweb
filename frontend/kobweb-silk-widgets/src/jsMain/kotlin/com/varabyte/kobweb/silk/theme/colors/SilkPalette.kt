package com.varabyte.kobweb.silk.theme.colors

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.graphics.lightened
import org.jetbrains.compose.web.css.*

// Misc. general color vars not used directly by Silk but provided for users for their own widgets if they want to use
// them.

val BackgroundColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val ColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val BorderColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val FocusOutlineColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val PlaceholderOpacityVar by StyleVariable<Number>(1.0)
val PlaceholderColorVar by StyleVariable<CSSColorValue>(prefix = "silk")

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

    /** Color used for Silk borders, i.e. lines drawn around or between elements. */
    val border: Color

    /** Color used for the highlight that appears around focused elements. */
    val focusOutline: Color

    val overlay: Color

    /** Color used for placeholder text in widgets that have them. */
    val placeholder: Color

    val button: Button
    val checkbox: Checkbox
    val input: Input
    val link: Link
    val switch: Switch
    val tab: Tab
    val tooltip: Tooltip

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

    interface Checkbox {
        /** The background color of the checkbox icon. */
        val background: Color

        /** The foreground color of the checkbox icon. */
        val color: Color
    }

    interface Link {
        /** Color used for links that the user has never clicked on before. */
        val default: Color

        /** Color used for links that have been visited before. */
        val visited: Color
    }

    interface Input {
        /** Color used for the outline of inputs when hovered over */
        val hoveredBorder: Color

        /** Color used for the outline of inputs when `valid` is set to false */
        val invalidBorder: Color

        /** Color used for the background of inputs using the filled background variant */
        val filled: Color
        /** The filled variant's background when hovered over. */
        val filledHover: Color
        /** The filled variant's background when focused. */
        val filledFocus: Color
    }

    interface Switch {
        val backgroundOn: Color
        val backgroundOff: Color
        val thumb: Color
    }

    interface Tab {
        val color: Color
        val background: Color
        val selectedColor: Color
        val selectedBackground: Color
        val selectedBorder: Color
        val hover: Color
        val pressed: Color
        val disabled: Color
    }

    interface Tooltip {
        val background: Color
        val color: Color
    }
}

class MutableSilkPalette(
    override var background: Color,
    override var color: Color,
    override var button: Button,
    override var checkbox: Checkbox,
    override var input: Input,
    override var link: Link,
    override var switch: Switch,
    override var tab: Tab,
    override var border: Color = color.toRgb().copyf(alpha = 0.2f),
    override val focusOutline: Color = ColorSchemes.Blue._500.toRgb().copyf(alpha = 0.5f),
    // Intentionally invert backdrop from normal background
    override var overlay: Color = color.toRgb().copyf(alpha = 0.5f),
    override var placeholder: Color = ColorSchemes.Gray._500,
    override var tooltip: Tooltip = Tooltip(
        // Intentionally inverted from main colors, for contrast.
        background = color,
        color = background,
    ),
) : SilkPalette {
    class Button(
        override var default: Color,
        override var hover: Color,
        override var focus: Color,
        override var pressed: Color,
    ) : SilkPalette.Button

    class Checkbox(
        override var background: Color,
        override var color: Color,
    ) : SilkPalette.Checkbox

    class Input(
        override val hoveredBorder: Color,
        override var invalidBorder: Color,
        override var filled: Color,
        override var filledHover: Color,
        override var filledFocus: Color = Colors.Transparent,
    ) : SilkPalette.Input {
        constructor(
            colorMode: ColorMode,
            filled: Color,
        ) : this(
            filled = filled,
            hoveredBorder = if (colorMode.isDark) ColorSchemes.Gray._600 else ColorSchemes.Gray._500,
            invalidBorder = if (colorMode.isDark) ColorSchemes.Red._300 else ColorSchemes.Red._900,
            filledHover = filled.shifted(colorMode, 0.1f)
        )
    }

    class Link(
        override var default: Color,
        override var visited: Color,
    ) : SilkPalette.Link

    class Switch(
        override var backgroundOn: Color,
        override var backgroundOff: Color,
        override var thumb: Color = Colors.White,
    ) : SilkPalette.Switch

    class Tab(
        override var color: Color,
        override var background: Color,
        override var selectedColor: Color,
        override var hover: Color,
        override var pressed: Color,
        override var disabled: Color,
        override var selectedBackground: Color = background,
        override var selectedBorder: Color = selectedColor,
    ) : SilkPalette.Tab

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
            button = MutableSilkPalette.Button(
                default = buttonBase,
                hover = buttonBase.darkened(byPercent = 0.2f),
                focus = Colors.CornflowerBlue,
                pressed = buttonBase.darkened(byPercent = 0.4f)
            ),
            checkbox = MutableSilkPalette.Checkbox(
                background = ColorSchemes.Blue._500,
                color = Colors.White,
            ),
            input = MutableSilkPalette.Input(
                ColorMode.LIGHT,
                filled = ColorSchemes.Gray._200
            ),
            link = MutableSilkPalette.Link(
                default = Colors.Blue,
                visited = Colors.Purple,
            ),
            switch = MutableSilkPalette.Switch(
                backgroundOn = Colors.DodgerBlue,
                backgroundOff = Colors.LightGray,
            ),
            tab = MutableSilkPalette.Tab(
                color = Colors.Black,
                background = Colors.White,
                selectedColor = Colors.CornflowerBlue,
                hover = Colors.LightGray,
                pressed = Colors.WhiteSmoke,
                disabled = Colors.White,
            ),
        )
    },
    override val dark: MutableSilkPalette = run {
        val buttonBase = Colors.Black.lightened(byPercent = 0.2f)
        MutableSilkPalette(
            background = Colors.Black,
            color = Colors.White,
            button = MutableSilkPalette.Button(
                default = buttonBase,
                hover = buttonBase.lightened(byPercent = 0.2f),
                focus = Colors.LightSkyBlue,
                pressed = buttonBase.lightened(byPercent = 0.4f)
            ),
            checkbox = MutableSilkPalette.Checkbox(
                background = ColorSchemes.Blue._200,
                color = Colors.Black,
            ),
            input = MutableSilkPalette.Input(ColorMode.DARK, filled = ColorSchemes.Gray._900),
            link = MutableSilkPalette.Link(
                default = Colors.Cyan,
                visited = Colors.Violet,
            ),
            switch = MutableSilkPalette.Switch(
                backgroundOn = Colors.LightSkyBlue,
                backgroundOff = Colors.DarkGray,
            ),
            tab = MutableSilkPalette.Tab(
                color = Colors.White,
                background = Colors.Black,
                selectedColor = Colors.LightSkyBlue,
                hover = Colors.DarkSlateGray,
                pressed = Colors.DarkGray,
                disabled = Colors.Black,
            ),
        )
    }
) : SilkPalettes
