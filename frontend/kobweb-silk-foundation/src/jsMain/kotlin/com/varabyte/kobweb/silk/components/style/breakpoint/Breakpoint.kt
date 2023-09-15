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
 * Some default breakpoint sizes are defined by Silk, but you can override them by marking a method with `@InitSilk` and
 * updating `ctx.theme.breakpoints` inside it.
 *
 * For more information about breakpoints, see also: https://www.w3schools.com/howto/howto_css_media_query_breakpoints.asp
 */
enum class Breakpoint {
    /**
     * Special value which always means 0-width, useful for querying the current breakpoint of a screen that's smaller
     * than "small" (which usually indicates a mobile device).
     */
    ZERO,

    SM,
    MD,
    LG,
    XL;

    // TODO(#168): Remove before v1.0
    // At the time of writing this comment, we recently realized that there was a difference between two similar APIs,
    // rememberPageContext and rememberBreakpoint. The former returned a raw `PageContext` element while the latter
    // returned a `State<Breakpoint>`. We decided that the non-state version was better because there's nothing useful
    // you can do with a state besides unwrap it. We also searched the Android codebase and noticed that most
    // `remember` calls returned raw values, not states, so doing that here would be consistent with their code as well.
    // However, we didn't want to break existing code in the wild, so we added this operator to allow the old syntax to
    // continue to work for now. With this operator in place, the user can choose to write:
    // `val bp = rememberBreakpoint()`
    // OR
    // `val bp by rememberBreakpoint() // Legacy version, delete before 1.0!`
    @Suppress("DeprecatedCallableAddReplaceWith") // Can't suggest a replace-with expression for property delegation syntax
    @Deprecated("You no longer should use the `by` keyword with `rememberBreakpoint`. In other words, change `val bp by rememberBreakpoint()` to `val bp = rememberBreakpoint()`")
    operator fun getValue(thisRef: Any?, property: Any?) = this
}
