package com.varabyte.kobweb.silk.theme.breakpoint

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.BreakpointUnitValue
import com.varabyte.kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.web.css.*
import org.w3c.dom.Window

private fun Breakpoint.toValue(): BreakpointUnitValue<CSSLengthNumericValue>? {
    return when (this) {
        Breakpoint.ZERO -> null
        Breakpoint.SM -> SilkTheme.breakpoints.sm
        Breakpoint.MD -> SilkTheme.breakpoints.md
        Breakpoint.LG -> SilkTheme.breakpoints.lg
        Breakpoint.XL -> SilkTheme.breakpoints.xl
    }
}

/**
 * Convenience method for fetching the associated `SilkTheme.breakpoints` value for the current [Breakpoint] value.
 */
fun Breakpoint.toWidth(): CSSLengthNumericValue {
    return (this.toValue()?.width ?: 0.px)
}

/**
 * Convenience method for fetching the associated `SilkTheme.breakpoints` value for the current [Breakpoint] value.
 */
fun Breakpoint.toPx(): CSSpxValue {
    return this.toValue()?.toPx() ?: 0.px
}

fun Breakpoint.toMinWidthQuery(): CSSMediaQuery = CSSMediaQuery.MediaFeature("min-width", this.toWidth())

/**
 * Returns the bottom of the breakpoint range that the current window's width is between.
 *
 * For example, all widths between [Breakpoint.SM] and [Breakpoint.MD] will return [Breakpoint.SM].
 */
val Window.breakpointFloor: Breakpoint
    get() {
        return Breakpoint.entries.last { bp -> bp.toPx().value <= innerWidth }
    }
