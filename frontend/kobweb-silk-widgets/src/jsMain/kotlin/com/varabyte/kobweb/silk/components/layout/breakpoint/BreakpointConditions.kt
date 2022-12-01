package com.varabyte.kobweb.silk.components.layout.breakpoint

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.breakpoint.toMinWidthQuery
import org.jetbrains.compose.web.css.*


private fun CSSMediaQuery.invert(): CSSMediaQuery {
    // Note: We invert a min-width query instead of using a max-width query because otherwise there's a width where
    // both media queries overlap. It seems like you have to include the "not all" for some technical reason; otherwise,
    // this would just be "Not(this)"
    // See also: https://stackoverflow.com/a/13611538
    return CSSMediaQuery.Raw("not all and $this")
}

internal val DisplayIfSmStyle = ComponentStyle("silk-display-if-sm") {
    cssRule(Breakpoint.SM.toMinWidthQuery().invert()) { Modifier.display(DisplayStyle.None) }
}

internal val DisplayIfMdStyle = ComponentStyle("silk-display-if-md") {
    cssRule(Breakpoint.MD.toMinWidthQuery().invert()) { Modifier.display(DisplayStyle.None) }
}

internal val DisplayIfLgStyle = ComponentStyle("silk-display-if-lg") {
    cssRule(Breakpoint.LG.toMinWidthQuery().invert()) { Modifier.display(DisplayStyle.None) }
}

internal val DisplayIfXlStyle = ComponentStyle("silk-display-if-xl") {
    cssRule(Breakpoint.XL.toMinWidthQuery().invert()) { Modifier.display(DisplayStyle.None) }
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
