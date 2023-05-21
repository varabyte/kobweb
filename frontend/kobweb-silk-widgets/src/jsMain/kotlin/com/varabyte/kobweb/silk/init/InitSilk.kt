package com.varabyte.kobweb.silk.init

import com.varabyte.kobweb.silk.SilkStyleSheet
import com.varabyte.kobweb.silk.components.document.TocBorderedVariant
import com.varabyte.kobweb.silk.components.document.TocStyle
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.graphics.CanvasStyle
import com.varabyte.kobweb.silk.components.graphics.FitWidthImageVariant
import com.varabyte.kobweb.silk.components.graphics.ImageStyle
import com.varabyte.kobweb.silk.components.layout.AnimatedColorSurfaceVariant
import com.varabyte.kobweb.silk.components.layout.DividerStyle
import com.varabyte.kobweb.silk.components.layout.SimpleGridStyle
import com.varabyte.kobweb.silk.components.layout.SurfaceStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.*
import com.varabyte.kobweb.silk.components.navigation.LinkStyle
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.overlay.*
import com.varabyte.kobweb.silk.components.style.common.DisabledStyle
import com.varabyte.kobweb.silk.components.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.components.text.DivTextStyle
import com.varabyte.kobweb.silk.components.text.SpanTextStyle
import com.varabyte.kobweb.silk.theme.*

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