package com.varabyte.kobweb.silk

import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.graphics.CanvasStyle
import com.varabyte.kobweb.silk.components.graphics.ImageStyle
import com.varabyte.kobweb.silk.components.layout.DividerStyle
import com.varabyte.kobweb.silk.components.layout.SimpleGridStyle
import com.varabyte.kobweb.silk.components.layout.SurfaceStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.*
import com.varabyte.kobweb.silk.components.navigation.LinkStyle
import com.varabyte.kobweb.silk.components.overlay.ModalBackdropStyle
import com.varabyte.kobweb.silk.components.overlay.ModalStyle
import com.varabyte.kobweb.silk.components.overlay.PopupStyle
import com.varabyte.kobweb.silk.components.overlay.TooltipArrowStyle
import com.varabyte.kobweb.silk.components.overlay.TooltipStyle
import com.varabyte.kobweb.silk.components.style.common.DisabledStyle
import com.varabyte.kobweb.silk.components.text.SpanTextStyle
import com.varabyte.kobweb.silk.theme.*

/**
 * Various classes passed to the user in a method annotated by `@InitSilk` which they can use to for initializing Silk
 * values.
 *
 * @param config A handful of settings which will be used for configuring Silk behavior at startup time.
 * @param theme A version of [SilkTheme] that is still mutable (before it has been frozen, essentially, at startup).
 *   Use this if you need to modify site global colors, shapes, typography, and/or styles.
 */
class InitSilkContext(val config: SilkConfig, val theme: MutableSilkTheme)

fun initSilk(additionalInit: (InitSilkContext) -> Unit = {}) {
    val mutableTheme = MutableSilkTheme()

    // TODO: Automate the creation of this list (with a Gradle task?)
    mutableTheme.registerComponentStyle(ButtonStyle)
    mutableTheme.registerComponentStyle(CanvasStyle)
    mutableTheme.registerComponentStyle(DisabledStyle)
    mutableTheme.registerComponentStyle(DividerStyle)
    mutableTheme.registerComponentStyle(ImageStyle)
    mutableTheme.registerComponentStyle(LinkStyle)
    mutableTheme.registerComponentStyle(ModalBackdropStyle)
    mutableTheme.registerComponentStyle(ModalStyle)
    mutableTheme.registerComponentStyle(PopupStyle)
    mutableTheme.registerComponentStyle(SimpleGridStyle)
    mutableTheme.registerComponentStyle(SurfaceStyle)
    mutableTheme.registerComponentStyle(SpanTextStyle)
    mutableTheme.registerComponentStyle(TooltipArrowStyle)
    mutableTheme.registerComponentStyle(TooltipStyle)

    mutableTheme.registerComponentStyle(DisplayIfSmStyle)
    mutableTheme.registerComponentStyle(DisplayIfMdStyle)
    mutableTheme.registerComponentStyle(DisplayIfLgStyle)
    mutableTheme.registerComponentStyle(DisplayIfXlStyle)
    mutableTheme.registerComponentStyle(DisplayUntilSmStyle)
    mutableTheme.registerComponentStyle(DisplayUntilMdStyle)
    mutableTheme.registerComponentStyle(DisplayUntilLgStyle)
    mutableTheme.registerComponentStyle(DisplayUntilXlStyle)

    additionalInit(InitSilkContext(SilkConfigInstance, mutableTheme))

    _SilkTheme = ImmutableSilkTheme(mutableTheme)
    SilkConfigInstance.registerStyles(SilkStyleSheet)
    SilkTheme.registerStyles(SilkStyleSheet)
}