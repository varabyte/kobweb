package com.varabyte.kobweb.silk.theme.colors.palette

import com.varabyte.kobweb.compose.ui.graphics.Color

object SilkWidgetColorGroups {
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

    class MutableButton(palette: MutablePalette) : MutablePalette.ColorGroup(palette, "button"), Button {
        override var default by paletteEntry()
        override var hover by paletteEntry()
        override var focus by paletteEntry()
        override var pressed by paletteEntry()

        fun set(
            default: Color,
            hover: Color,
            focus: Color,
            pressed: Color,
        ) {
            this.default = default
            this.hover = hover
            this.focus = focus
            this.pressed = pressed
        }
    }

    interface Callout {
        val caution: Color
        val important: Color
        val note: Color
        val question: Color
        val quote: Color
        val tip: Color
        val warning: Color
    }

    class MutableCallout(palette: MutablePalette) : MutablePalette.ColorGroup(palette, "callout"), Callout {
        override var caution by paletteEntry()
        override var important by paletteEntry()
        override var note by paletteEntry()
        override var question by paletteEntry()
        override var quote by paletteEntry()
        override var tip by paletteEntry()
        override var warning by paletteEntry()

        fun set(
            caution: Color,
            important: Color,
            note: Color,
            question: Color,
            quote: Color,
            tip: Color,
            warning: Color,
        ) {
            this.caution = caution
            this.important = important
            this.note = note
            this.question = question
            this.quote = quote
            this.tip = tip
            this.warning = warning
        }
    }

    interface Checkbox {
        /** The background color of the checkbox icon. */
        val background: Color

        /** The background color of the checkbox icon, when hovered over. */
        val hover: Color

        /** The foreground color of the checkbox icon. */
        val color: Color
    }

    class MutableCheckbox(palette: MutablePalette) : MutablePalette.ColorGroup(palette, "checkbox"), Checkbox {
        override var background by paletteEntry()
        override var hover by paletteEntry()
        override var color by paletteEntry()

        fun set(
            background: Color,
            hover: Color,
            color: Color,
        ) {
            this.background = background
            this.hover = hover
            this.color = color
        }
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

    class MutableInput(palette: MutablePalette) : MutablePalette.ColorGroup(palette, "input"), Input {
        override var hoveredBorder by paletteEntry()
        override var invalidBorder by paletteEntry()
        override var filled by paletteEntry()
        override var filledHover by paletteEntry()
        override var filledFocus by paletteEntry()

        fun set(
            hoveredBorder: Color,
            invalidBorder: Color,
            filled: Color,
            filledHover: Color,
            filledFocus: Color,
        ) {
            this.hoveredBorder = hoveredBorder
            this.invalidBorder = invalidBorder
            this.filled = filled
            this.filledHover = filledHover
            this.filledFocus = filledFocus
        }
    }

    interface Switch {
        /** The background color of the switch when it is on. */
        val backgroundOn: Color

        /** The background color of the switch when it is off. */
        val backgroundOff: Color

        /** The color of the thumb of the switch. */
        val thumb: Color
    }

    class MutableSwitch(palette: MutablePalette) : MutablePalette.ColorGroup(palette, "switch"), Switch {
        override var backgroundOn by paletteEntry()
        override var backgroundOff by paletteEntry()
        override var thumb by paletteEntry()

        fun set(
            backgroundOn: Color,
            backgroundOff: Color,
            thumb: Color,
        ) {
            this.backgroundOn = backgroundOn
            this.backgroundOff = backgroundOff
            this.thumb = thumb
        }
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

    class MutableTab(palette: MutablePalette) : MutablePalette.ColorGroup(palette, "tab"), Tab {
        override var color by paletteEntry()
        override var background by paletteEntry()
        override var selectedColor by paletteEntry()
        override var selectedBackground by paletteEntry()
        override var selectedBorder by paletteEntry()
        override var hover by paletteEntry()
        override var pressed by paletteEntry()
        override var disabled by paletteEntry()

