package com.varabyte.kobweb.silk.init

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.setVariable
import com.varabyte.kobweb.silk.SilkStyleSheet
import com.varabyte.kobweb.silk.components.disclosure.*
import com.varabyte.kobweb.silk.components.document.TocBorderColorVar
import com.varabyte.kobweb.silk.components.document.TocBorderedVariant
import com.varabyte.kobweb.silk.components.document.TocStyle
import com.varabyte.kobweb.silk.components.forms.*
import com.varabyte.kobweb.silk.components.graphics.CanvasStyle
import com.varabyte.kobweb.silk.components.graphics.FitWidthImageVariant
import com.varabyte.kobweb.silk.components.graphics.ImageStyle
import com.varabyte.kobweb.silk.components.layout.*
import com.varabyte.kobweb.silk.components.layout.breakpoint.*
import com.varabyte.kobweb.silk.components.navigation.*
import com.varabyte.kobweb.silk.components.overlay.*
import com.varabyte.kobweb.silk.components.style.common.DisabledStyle
import com.varabyte.kobweb.silk.components.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.components.text.DivTextStyle
import com.varabyte.kobweb.silk.components.text.SpanTextStyle
import com.varabyte.kobweb.silk.theme.*
import com.varabyte.kobweb.silk.theme.colors.BackgroundColorVar
import com.varabyte.kobweb.silk.theme.colors.BorderColorVar
import com.varabyte.kobweb.silk.theme.colors.ColorVar
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
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

    mutableTheme.registerComponentStyle(DisplayIfSmStyle)
    mutableTheme.registerComponentStyle(DisplayIfMdStyle)
    mutableTheme.registerComponentStyle(DisplayIfLgStyle)
    mutableTheme.registerComponentStyle(DisplayIfXlStyle)
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
    val colorMode by rememberColorMode()
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

    setVariable(TabColorVar, palette.tab.color)
    setVariable(TabBackgroundColorVar, palette.tab.background)
    setVariable(TabBorderColorVar, palette.border)
    setVariable(TabDisabledBackgroundColorVar, palette.tab.disabled)
    setVariable(TabHoverBackgroundColorVar, palette.tab.hover)
    setVariable(TabPressedBackgroundColorVar, palette.tab.pressed)

    setVariable(TocBorderColorVar, palette.border)

    setVariable(TooltipBackgroundColorVar, palette.tooltip.background)
    setVariable(TooltipColorVar, palette.tooltip.color)
    // endregion
}