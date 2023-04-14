package com.varabyte.kobweb.compose.dom

import org.w3c.dom.Element

fun Element.clearFocus() {
    val dynElement: dynamic = this
    // Blur is a bad name - it means, here, remove focus
    // https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/blur
    dynElement.blur()
}