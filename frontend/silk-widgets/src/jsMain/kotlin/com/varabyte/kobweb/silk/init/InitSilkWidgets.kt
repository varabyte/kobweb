package com.varabyte.kobweb.silk.init

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.graphics.lightened
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.animation.registerKeyframes
import com.varabyte.kobweb.silk.components.disclosure.TabVars
import com.varabyte.kobweb.silk.components.disclosure.TabsPanelStyle
import com.varabyte.kobweb.silk.components.disclosure.TabsStyle
import com.varabyte.kobweb.silk.components.disclosure.TabsTabRowStyle
import com.varabyte.kobweb.silk.components.disclosure.TabsTabStyle
import com.varabyte.kobweb.silk.components.forms.ButtonSize
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.forms.ButtonVars
import com.varabyte.kobweb.silk.components.forms.CheckboxEnabledAnim
import com.varabyte.kobweb.silk.components.forms.CheckboxIconContainerStyle
import com.varabyte.kobweb.silk.components.forms.CheckboxIconStyle
import com.varabyte.kobweb.silk.components.forms.CheckboxInputVariant
import com.varabyte.kobweb.silk.components.forms.CheckboxSize
import com.varabyte.kobweb.silk.components.forms.CheckboxStyle
import com.varabyte.kobweb.silk.components.forms.CheckboxVars
import com.varabyte.kobweb.silk.components.forms.CheckedCheckboxIconContainerVariant
import com.varabyte.kobweb.silk.components.forms.FilledInputVariant
import com.varabyte.kobweb.silk.components.forms.FlushedInputVariant
import com.varabyte.kobweb.silk.components.forms.InputGroupStyle
import com.varabyte.kobweb.silk.components.forms.InputSize
import com.varabyte.kobweb.silk.components.forms.InputStyle
import com.varabyte.kobweb.silk.components.forms.InputVars
import com.varabyte.kobweb.silk.components.forms.OutlinedInputVariant
import com.varabyte.kobweb.silk.components.forms.SwitchInputVariant
import com.varabyte.kobweb.silk.components.forms.SwitchSize
import com.varabyte.kobweb.silk.components.forms.SwitchStyle
import com.varabyte.kobweb.silk.components.forms.SwitchThumbStyle
import com.varabyte.kobweb.silk.components.forms.SwitchTrackStyle
import com.varabyte.kobweb.silk.components.forms.SwitchVars
import com.varabyte.kobweb.silk.components.forms.UncheckedCheckboxIconContainerVariant
import com.varabyte.kobweb.silk.components.forms.UnstyledInputVariant
import com.varabyte.kobweb.silk.components.graphics.CanvasStyle
import com.varabyte.kobweb.silk.components.layout.HorizontalDividerStyle
import com.varabyte.kobweb.silk.components.layout.SimpleGridStyle
import com.varabyte.kobweb.silk.components.layout.SurfaceStyle
import com.varabyte.kobweb.silk.components.layout.VerticalDividerStyle
import com.varabyte.kobweb.silk.components.overlay.BottomLeftTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.BottomRightTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.BottomTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.LeftBottomTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.LeftTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.LeftTopTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.OverlayStyle
import com.varabyte.kobweb.silk.components.overlay.OverlayVars
import com.varabyte.kobweb.silk.components.overlay.PopupStyle
import com.varabyte.kobweb.silk.components.overlay.RightBottomTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.RightTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.RightTopTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.TooltipArrowStyle
import com.varabyte.kobweb.silk.components.overlay.TooltipStyle
import com.varabyte.kobweb.silk.components.overlay.TooltipTextContainerStyle
import com.varabyte.kobweb.silk.components.overlay.TooltipVars
import com.varabyte.kobweb.silk.components.overlay.TopLeftTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.TopRightTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.TopTooltipArrowVariant
import com.varabyte.kobweb.silk.components.style.CssStyle
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.common.DisabledStyle
import com.varabyte.kobweb.silk.components.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.components.style.vars.color.BackgroundColorVar
import com.varabyte.kobweb.silk.components.style.vars.color.BorderColorVar
import com.varabyte.kobweb.silk.components.style.vars.color.ColorVar
import com.varabyte.kobweb.silk.components.style.vars.color.FocusOutlineColorVar
import com.varabyte.kobweb.silk.components.style.vars.color.PlaceholderColorVar
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.ColorSchemes
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.border
import com.varabyte.kobweb.silk.theme.colors.palette.button
import com.varabyte.kobweb.silk.theme.colors.palette.checkbox
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.focusOutline
import com.varabyte.kobweb.silk.theme.colors.palette.input
import com.varabyte.kobweb.silk.theme.colors.palette.overlay
import com.varabyte.kobweb.silk.theme.colors.palette.placeholder
import com.varabyte.kobweb.silk.theme.colors.palette.switch
import com.varabyte.kobweb.silk.theme.colors.palette.tab
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import com.varabyte.kobweb.silk.theme.colors.palette.tooltip
import org.w3c.dom.Document
import org.w3c.dom.HTMLElement

