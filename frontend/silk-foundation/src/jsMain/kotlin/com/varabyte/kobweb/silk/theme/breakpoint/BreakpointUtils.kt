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
 * IMPORTANT: You should ideally NOT use this method in any branching logic that affects a first page render. The main
 * way you should be styling your responsive page is by using CSS style breakpoints or by showing / hiding elements
 * using [Modifier.displayIfAtLeast], [Modifier.displayBetween], and [Modifier.displayUntil].
 *
 * Otherwise, when you export your site, this breakpoint value will be fetched and a desktop render will occur; but
 * then, if a user opens your site on a mobile device, they'll get a flicker of the desktop layout before the mobile
 * version is rendered. Also, even if the user is visiting the site on a desktop, the additional recomposition can
 * still result in a worse performance score as evaluated by Google Lighthouse.
 *
 * The other approaches (i.e. CSS style breakpoints and the conditional display modifiers) will also make it easier to
 * debug via your browser tools, do not require adding an event listener behind the scenes, and will avoid extra
 * recompositions as the screen is resized.
 *
 * ```
 * // Using rememberBreakpoint (acceptable in some cases)
 * val bp = rememberBreakpoint()
 * if (bp >= Breakpoint.MD) {
 *   Widget(...)
 * }
 *
 * // Using Modifier.displayIfAtLeast (preferred #1)
 * Widget(Modifier.displayIfAtLeast(Breakpoint.MD))
 *
 * // Using CSS style breakpoints (preferred #2)
 * val WidgetStyle = CssStyle {
 *     base { Modifier.display(DisplayStyle.None) }
 *     Breakpoint.MD { Modifier.display(DisplayStyle.Block) }
 * }
 *
 * Widget(WidgetStyle.toModifier())
 * ```
 *
 * An example case where using `rememberBreakpoint` is fine would be using it for an individual component that loads
 * dynamically and is not a core part of the initial layout, like a popup or a modal.
 */
@Composable
fun rememberBreakpoint() = produceState(window.breakpointFloor) {
    val resizeListener = EventListener { value = window.breakpointFloor }

    window.addEventListener("resize", resizeListener)
    awaitDispose {
        window.removeEventListener("resize", resizeListener)
    }
}.value
