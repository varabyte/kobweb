package com.varabyte.kobweb.silk.components.layout.breakpoint

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.breakpoint.toMaxWidthQuery
import org.jetbrains.compose.web.css.DisplayStyle

internal val DisplayIfSmStyle = ComponentStyle("silk-display-if-sm") {
    cssRule(Breakpoint.SM.toMaxWidthQuery()) { Modifier.display(DisplayStyle.None) }
}

internal val DisplayIfMdStyle = ComponentStyle("silk-display-if-md") {
    cssRule(Breakpoint.MD.toMaxWidthQuery()) { Modifier.display(DisplayStyle.None) }
}

internal val DisplayIfLgStyle = ComponentStyle("silk-display-if-lg") {
    cssRule(Breakpoint.LG.toMaxWidthQuery()) { Modifier.display(DisplayStyle.None) }
}

internal val DisplayIfXlStyle = ComponentStyle("silk-display-if-xl") {
    cssRule(Breakpoint.XL.toMaxWidthQuery()) { Modifier.display(DisplayStyle.None) }
}

internal val DisplayUntilSmStyle = ComponentStyle("silk-display-until-sm") {
    Breakpoint.SM { Modifier.display(DisplayStyle.None) }
}

internal val DisplayUntilMdStyle = ComponentStyle("silk-display-until-md") {
    Breakpoint.MD { Modifier.display(DisplayStyle.None) }
}

internal val DisplayUntilLgStyle = ComponentStyle("silk-display-until-lg") {
    Breakpoint.LG { Modifier.display(DisplayStyle.None) }
}

internal val DisplayUntilXlStyle = ComponentStyle("silk-display-until-xl") {
    Breakpoint.XL { Modifier.display(DisplayStyle.None) }
}

fun Modifier.displayIf(breakpoint: Breakpoint) = this.classNames("silk-display-if-${breakpoint.name.lowercase()}")
fun Modifier.displayUntil(breakpoint: Breakpoint) = this.classNames("silk-display-until-${breakpoint.name.lowercase()}")
fun Modifier.displayBetween(breakpointLower: Breakpoint, breakpointUpper: Breakpoint): Modifier {
    require(breakpointLower.ordinal < breakpointUpper.ordinal) { "showBetween breakpoints passed in wrong order: $breakpointLower should be smaller than $breakpointUpper" }

    return this.classNames("silk-display-if-${breakpointLower.name.lowercase()}", "silk-display-until-${breakpointUpper.name.lowercase()}")
}