fun initSilkWidgets(ctx: InitSilkContext) {
    val mutableTheme = ctx.theme

    ctx.theme.palettes.apply {
        val focusOutline = ColorSchemes.Blue._500.toRgb().copyf(alpha = 0.5f)
        val placeholder = ColorSchemes.Gray._500

        run { // init light palette
            val color = Colors.Black
            light.background = Colors.White
            light.color = color
            light.border = color.copyf(alpha = 0.2f)
            light.focusOutline = focusOutline
            light.overlay = color.copyf(alpha = 0.5f)
            light.placeholder = placeholder

            val buttonBase = Colors.White.darkened(byPercent = 0.2f)
            light.button.set(
                default = buttonBase,
                hover = buttonBase.darkened(byPercent = 0.2f),
                focus = Colors.CornflowerBlue,
                pressed = buttonBase.darkened(byPercent = 0.4f),
            )

            light.checkbox.set(
                background = ColorSchemes.Blue._500,
                hover = ColorSchemes.Blue._600,
                color = Colors.White,
            )

            val inputFilled = ColorSchemes.Gray._200
            light.input.set(
                filled = inputFilled,
                filledFocus = Colors.Transparent,
                hoveredBorder = ColorSchemes.Gray._500,
                invalidBorder = ColorSchemes.Red._900,
                filledHover = inputFilled.darkened(0.1f),
            )

            light.switch.set(
                thumb = Colors.White,
                backgroundOn = Colors.DodgerBlue,
                backgroundOff = Colors.LightGray,
            )

            light.tab.set(
                color = Colors.Black,
                background = Colors.White,
                selectedColor = Colors.CornflowerBlue,
                hover = Colors.LightGray,
                pressed = Colors.WhiteSmoke,
                disabled = Colors.White,
            )

            light.tooltip.set(
                // Intentionally inverted from main colors, for contrast
                background = light.color,
                color = light.background,
            )
        }

        run { // init dark palette
            val color = Colors.White
            dark.background = Colors.Black
            dark.color = color
            dark.border = color.copyf(alpha = 0.2f)
            dark.focusOutline = focusOutline
            dark.overlay = color.copyf(alpha = 0.5f)
            dark.placeholder = placeholder

            val buttonBase = Colors.Black.lightened(byPercent = 0.2f)
            dark.button.set(
                default = buttonBase,
                hover = buttonBase.lightened(byPercent = 0.2f),
                focus = Colors.LightSkyBlue,
                pressed = buttonBase.lightened(byPercent = 0.4f),
            )

            dark.checkbox.set(
                background = ColorSchemes.Blue._200,
                hover = ColorSchemes.Blue._300,
                color = Colors.Black,
            )

            val inputFilled = ColorSchemes.Gray._900
            dark.input.set(
                filled = inputFilled,
                filledFocus = Colors.Transparent,
                hoveredBorder = ColorSchemes.Gray._600,
                invalidBorder = ColorSchemes.Red._300,
                filledHover = inputFilled.lightened(0.1f),
            )

            dark.switch.set(
                thumb = Colors.White,
                backgroundOn = Colors.LightSkyBlue,
                backgroundOff = Colors.DarkGray,
            )

            dark.tab.set(
                color = Colors.White,
                background = Colors.Black,
                selectedColor = Colors.LightSkyBlue,
                hover = Colors.DarkSlateGray,
                pressed = Colors.DarkGray,
                disabled = Colors.Black,
            )

            dark.tooltip.set(
                // Intentionally inverted from main colors, for contrast
                background = dark.color,
                color = dark.background,
            )
        }
    }

    mutableTheme.registerCssStyle(null, SilkColorsStyle)

    // TODO: Automate the creation of this list (with a Gradle task?)
    mutableTheme.registerComponentStyle(DisabledStyle)
    mutableTheme.registerComponentStyle(SmoothColorStyle)

    mutableTheme.registerComponentStyle(ButtonStyle)
    mutableTheme.registerComponentStyle(CanvasStyle)
    mutableTheme.registerComponentStyle(CheckboxStyle)
    mutableTheme.registerComponentVariants(CheckboxInputVariant)
    mutableTheme.registerComponentStyle(CheckboxIconContainerStyle)
    mutableTheme.registerComponentStyle(CheckboxIconStyle)
    mutableTheme.registerComponentVariants(CheckedCheckboxIconContainerVariant, UncheckedCheckboxIconContainerVariant)
    mutableTheme.registerComponentStyle(OverlayStyle)
    mutableTheme.registerComponentStyle(PopupStyle)
    mutableTheme.registerComponentStyle(SimpleGridStyle)
    mutableTheme.registerComponentStyle(SurfaceStyle)

    mutableTheme.registerComponentStyle(HorizontalDividerStyle)
    mutableTheme.registerComponentStyle(VerticalDividerStyle)

    mutableTheme.registerComponentStyle(SwitchStyle)
    mutableTheme.registerComponentStyle(SwitchTrackStyle)
    mutableTheme.registerComponentStyle(SwitchThumbStyle)
    mutableTheme.registerComponentVariants(SwitchInputVariant)

    mutableTheme.registerComponentStyle(TabsStyle)
    mutableTheme.registerComponentStyle(TabsTabRowStyle)
    mutableTheme.registerComponentStyle(TabsTabStyle)
    mutableTheme.registerComponentStyle(TabsPanelStyle)

    mutableTheme.registerComponentStyle(InputStyle)
    mutableTheme.registerComponentVariants(
        OutlinedInputVariant,
        FilledInputVariant,
        FlushedInputVariant,
        UnstyledInputVariant
    )
    mutableTheme.registerComponentStyle(InputGroupStyle)

    mutableTheme.registerComponentStyle(TooltipArrowStyle)
    mutableTheme.registerComponentVariants(
        TopLeftTooltipArrowVariant,
        TopTooltipArrowVariant,
        TopRightTooltipArrowVariant,
        LeftTopTooltipArrowVariant,
        LeftTooltipArrowVariant,
        LeftBottomTooltipArrowVariant,
        RightTopTooltipArrowVariant,
        RightTooltipArrowVariant,
        RightBottomTooltipArrowVariant,
        BottomLeftTooltipArrowVariant,
        BottomTooltipArrowVariant,
        BottomRightTooltipArrowVariant
    )
    mutableTheme.registerComponentStyle(TooltipStyle)
    mutableTheme.registerComponentStyle(TooltipTextContainerStyle)

    ctx.stylesheet.registerKeyframes(CheckboxEnabledAnim)

    // TODO (double): we definitely want to automate this
    //  Note: B sure to add @CssPrefix to the relevant objects (or account for the prefix some other way)
    mutableTheme.registerCssStyle("silk-button-size_xs", ButtonSize.XS)
    mutableTheme.registerCssStyle("silk-button-size_sm", ButtonSize.SM)
    mutableTheme.registerCssStyle("silk-button-size_md", ButtonSize.MD)
    mutableTheme.registerCssStyle("silk-button-size_lg", ButtonSize.LG)
    mutableTheme.registerCssStyle("silk-checkbox-size_sm", CheckboxSize.SM)
    mutableTheme.registerCssStyle("silk-checkbox-size_md", CheckboxSize.MD)
    mutableTheme.registerCssStyle("silk-checkbox-size_lg", CheckboxSize.LG)
    mutableTheme.registerCssStyle("silk-input-size_xs", InputSize.XS)
    mutableTheme.registerCssStyle("silk-input-size_sm", InputSize.SM)
    mutableTheme.registerCssStyle("silk-input-size_md", InputSize.MD)
    mutableTheme.registerCssStyle("silk-input-size_lg", InputSize.LG)
    mutableTheme.registerCssStyle("silk-switch-size_sm", SwitchSize.SM)
    mutableTheme.registerCssStyle("silk-switch-size_md", SwitchSize.MD)
    mutableTheme.registerCssStyle("silk-switch-size_lg", SwitchSize.LG)
}

