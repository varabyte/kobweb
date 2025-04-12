package com.varabyte.kobweb.silk

import androidx.compose.runtime.*
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.cssClass
import kotlinx.browser.document
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import org.w3c.dom.HTMLElement

/**
 * Set the CSS class representing the active color mode on [element]. This will enable color mode aware styles for all
 * descendants of [element] and set the necessary CSS variables for Silk widgets, if being used.
 *
 * @param element An element which must live at a point in the DOM tree at or above the app root. By default,
 *  this will be the DOM root.
 */
@Composable
fun ColorModeAware(element: HTMLElement = document.documentElement.unsafeCast<HTMLElement>()) {
    val colorMode = ColorMode.current
    DisposableEffect(colorMode) {
        // Use this and not `addClass` because after an export, the opposite color mode might be set on some
        // elements (if that was the color mode at export time)
        element.setStyleFor(colorMode)
        onDispose {
            element.removeClass(colorMode.cssClass)
        }
    }
}

/**
 * Set the CSS class representing the active color mode on the element with the target ID. This will enable color mode
 * aware styles for all the element's descendants and set the necessary CSS variables for Silk widgets, if being used.
 */
@Composable
fun ColorModeAware(elementId: String) {
    ColorModeAware(document.getElementById(elementId) as HTMLElement)
}

/**
 * Set the CSS class representing the provided color mode on [this]. This will set the color mode for styles on the
 * element's descendants and set the necessary CSS variables for Silk widgets, if being used.
 */
fun HTMLElement.setStyleFor(colorMode: ColorMode) {
    removeClass(colorMode.opposite.cssClass)
    addClass(colorMode.cssClass)
}
