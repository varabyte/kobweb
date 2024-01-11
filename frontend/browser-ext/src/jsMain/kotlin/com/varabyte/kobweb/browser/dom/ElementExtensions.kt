package com.varabyte.kobweb.browser.dom

import org.w3c.dom.HTMLElement

fun HTMLElement.clearFocus() {
    // Blur is a bad name - it means, here, remove focus
    // https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/blur
    blur()
}
