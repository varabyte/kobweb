package com.varabyte.kobweb.silk.style.breakpoint

import com.varabyte.kobweb.silk.style.StyleScope
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint.LG
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint.MD
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint.SM
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint.XL
import com.varabyte.kobweb.silk.theme.breakpoint.toWidth
import org.jetbrains.compose.web.css.*

/** An interface for expressing CSS media queries through [Breakpoint]s. */
sealed interface BreakpointQueryProvider {
    fun toCSSMediaQuery(): CSSMediaQuery
}

/**
 * Breakpoints are size values which can be used to affect the layout of the UI at various boundary points.
 *
 * The user is ultimately allowed to define what all sizes here mean to their project, but by default Silk is set up so
 * that [SM] means devices larger than mobile, [MD] means devices larger than (small) tablets, and [LG] means devices
 * wider than your standard browser session. (In many cases, it's probably fine to just define base styles and [MD]
 * styles, if you even need to use breakpoints at all).
 *
 * [XL] is provided for ultra-wide scenarios as well as additional flexibility in case the user wants to override what
 * these size means in their own web app.
 *
 * By default, you can think about base styles (without breakpoints) applying to mobile. In other words, if you never
 * add any breakpoints, then the page you get will be styled the same on your phone as well as the widest-screen
 * monitor.
 *
 * Some default breakpoint sizes are defined by Silk, but you can override them by marking a method with `@InitSilk` and
 * updating `ctx.theme.breakpoints` inside it.
 *
 * For more information about breakpoints, see also: https://www.w3schools.com/howto/howto_css_media_query_breakpoints.asp
 */
enum class Breakpoint : BreakpointQueryProvider {
    /**
     * Special value which always means 0-width, useful for querying the current breakpoint of a screen that's smaller
     * than "small" (which usually indicates a mobile device).
     */
    ZERO,

    SM,
    MD,
    LG,
    XL;

    override fun toCSSMediaQuery(): CSSMediaQuery = CSSMediaQuery.MediaFeature("min-width", this.toWidth())

    class Range internal constructor(
        private val lower: Breakpoint,
        private val upper: Breakpoint,
        private val upperExclusive: Boolean,
    ) : BreakpointQueryProvider {
        init {
            require(lower.ordinal != upper.ordinal) { "Breakpoint range should not be called with the same breakpoint twice ($lower)." }
            require(lower.ordinal < upper.ordinal) { "Breakpoint range breakpoints passed in wrong order: $lower should be smaller than $upper" }
        }

        private fun toCSSMediaQuery(lowerInclusive: Breakpoint, upperExclusive: Breakpoint): CSSMediaQuery.Raw {
            // see also: https://www.miragecraft.com/blog/polyfill-for-media-range-syntax
            return CSSMediaQuery.Raw("(min-width: ${lowerInclusive.toWidth()}) and (max-width: ${upperExclusive.toWidth()}) and (not (width: ${upperExclusive.toWidth()}))")
        }

        override fun toCSSMediaQuery(): CSSMediaQuery {
            return if (upperExclusive) {
                toCSSMediaQuery(lower, upper)
            } else {
                if (upper.ordinal < Breakpoint.entries.lastIndex) {
                    toCSSMediaQuery(lower, Breakpoint.entries[upper.ordinal + 1])
                } else {
                    lower.toCSSMediaQuery()
                }
            }
        }
    }

    /**
     * Declare a breakpoint range that applies between a lower-bound breakpoint (inclusive) and an upper-bound
     * breakpoint (inclusive).
     *
     * For example, styles declared with `(SM .. MD) { ... }` will appear for tablets through desktops but not for
     * mobile devices nor wide screens.
     *
     * Declaring a range with the same lower and upper bound is an error; use [until] (`ZERO until SM`) or
     * [rangeUntil] (`ZERO ..< SM`) instead.
     */
    operator fun rangeTo(upper: Breakpoint): Range {
        return Range(this, upper, upperExclusive = false)
    }

    /**
     * Declare a breakpoint range that applies between a lower-bound breakpoint (inclusive) and an upper-bound
     * breakpoint (exclusive).
     *
     * For example, styles declared with `(SM ..< LG) { ... }` will appear for tablets through desktops but not for
     * mobile devices nor wide screens; styles declared with `(ZERO ..< SM) { ... }` will only apply to mobile devices.
     *
     * Note: Within a [StyleScope], `(ZERO ..< SM) { ... }` reads OK, but we would probably recommend
     * `until(SM) { ... }` in this case.
     */
    operator fun rangeUntil(upper: Breakpoint): Range {
        return Range(this, upper, upperExclusive = true)
    }

    /**
     * Convenience method replacing the [rangeUntil] operator syntax with something more readable.
     */
    infix fun until(upper: Breakpoint): Range {
        return this.rangeUntil(upper)
    }
}
