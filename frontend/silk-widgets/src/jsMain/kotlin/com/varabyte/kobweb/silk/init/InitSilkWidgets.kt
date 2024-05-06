package com.varabyte.kobweb.silk.init

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.graphics.lightened
import com.varabyte.kobweb.compose.ui.modifiers.*
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
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.common.DisabledStyle
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.vars.color.BackgroundColorVar
import com.varabyte.kobweb.silk.style.vars.color.BorderColorVar
import com.varabyte.kobweb.silk.style.vars.color.ColorVar
import com.varabyte.kobweb.silk.style.vars.color.FocusOutlineColorVar
import com.varabyte.kobweb.silk.style.vars.color.PlaceholderColorVar
import com.varabyte.kobweb.silk.theme.SilkTheme
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
import com.varabyte.kobweb.silk.theme.colors.suffixedWith
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
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

    mutableTheme.registerStyle("silk-colors", SilkColorsStyle, FRAMEWORK_LAYER_NAME)

    // Register InputStyle early as it's wrapped by other components
    mutableTheme.registerStyle("silk-input", InputStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-outlined", OutlinedInputVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-filled", FilledInputVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-flushed", FlushedInputVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-unstyled", UnstyledInputVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-input-group", InputGroupStyle, FRAMEWORK_LAYER_NAME)

    // TODO: Automate the creation of this list (by refactoring KSP processor into something we can use?)
    mutableTheme.registerStyle("silk-disabled", DisabledStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-smooth-color", SmoothColorStyle, FRAMEWORK_LAYER_NAME)

    mutableTheme.registerStyle("silk-button", ButtonStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-canvas", CanvasStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-checkbox", CheckboxStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-checkbox", CheckboxInputVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-checkbox-icon-container", CheckboxIconContainerStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-checkbox-icon", CheckboxIconStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-checked", CheckedCheckboxIconContainerVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-unchecked", UncheckedCheckboxIconContainerVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-overlay", OverlayStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-popup", PopupStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-simple-grid", SimpleGridStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-surface", SurfaceStyle, FRAMEWORK_LAYER_NAME)

    mutableTheme.registerStyle("silk-horizontal-divider", HorizontalDividerStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-vertical-divider", VerticalDividerStyle, FRAMEWORK_LAYER_NAME)

    mutableTheme.registerStyle("silk-switch", SwitchStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-switch-track", SwitchTrackStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-switch-thumb", SwitchThumbStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-switch", SwitchInputVariant, FRAMEWORK_LAYER_NAME)

    mutableTheme.registerStyle("silk-tabs", TabsStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-tabs-tab-row", TabsTabRowStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-tabs-tab", TabsTabStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-tabs-panel", TabsPanelStyle, FRAMEWORK_LAYER_NAME)

    mutableTheme.registerStyle("silk-tooltip-arrow", TooltipArrowStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-top-left", TopLeftTooltipArrowVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-top", TopTooltipArrowVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-top-right", TopRightTooltipArrowVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-left-top", LeftTopTooltipArrowVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-left", LeftTooltipArrowVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-left-bottom", LeftBottomTooltipArrowVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-right-top", RightTopTooltipArrowVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-right", RightTooltipArrowVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-right-bottom", RightBottomTooltipArrowVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-bottom-left", BottomLeftTooltipArrowVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-bottom", BottomTooltipArrowVariant, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerVariant("-bottom-right", BottomRightTooltipArrowVariant, FRAMEWORK_LAYER_NAME)

    mutableTheme.registerStyle("silk-tooltip", TooltipStyle, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-tooltip-text", TooltipTextContainerStyle, FRAMEWORK_LAYER_NAME)

    mutableTheme.registerKeyframes("silk-checkbox-enabled", CheckboxEnabledAnim, FRAMEWORK_LAYER_NAME)

    // TODO (double): we definitely want to automate this
    //  Note: Be sure to add @CssPrefix to the relevant objects (or account for the prefix some other way)
    mutableTheme.registerStyle("silk-button-size_xs", ButtonSize.XS, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-button-size_sm", ButtonSize.SM, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-button-size_md", ButtonSize.MD, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-button-size_lg", ButtonSize.LG, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-checkbox-size_sm", CheckboxSize.SM, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-checkbox-size_md", CheckboxSize.MD, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-checkbox-size_lg", CheckboxSize.LG, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-input-size_xs", InputSize.XS, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-input-size_sm", InputSize.SM, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-input-size_md", InputSize.MD, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-input-size_lg", InputSize.LG, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-switch-size_sm", SwitchSize.SM, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-switch-size_md", SwitchSize.MD, FRAMEWORK_LAYER_NAME)
    mutableTheme.registerStyle("silk-switch-size_lg", SwitchSize.LG, FRAMEWORK_LAYER_NAME)
}

val SilkColorsStyle = CssStyle.base {
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
    SilkTheme.nameFor(SilkColorsStyle).let { silkColorsStyleName ->
        removeClass(silkColorsStyleName.suffixedWith(colorMode.opposite))
        addClass(silkColorsStyleName.suffixedWith(colorMode))
    }
}
