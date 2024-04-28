package com.varabyte.kobweb.silk.style.breakpoint

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.theme.breakpoint.toMinWidthQuery
import org.jetbrains.compose.web.css.*


private fun CSSMediaQuery.invert(): CSSMediaQuery {
    // Note: We invert a min-width query instead of using a max-width query because otherwise there's a width where
    // both media queries overlap. It seems like you have to include the "not all" for some technical reason; otherwise,
    // this would just be "Not(this)"
    // See also: https://stackoverflow.com/a/13611538
    return CSSMediaQuery.Raw("not all and $this")
}

// Technically unnecessary -- this just means always display. But provided for completion for all breakpoint values.
internal val DisplayIfAtLeastZeroStyle = CssStyle {
    cssRule(Breakpoint.ZERO.toMinWidthQuery().invert()) { Modifier.display(DisplayStyle.None) }
}

internal val DisplayIfAtLeastSmStyle = CssStyle {
    cssRule(Breakpoint.SM.toMinWidthQuery().invert()) { Modifier.display(DisplayStyle.None) }
}

internal val DisplayIfAtLeastMdStyle = CssStyle {
    cssRule(Breakpoint.MD.toMinWidthQuery().invert()) { Modifier.display(DisplayStyle.None) }
}

internal val DisplayIfAtLeastLgStyle = CssStyle {
    cssRule(Breakpoint.LG.toMinWidthQuery().invert()) { Modifier.display(DisplayStyle.None) }
}

internal val DisplayIfAtLeastXlStyle = CssStyle {
    cssRule(Breakpoint.XL.toMinWidthQuery().invert()) { Modifier.display(DisplayStyle.None) }
}

// Technically unnecessary -- this just means never display. But provided for completion for all breakpoint values.
internal val DisplayUntilZeroStyle = CssStyle {
    Breakpoint.ZERO { Modifier.display(DisplayStyle.None) }
}

internal val DisplayUntilSmStyle = CssStyle {
    Breakpoint.SM { Modifier.display(DisplayStyle.None) }
}

internal val DisplayUntilMdStyle = CssStyle {
    Breakpoint.MD { Modifier.display(DisplayStyle.None) }
}

internal val DisplayUntilLgStyle = CssStyle {
    Breakpoint.LG { Modifier.display(DisplayStyle.None) }
}

internal val DisplayUntilXlStyle = CssStyle {
    Breakpoint.XL { Modifier.display(DisplayStyle.None) }
}

fun Modifier.displayIfAtLeast(breakpoint: Breakpoint) =
    this.classNames("silk-display-if-at-least-${breakpoint.name.lowercase()}")

fun Modifier.displayUntil(breakpoint: Breakpoint) = this.classNames("silk-display-until-${breakpoint.name.lowercase()}")
fun Modifier.displayBetween(breakpointLower: Breakpoint, breakpointUpper: Breakpoint): Modifier {
    require(breakpointLower.ordinal < breakpointUpper.ordinal) { "displayBetween breakpoints passed in wrong order: $breakpointLower should be smaller than $breakpointUpper" }

    return this.classNames(
        "silk-display-if-${breakpointLower.name.lowercase()}",
        "silk-display-until-${breakpointUpper.name.lowercase()}"
    )
}
