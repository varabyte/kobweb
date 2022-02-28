package com.varabyte.kobweb.silk.components.style.breakpoint

import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint.LG
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint.MD
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint.SM
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint.XL

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
 * Some default breakpoint sizes are defined by Silk but you can override them by marking a method with `@InitSilk` and
 * updating the `ctx.config.breakpoints` map inside it.
 *
 * For more information about breakpoints, see also: https://www.w3schools.com/howto/howto_css_media_query_breakpoints.asp
 */
enum class Breakpoint {
    /**
     * Special value which always means 0-width, useful for querying the current breakpoint of a screen that's smaller
     * than "small" (which usually indicates a mobile device)
     */
    ZERO,

    SM,
    MD,
    LG,
    XL;
}
