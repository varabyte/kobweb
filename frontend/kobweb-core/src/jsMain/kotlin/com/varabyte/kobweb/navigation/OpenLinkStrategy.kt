package com.varabyte.kobweb.navigation

import androidx.compose.web.events.SyntheticMouseEvent
import org.w3c.dom.Window

enum class OpenLinkStrategy {
    IN_PLACE,
    IN_NEW_TAB,
    @Deprecated(
        "Use IN_NEW_TAB instead. It turns out you cannot specify IN_NEW_TAB_BACKGROUND as a developer. Only users can control this behavior by pressing CTRL when clicking on a link.",
        ReplaceWith("IN_NEW_TAB")
    )
    IN_NEW_TAB_BACKGROUND,
    @Deprecated(
        "Use IN_NEW_TAB instead. It turns out you cannot specify IN_NEW_TAB_FOREGROUND as a developer. Only users can control this behavior by pressing CTRL+SHIFT when clicking on a link.",
        ReplaceWith("IN_NEW_TAB")
    )
    IN_NEW_TAB_FOREGROUND,
    @Deprecated(
        "Use IN_NEW_TAB or IN_PLACE instead. It turns out you cannot specify IN_NEW_WINDOW as a developer. Only users can control this behavior by pressing SHIFT when clicking on a link.",
        ReplaceWith("IN_NEW_TAB")
    )
    IN_NEW_WINDOW,
}

@Suppress("DEPRECATION") // Deprecated, sure, but we still need to handle the enums!
fun Window.open(href: String, strategy: OpenLinkStrategy) {
    when (strategy) {
        OpenLinkStrategy.IN_PLACE -> open(href, "_self")
        OpenLinkStrategy.IN_NEW_TAB -> open(href, "_blank")
        OpenLinkStrategy.IN_NEW_TAB_BACKGROUND -> open(href, "_blank")
        OpenLinkStrategy.IN_NEW_TAB_FOREGROUND -> open(href, "_blank").also { it?.focus() }
        OpenLinkStrategy.IN_NEW_WINDOW -> open(href)
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
