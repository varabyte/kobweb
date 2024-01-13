package com.varabyte.kobweb.browser.dom

import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

fun HTMLElement.clearFocus() {
    // Blur is a bad name - it means, here, remove focus
    // https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/blur
    blur()
}

// Provided temporarily as a way to help people migrate away from com.varabyte.kobweb.compose.dom.clearFocus
@Deprecated(
    "Use the HTMLElement version instead",
    ReplaceWith("(this as? HTMLElement)?.clearFocus()"),
)
fun Element.clearFocus() {
    (this as? HTMLElement)?.clearFocus()
}
