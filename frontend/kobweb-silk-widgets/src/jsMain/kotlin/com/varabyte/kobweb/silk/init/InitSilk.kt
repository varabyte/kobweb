package com.varabyte.kobweb.silk.init

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.SilkStyleSheet
import com.varabyte.kobweb.silk.components.disclosure.TabBackgroundColorVar
import com.varabyte.kobweb.silk.components.disclosure.TabBorderColorVar
import com.varabyte.kobweb.silk.components.disclosure.TabColorVar
import com.varabyte.kobweb.silk.components.disclosure.TabDisabledBackgroundColorVar
import com.varabyte.kobweb.silk.components.disclosure.TabHoverBackgroundColorVar
import com.varabyte.kobweb.silk.components.disclosure.TabPressedBackgroundColorVar
import com.varabyte.kobweb.silk.components.disclosure.TabsPanelStyle
import com.varabyte.kobweb.silk.components.disclosure.TabsStyle
import com.varabyte.kobweb.silk.components.disclosure.TabsTabRowStyle
import com.varabyte.kobweb.silk.components.disclosure.TabsTabStyle
import com.varabyte.kobweb.silk.components.document.TocBorderColorVar
import com.varabyte.kobweb.silk.components.document.TocBorderedVariant
import com.varabyte.kobweb.silk.components.document.TocStyle
import com.varabyte.kobweb.silk.components.forms.ButtonBackgroundDefaultColorVar
import com.varabyte.kobweb.silk.components.forms.ButtonBackgroundFocusColorVar
import com.varabyte.kobweb.silk.components.forms.ButtonBackgroundHoverColorVar
import com.varabyte.kobweb.silk.components.forms.ButtonBackgroundPressedColorVar
import com.varabyte.kobweb.silk.components.forms.ButtonColorVar
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.forms.SwitchStyle
import com.varabyte.kobweb.silk.components.forms.SwitchThumbColorVar
import com.varabyte.kobweb.silk.components.forms.SwitchThumbStyle
import com.varabyte.kobweb.silk.components.forms.SwitchTrackStyle
import com.varabyte.kobweb.silk.components.graphics.CanvasStyle
import com.varabyte.kobweb.silk.components.graphics.FitWidthImageVariant
import com.varabyte.kobweb.silk.components.graphics.ImageStyle
import com.varabyte.kobweb.silk.components.layout.AnimatedColorSurfaceVariant
import com.varabyte.kobweb.silk.components.layout.DividerColorVar
import com.varabyte.kobweb.silk.components.layout.DividerStyle
import com.varabyte.kobweb.silk.components.layout.SimpleGridStyle
import com.varabyte.kobweb.silk.components.layout.SurfaceBackgroundColorVar
import com.varabyte.kobweb.silk.components.layout.SurfaceColorVar
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
import com.varabyte.kobweb.silk.components.navigation.LinkDefaultColorVar
import com.varabyte.kobweb.silk.components.navigation.LinkStyle
import com.varabyte.kobweb.silk.components.navigation.LinkVisitedColorVar
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.overlay.BottomLeftTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.BottomRightTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.BottomTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.LeftBottomTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.LeftTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.LeftTopTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.OverlayBackgroundColorVar
import com.varabyte.kobweb.silk.components.overlay.OverlayStyle
import com.varabyte.kobweb.silk.components.overlay.PopupStyle
import com.varabyte.kobweb.silk.components.overlay.RightBottomTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.RightTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.RightTopTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.TooltipArrowStyle
import com.varabyte.kobweb.silk.components.overlay.TooltipBackgroundColorVar
import com.varabyte.kobweb.silk.components.overlay.TooltipColorVar
import com.varabyte.kobweb.silk.components.overlay.TooltipStyle
import com.varabyte.kobweb.silk.components.overlay.TooltipTextContainerStyle
import com.varabyte.kobweb.silk.components.overlay.TopLeftTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.TopRightTooltipArrowVariant
import com.varabyte.kobweb.silk.components.overlay.TopTooltipArrowVariant
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
import com.varabyte.kobweb.silk.theme.toSilkPalette
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

    // TODO: Automate the creation of this list (with a Gradle task?)
    mutableTheme.registerComponentStyle(ButtonStyle)
    mutableTheme.registerComponentStyle(CanvasStyle)
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

    mutableTheme.registerComponentStyle(TabsStyle)
    mutableTheme.registerComponentStyle(TabsTabRowStyle)
    mutableTheme.registerComponentStyle(TabsTabStyle)
    mutableTheme.registerComponentStyle(TabsPanelStyle)

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

    val config = MutableSilkConfig()
    additionalInit(InitSilkContext(config, SilkStylesheetInstance, mutableTheme))
    MutableSilkConfigInstance = config

    _SilkTheme = ImmutableSilkTheme(mutableTheme)
    SilkStylesheetInstance.registerStylesAndKeyframesInto(SilkStyleSheet)
    SilkTheme.registerStyles(SilkStyleSheet)
}

@Composable
fun HTMLElement.setSilkVariables() {
    setSilkVariables(ColorMode.current)
}

fun HTMLElement.setSilkVariables(colorMode: ColorMode) {
    val palette = colorMode.toSilkPalette()

    // region General color vars
    setVariable(BackgroundColorVar, palette.background)
    setVariable(ColorVar, palette.color)
    setVariable(BorderColorVar, palette.border)
    // endregion

    // region Widget color vars
    setVariable(ButtonBackgroundDefaultColorVar, palette.button.default)
    setVariable(ButtonBackgroundFocusColorVar, palette.button.focus)
    setVariable(ButtonBackgroundHoverColorVar, palette.button.hover)
    setVariable(ButtonBackgroundPressedColorVar, palette.button.pressed)
    setVariable(ButtonColorVar, palette.color)

    setVariable(DividerColorVar, palette.border)

    setVariable(LinkDefaultColorVar, palette.link.default)
    setVariable(LinkVisitedColorVar, palette.link.visited)

    setVariable(OverlayBackgroundColorVar, palette.overlay)

    setVariable(SurfaceBackgroundColorVar, palette.background)
    setVariable(SurfaceColorVar, palette.color)

    setVariable(SwitchThumbColorVar, palette.switch.thumb)

    setVariable(TabColorVar, palette.tab.color)
    setVariable(TabBackgroundColorVar, palette.tab.background)
    setVariable(TabBorderColorVar, palette.tab.border)
    setVariable(TabDisabledBackgroundColorVar, palette.tab.disabled)
    setVariable(TabHoverBackgroundColorVar, palette.tab.hover)
    setVariable(TabPressedBackgroundColorVar, palette.tab.pressed)

    setVariable(TocBorderColorVar, palette.border)

    setVariable(TooltipBackgroundColorVar, palette.tooltip.background)
    setVariable(TooltipColorVar, palette.tooltip.color)
    // endregion
}