val SilkColorsStyle by CssStyle.base {
    val palette = colorMode.toPalette()
    Modifier
        // region General color vars
        .setVariable(BackgroundColorVar, palette.background)
        .setVariable(ColorVar, palette.color)
        .setVariable(BorderColorVar, palette.border)
        .setVariable(FocusOutlineColorVar, palette.focusOutline)
        .setVariable(PlaceholderColorVar, palette.placeholder)
        // endregion

        // region Widget color vars
        .setVariable(ButtonVars.BackgroundDefaultColor, palette.button.default)
        .setVariable(ButtonVars.BackgroundHoverColor, palette.button.hover)
        .setVariable(ButtonVars.BackgroundPressedColor, palette.button.pressed)

        .setVariable(CheckboxVars.IconBackgroundColor, palette.checkbox.background)
        .setVariable(CheckboxVars.IconBackgroundHoverColor, palette.checkbox.hover)
        .setVariable(CheckboxVars.IconColor, palette.checkbox.color)

        .setVariable(InputVars.BorderHoverColor, palette.input.hoveredBorder)
        .setVariable(InputVars.BorderInvalidColor, palette.input.invalidBorder)
        .setVariable(InputVars.FilledColor, palette.input.filled)
        .setVariable(InputVars.FilledHoverColor, palette.input.filledHover)
        .setVariable(InputVars.FilledFocusColor, palette.input.filledFocus)

        .setVariable(OverlayVars.BackgroundColor, palette.overlay)

        .setVariable(SwitchVars.ThumbColor, palette.switch.thumb)

        .setVariable(TabVars.Color, palette.tab.color)
        .setVariable(TabVars.BackgroundColor, palette.tab.background)
        .setVariable(TabVars.DisabledBackgroundColor, palette.tab.disabled)
        .setVariable(TabVars.HoverBackgroundColor, palette.tab.hover)
        .setVariable(TabVars.PressedBackgroundColor, palette.tab.pressed)

        .setVariable(TooltipVars.BackgroundColor, palette.tooltip.background)
        .setVariable(TooltipVars.Color, palette.tooltip.color)
    // endregion
}

@Composable
fun Document.setSilkWidgetVariables() {
    val root = remember { this.getElementById("root") as HTMLElement }
    root.setSilkWidgetVariables()
}

@Composable
fun HTMLElement.setSilkWidgetVariables() {
    setSilkWidgetVariables(ColorMode.current)
}

fun HTMLElement.setSilkWidgetVariables(colorMode: ColorMode) {
    // TODO: IMPORTANT figure out how this should work
//    removeClass(SilkColorsStyle.name.suffixedWith(colorMode.opposite))
//    addClass(SilkColorsStyle.name.suffixedWith(colorMode))
}
