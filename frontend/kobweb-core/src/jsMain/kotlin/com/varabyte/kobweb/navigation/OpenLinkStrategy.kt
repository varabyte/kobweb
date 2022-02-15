package com.varabyte.kobweb.navigation

import androidx.compose.web.events.SyntheticMouseEvent
import org.w3c.dom.Window

enum class OpenLinkStrategy {
    IN_PLACE,
    IN_NEW_TAB_BACKGROUND,
    IN_NEW_TAB_FOREGROUND,
    IN_NEW_WINDOW,
}

fun Window.open(href: String, strategy: OpenLinkStrategy) {
    when (strategy) {
        OpenLinkStrategy.IN_PLACE -> open(href, "_self")
        OpenLinkStrategy.IN_NEW_TAB_BACKGROUND -> open(href, "_blank")
        OpenLinkStrategy.IN_NEW_TAB_FOREGROUND -> open(href, "_blank").also { it?.focus() }
        OpenLinkStrategy.IN_NEW_WINDOW -> open(href)
    }
}

fun SyntheticMouseEvent.toOpenLinkStrategy(): OpenLinkStrategy {
    return when {
        ctrlKey && shiftKey -> OpenLinkStrategy.IN_NEW_TAB_FOREGROUND
        ctrlKey -> OpenLinkStrategy.IN_NEW_TAB_BACKGROUND
        shiftKey -> OpenLinkStrategy.IN_NEW_WINDOW
        else -> OpenLinkStrategy.IN_PLACE
    }
}
