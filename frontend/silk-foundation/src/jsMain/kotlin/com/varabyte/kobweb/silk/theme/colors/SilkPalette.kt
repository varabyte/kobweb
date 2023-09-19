@file:Suppress("DEPRECATION")

package com.varabyte.kobweb.silk.theme.colors

import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.silk.theme.colors.palette.MutablePalette
import com.varabyte.kobweb.silk.theme.colors.palette.MutablePalettes
import kotlin.reflect.KProperty

/**
 * Colors used by all widgets in the Silk library.
 *
 * In order to retheme Silk widgets, you can always override their styles, but changing the palette is perhaps an easier
 * way to affect all Silk widgets globally with minimal effort. Of course, it can make sense to do both -- modify the
 * palette to match your own branding while overriding themes for one-off widget adjustments.
 */
@Deprecated("`SilkPalette` is being replaced with `Palette`, a more general purpose color palette system.")
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

        /** The background color of the checkbox icon, when hovered over. */
        val hover: Color

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

// Dev note: MutableSilkPalette used to be the class that provided configuration support for setting the colors used by
// the silk themes. Now, `MutablePalette` is the class that provides this support in a more extensible, generic way, but
// in order to prevent existing code from failing to compile when this change goes out, we provide a thin wrapper that
// uses the old interface but delegates to the new system instead. This will be removed before v1.0.
class LegacyMutableSilkPalette(private val delegatePalette: MutablePalette) : SilkPalette {
    private inner class PaletteEntry {
        private fun getKey(thisRef: Any, property: KProperty<*>) = "${thisRef::class.simpleName!!.lowercase()}.${property.name}"
        operator fun getValue(thisRef: Any, property: KProperty<*>): Color {
            return delegatePalette.getValue(getKey(thisRef, property))
        }

        operator fun setValue(thisRef: Any, property: KProperty<*>, value: Color) {
            delegatePalette[getKey(thisRef, property)] = value
        }
    }

    inner class Button : SilkPalette.Button {
        override var default: Color by PaletteEntry()
        override var hover: Color by PaletteEntry()
        override var focus: Color by PaletteEntry()
        override var pressed: Color by PaletteEntry()}

    inner class Checkbox : SilkPalette.Checkbox {
        override var background: Color by PaletteEntry()
        override var hover: Color by PaletteEntry()
        override var color: Color by PaletteEntry()}

    inner class Input : SilkPalette.Input {
        override var hoveredBorder: Color by PaletteEntry()
        override var invalidBorder: Color by PaletteEntry()
        override var filled: Color by PaletteEntry()
        override var filledHover: Color by PaletteEntry()
        override var filledFocus: Color by PaletteEntry()
    }

    inner class Link : SilkPalette.Link {
        override var default: Color by PaletteEntry()
        override var visited: Color by PaletteEntry()
    }

    inner class Switch : SilkPalette.Switch {
        override var backgroundOn: Color by PaletteEntry()
        override var backgroundOff: Color by PaletteEntry()
        override var thumb: Color by PaletteEntry()

    }

    inner class Tab : SilkPalette.Tab {
        override var color: Color by PaletteEntry()
        override var background: Color by PaletteEntry()
        override var selectedColor: Color by PaletteEntry()
        override var selectedBackground: Color by PaletteEntry()
        override var selectedBorder: Color by PaletteEntry()
        override var hover: Color by PaletteEntry()
        override var pressed: Color by PaletteEntry()
        override var disabled: Color by PaletteEntry()

    }

    inner class Tooltip : SilkPalette.Tooltip {
        override var background: Color by PaletteEntry()
        override var color: Color by PaletteEntry()
    }


    override var background: Color
        get() = delegatePalette.getValue("background")
        set(value) = delegatePalette.set("background", value)

    override var color: Color
        get() = delegatePalette.getValue("color")
        set(value) = delegatePalette.set("color", value)

    override var border: Color
        get() = delegatePalette.getValue("border")
        set(value) = delegatePalette.set("border", value)

    override var focusOutline: Color
        get() = delegatePalette.getValue("focusOutline")
        set(value) = delegatePalette.set("focusOutline", value)

    override var overlay: Color
        get() = delegatePalette.getValue("overlay")
        set(value) = delegatePalette.set("overlay", value)

    override var placeholder: Color
        get() = delegatePalette.getValue("placeholder")
        set(value) = delegatePalette.set("placeholder", value)

    override val button = Button()
    override val checkbox = Checkbox()
    override val input = Input()
    override val link = Link()
    override val switch = Switch()
    override val tab = Tab()
    override val tooltip = Tooltip()
}

interface SilkPalettes {
    val light: SilkPalette
    val dark: SilkPalette

    operator fun get(colorMode: ColorMode) = when (colorMode) {
        ColorMode.LIGHT -> light
        ColorMode.DARK -> dark
    }
}

class LegacyMutableSilkPalettes(
    palettes: MutablePalettes,
    override val light: SilkPalette = LegacyMutableSilkPalette(palettes.light),
    override val dark: SilkPalette = LegacyMutableSilkPalette(palettes.dark),
) : SilkPalettes
