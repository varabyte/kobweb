package com.varabyte.kobweb.silk.init

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.SilkStyleSheet
import com.varabyte.kobweb.silk.components.animation.registerKeyframes
import com.varabyte.kobweb.silk.components.disclosure.TabVars
import com.varabyte.kobweb.silk.components.disclosure.TabsPanelStyle
import com.varabyte.kobweb.silk.components.disclosure.TabsStyle
import com.varabyte.kobweb.silk.components.disclosure.TabsTabRowStyle
import com.varabyte.kobweb.silk.components.disclosure.TabsTabStyle
import com.varabyte.kobweb.silk.components.document.TocBorderedVariant
import com.varabyte.kobweb.silk.components.document.TocStyle
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.forms.ButtonVars
import com.varabyte.kobweb.silk.components.forms.CheckboxEnabledAnim
import com.varabyte.kobweb.silk.components.forms.CheckboxIconContainerStyle
import com.varabyte.kobweb.silk.components.forms.CheckboxIconStyle
import com.varabyte.kobweb.silk.components.forms.CheckboxInputVariant
import com.varabyte.kobweb.silk.components.forms.CheckboxStyle
import com.varabyte.kobweb.silk.components.forms.CheckboxVars
import com.varabyte.kobweb.silk.components.forms.CheckedCheckboxIconContainerVariant
import com.varabyte.kobweb.silk.components.forms.FilledInputVariant
import com.varabyte.kobweb.silk.components.forms.FlushedInputVariant
import com.varabyte.kobweb.silk.components.forms.InputGroupStyle
import com.varabyte.kobweb.silk.components.forms.InputStyle
import com.varabyte.kobweb.silk.components.forms.InputVars
import com.varabyte.kobweb.silk.components.forms.OutlinedInputVariant
import com.varabyte.kobweb.silk.components.forms.SwitchInputVariant
import com.varabyte.kobweb.silk.components.forms.SwitchStyle
import com.varabyte.kobweb.silk.components.forms.SwitchThumbStyle
import com.varabyte.kobweb.silk.components.forms.SwitchTrackStyle
import com.varabyte.kobweb.silk.components.forms.SwitchVars
import com.varabyte.kobweb.silk.components.forms.UncheckedCheckboxIconContainerVariant
import com.varabyte.kobweb.silk.components.forms.UnstyledInputVariant
import com.varabyte.kobweb.silk.components.graphics.CanvasStyle
import com.varabyte.kobweb.silk.components.graphics.FitWidthImageVariant
import com.varabyte.kobweb.silk.components.graphics.ImageStyle
import com.varabyte.kobweb.silk.components.layout.AnimatedColorSurfaceVariant
import com.varabyte.kobweb.silk.components.layout.DividerStyle
import com.varabyte.kobweb.silk.components.layout.SimpleGridStyle
import com.varabyte.kobweb.silk.components.layout.SurfaceStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayIfAtLeastLgStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayIfAtLeastMdStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayIfAtLeastSmStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayIfAtLeastXlStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayIfAtLeastZeroStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayUntilLgStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayUntilMdStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayUntilSmStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayUntilXlStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayUntilZeroStyle
import com.varabyte.kobweb.silk.components.navigation.LinkStyle
import com.varabyte.kobweb.silk.components.navigation.LinkVars
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
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
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.common.DisabledStyle
import com.varabyte.kobweb.silk.components.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.components.text.DivTextStyle
import com.varabyte.kobweb.silk.components.text.SpanTextStyle
import com.varabyte.kobweb.silk.theme.ImmutableSilkTheme
import com.varabyte.kobweb.silk.theme.MutableSilkTheme
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme._SilkTheme
import com.varabyte.kobweb.silk.theme.colors.BackgroundColorVar
import com.varabyte.kobweb.silk.theme.colors.BorderColorVar
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.ColorVar
import com.varabyte.kobweb.silk.theme.colors.FocusOutlineColorVar
import com.varabyte.kobweb.silk.theme.colors.PlaceholderColorVar
import com.varabyte.kobweb.silk.theme.colors.suffixedWith
import com.varabyte.kobweb.silk.theme.toSilkPalette
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import org.w3c.dom.HTMLElement

