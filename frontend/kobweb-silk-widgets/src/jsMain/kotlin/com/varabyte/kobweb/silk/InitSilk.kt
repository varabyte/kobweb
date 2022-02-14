package com.varabyte.kobweb.silk

import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.graphics.CanvasStyle
import com.varabyte.kobweb.silk.components.graphics.ImageStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.*
import com.varabyte.kobweb.silk.components.layout.SimpleGridStyle
import com.varabyte.kobweb.silk.components.layout.SurfaceStyle
import com.varabyte.kobweb.silk.components.navigation.LinkStyle
import com.varabyte.kobweb.silk.components.text.TextStyle
import com.varabyte.kobweb.silk.theme.ImmutableSilkTheme
import com.varabyte.kobweb.silk.theme.MutableSilkTheme
import com.varabyte.kobweb.silk.theme.SilkConfig
import com.varabyte.kobweb.silk.theme.SilkConfigInstance
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme._SilkTheme

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
    mutableTheme.registerComponentStyle(ButtonStyle)
    mutableTheme.registerComponentStyle(CanvasStyle)
    mutableTheme.registerComponentStyle(ImageStyle)
    mutableTheme.registerComponentStyle(LinkStyle)
    mutableTheme.registerComponentStyle(SimpleGridStyle)
    mutableTheme.registerComponentStyle(SurfaceStyle)
    mutableTheme.registerComponentStyle(TextStyle)

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