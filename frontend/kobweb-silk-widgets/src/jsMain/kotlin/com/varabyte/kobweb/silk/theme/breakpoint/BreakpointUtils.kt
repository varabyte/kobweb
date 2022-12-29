package com.varabyte.kobweb.silk.theme.breakpoint

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayBetween
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayIf
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayUntil
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
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
 * [Modifier.displayIf], [Modifier.displayBetween], and [Modifier.displayUntil] should be preferred if possible as those
 * approaches will be easier to debug via your browser tools, does not require adding an event listener behind the
 * scenes, and will avoid an extra recomposition as the screen is resized.
 *
 * ```
 * // Using rememberBreakpoint (acceptable)
 * val bp by rememberBreakpoint()
 * if (bp >= Breakpoint.MD) {
 *   Widget(...)
 * }
 *
 * // Using Modifier.displayIf (preferred)
 * Widget(Modifier.displayIf(Breakpoint.MD))
 * ```
 */
@Composable
fun rememberBreakpoint() = produceState(window.breakpointFloor, key1 = window.location.href) {
    val resizeListener = EventListener { value = window.breakpointFloor }

    window.addEventListener("resize", resizeListener)
    awaitDispose {
        window.removeEventListener("resize", resizeListener)
    }
}
