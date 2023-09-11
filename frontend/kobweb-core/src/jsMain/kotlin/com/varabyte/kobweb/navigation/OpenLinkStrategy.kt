package com.varabyte.kobweb.navigation

import androidx.compose.web.events.SyntheticMouseEvent
import org.w3c.dom.Window

enum class OpenLinkStrategy {
    IN_PLACE,
    IN_NEW_TAB,
}

fun Window.open(href: String, strategy: OpenLinkStrategy) {
    when (strategy) {
        OpenLinkStrategy.IN_PLACE -> open(href, "_self")
        OpenLinkStrategy.IN_NEW_TAB -> open(href, "_blank")
    }
}

/**
 * Convert a mouse event plus control key state to an [OpenLinkStrategy].
 *
 * @param default A default value may be provided to control the desired fallback behavior if no control keys are
 *   pressed. Defaults to in place.
 */
fun SyntheticMouseEvent.toOpenLinkStrategy(default: OpenLinkStrategy = OpenLinkStrategy.IN_PLACE): OpenLinkStrategy {
    return when {
        ctrlKey || shiftKey -> OpenLinkStrategy.IN_NEW_TAB
        else -> default
    }
}