        fun set(
            color: Color,
            background: Color,
            selectedColor: Color,
            selectedBackground: Color = background,
            selectedBorder: Color = selectedColor,
            hover: Color,
            pressed: Color,
            disabled: Color,
        ) {
            this.color = color
            this.background = background
            this.selectedColor = selectedColor
            this.selectedBackground = selectedBackground
            this.selectedBorder = selectedBorder
            this.hover = hover
            this.pressed = pressed
            this.disabled = disabled
        }
    }

    interface Tooltip {
        val background: Color
        val color: Color
    }

    class MutableTooltip(palette: MutablePalette) : MutablePalette.ColorGroup(palette, "tooltip"), Tooltip {
        override var background by paletteEntry()
        override var color by paletteEntry()

        fun set(
            background: Color,
            color: Color,
        ) {
            this.background = background
            this.color = color
        }
    }
}

private const val BACKGROUND_KEY = "background"
val Palette.background: Color
    get() = this.getValue(BACKGROUND_KEY)

var MutablePalette.background: Color
    get() = this.getValue(BACKGROUND_KEY)
    set(value) { this[BACKGROUND_KEY] = value }

private const val COLOR_KEY = "color"
val Palette.color: Color
    get() = this.getValue(COLOR_KEY)

var MutablePalette.color: Color
    get() = this.getValue(COLOR_KEY)
    set(value) { this[COLOR_KEY] = value }

private const val BORDER_KEY = "border"
val Palette.border: Color
    get() = this.getValue(BORDER_KEY)

var MutablePalette.border: Color
    get() = this.getValue(BORDER_KEY)
    set(value) { this[BORDER_KEY] = value }

private const val FOCUS_OUTLINE_KEY = "focusOutline"
val Palette.focusOutline: Color
    get() = this.getValue(FOCUS_OUTLINE_KEY)

var MutablePalette.focusOutline: Color
    get() = this.getValue(FOCUS_OUTLINE_KEY)
    set(value) { this[FOCUS_OUTLINE_KEY] = value }

private const val OVERLAY_KEY = "overlay"
val Palette.overlay: Color
    get() = this.getValue(OVERLAY_KEY)

var MutablePalette.overlay: Color
    get() = this.getValue(OVERLAY_KEY)
    set(value) { this[OVERLAY_KEY] = value }

private const val PLACEHOLDER_KEY = "placeholder"
val Palette.placeholder: Color
    get() = this.getValue(PLACEHOLDER_KEY)

var MutablePalette.placeholder: Color
    get() = this.getValue(PLACEHOLDER_KEY)
    set(value) { this[PLACEHOLDER_KEY] = value }

val Palette.button: SilkWidgetColorGroups.Button get() = (this as MutablePalette).button
val MutablePalette.button: SilkWidgetColorGroups.MutableButton
    get() = SilkWidgetColorGroups.MutableButton(this)

val Palette.callout: SilkWidgetColorGroups.Callout get() = (this as MutablePalette).callout
val MutablePalette.callout: SilkWidgetColorGroups.MutableCallout
    get() = SilkWidgetColorGroups.MutableCallout(this)

val Palette.checkbox: SilkWidgetColorGroups.Checkbox get() = (this as MutablePalette).checkbox
val MutablePalette.checkbox: SilkWidgetColorGroups.MutableCheckbox
    get() = SilkWidgetColorGroups.MutableCheckbox(this)

val Palette.input: SilkWidgetColorGroups.Input get() = (this as MutablePalette).input
val MutablePalette.input: SilkWidgetColorGroups.MutableInput
    get() = SilkWidgetColorGroups.MutableInput(this)

val Palette.switch: SilkWidgetColorGroups.Switch get() = (this as MutablePalette).switch
val MutablePalette.switch: SilkWidgetColorGroups.MutableSwitch
    get() = SilkWidgetColorGroups.MutableSwitch(this)

val Palette.tab: SilkWidgetColorGroups.Tab get() = (this as MutablePalette).tab
val MutablePalette.tab: SilkWidgetColorGroups.MutableTab
    get() = SilkWidgetColorGroups.MutableTab(this)

val Palette.tooltip: SilkWidgetColorGroups.Tooltip get() = (this as MutablePalette).tooltip
val MutablePalette.tooltip: SilkWidgetColorGroups.MutableTooltip
    get() = SilkWidgetColorGroups.MutableTooltip(this)
