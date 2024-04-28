package com.varabyte.kobweb.silk.theme.breakpoint

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayBetween
import com.varabyte.kobweb.silk.style.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.style.breakpoint.displayUntil
import kotlinx.browser.window
import org.w3c.dom.events.EventListener

/**
 * A function you can call within a page to fetch the current breakpoint size.
 *
 * If the breakpoint changes, e.g. due to a screen resize that passes a breakpoint boundary, this will cause a
 * recomposition on the current page.
 *
 * Note that this method returns the *floor* of the current breakpoint range. That is, if your size is between
 * [Breakpoint.SM] and [Breakpoint.MD], this will return [Breakpoint.SM], and if your size is smaller than
 * [Breakpoint.SM], this will return [Breakpoint.ZERO].
 *
 * This method can be useful to branch logic based on screen width, although it should be noted that
 * [Modifier.displayIfAtLeast], [Modifier.displayBetween], and [Modifier.displayUntil] should be preferred if possible as those
 * approaches will be easier to debug via your browser tools, do not require adding an event listener behind the
 * scenes, and will avoid an extra recomposition as the screen is resized.
 *
 * ```
 * // Using rememberBreakpoint (acceptable)
 * val bp = rememberBreakpoint()
 * if (bp >= Breakpoint.MD) {
 *   Widget(...)
 * }
 *
 * // Using Modifier.displayIfAtLeast (preferred)
 * Widget(Modifier.displayIfAtLeast(Breakpoint.MD))
 * ```
 */
@Composable
fun rememberBreakpoint() = produceState(window.breakpointFloor) {
    val resizeListener = EventListener { value = window.breakpointFloor }

    window.addEventListener("resize", resizeListener)
    awaitDispose {
        window.removeEventListener("resize", resizeListener)
    }
}.value