/**
 * Various classes passed to the user in a method annotated by `@InitSilk` which they can use to for initializing Silk
 * values.
 *
 * @param config A handful of settings which will be used for configuring Silk behavior at startup time.
 * @param stylesheet A handful of methods for registering styles, etc., against Silk's provided stylesheet.
 * @param theme A version of [SilkTheme] that is still mutable (before it has been frozen, essentially, at startup).
 *   Use this if you need to modify site global colors, shapes, typography, and/or styles.
 */
class InitSilkContext(val config: MutableSilkConfig, val stylesheet: SilkStylesheet, val theme: MutableSilkTheme)

fun initSilk(additionalInit: (InitSilkContext) -> Unit = {}) {
    val mutableTheme = MutableSilkTheme()

    mutableTheme.registerComponentStyle(SilkColorsStyle)

    // TODO: Automate the creation of this list (with a Gradle task?)
    mutableTheme.registerComponentStyle(ButtonStyle)
    mutableTheme.registerComponentStyle(CanvasStyle)
    mutableTheme.registerComponentStyle(CheckboxStyle)
    mutableTheme.registerComponentVariants(CheckboxInputVariant)
    mutableTheme.registerComponentStyle(CheckboxIconContainerStyle)
    mutableTheme.registerComponentStyle(CheckboxIconStyle)
    mutableTheme.registerComponentVariants(CheckedCheckboxIconContainerVariant, UncheckedCheckboxIconContainerVariant)
    mutableTheme.registerComponentStyle(DisabledStyle)
    mutableTheme.registerComponentStyle(DividerStyle)
    mutableTheme.registerComponentStyle(DivTextStyle)
    mutableTheme.registerComponentStyle(ImageStyle)
    mutableTheme.registerComponentVariants(FitWidthImageVariant)
    mutableTheme.registerComponentStyle(LinkStyle)
    mutableTheme.registerComponentVariants(UncoloredLinkVariant, UndecoratedLinkVariant)
    mutableTheme.registerComponentStyle(OverlayStyle)
    mutableTheme.registerComponentStyle(PopupStyle)
    mutableTheme.registerComponentStyle(SimpleGridStyle)
    mutableTheme.registerComponentStyle(SmoothColorStyle)
    mutableTheme.registerComponentStyle(SurfaceStyle)
    @Suppress("DEPRECATION") mutableTheme.registerComponentVariants(AnimatedColorSurfaceVariant)
    mutableTheme.registerComponentStyle(SpanTextStyle)

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

    mutableTheme.registerComponentStyle(TocStyle)
    mutableTheme.registerComponentVariants(TocBorderedVariant)
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

    mutableTheme.registerComponentStyle(DisplayIfAtLeastZeroStyle)
    mutableTheme.registerComponentStyle(DisplayIfAtLeastSmStyle)
    mutableTheme.registerComponentStyle(DisplayIfAtLeastMdStyle)
    mutableTheme.registerComponentStyle(DisplayIfAtLeastLgStyle)
    mutableTheme.registerComponentStyle(DisplayIfAtLeastXlStyle)
    mutableTheme.registerComponentStyle(DisplayUntilZeroStyle)
    mutableTheme.registerComponentStyle(DisplayUntilSmStyle)
    mutableTheme.registerComponentStyle(DisplayUntilMdStyle)
    mutableTheme.registerComponentStyle(DisplayUntilLgStyle)
    mutableTheme.registerComponentStyle(DisplayUntilXlStyle)

    SilkStylesheetInstance.registerKeyframes(CheckboxEnabledAnim)

    val config = MutableSilkConfig()
    additionalInit(InitSilkContext(config, SilkStylesheetInstance, mutableTheme))
    MutableSilkConfigInstance = config

    _SilkTheme = ImmutableSilkTheme(mutableTheme)
    SilkStylesheetInstance.registerStylesAndKeyframesInto(SilkStyleSheet)
    SilkTheme.registerStyles(SilkStyleSheet)
}

private val SilkColorsStyle by ComponentStyle.base {
    val palette = colorMode.toSilkPalette()
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

        .setVariable(LinkVars.DefaultColor, palette.link.default)
        .setVariable(LinkVars.VisitedColor, palette.link.visited)

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
fun HTMLElement.setSilkVariables() {
    setSilkVariables(ColorMode.current)
}

fun HTMLElement.setSilkVariables(colorMode: ColorMode) {
    removeClass(SilkColorsStyle.name.suffixedWith(colorMode.opposite))
    addClass(SilkColorsStyle.name.suffixedWith(colorMode))
}
