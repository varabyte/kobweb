package com.varabyte.kobweb.compose.dom.observers

/**
 * A performant mechanism by which code can monitor an element for changes to its size.
 *
 * See https://developer.mozilla.org/en-US/docs/Web/API/Resize_Observer_API
 */
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.dom.observers.ResizeObserver` instead (that is, `compose` → `browser`).")
typealias ResizeObserver = com.varabyte.kobweb.browser.dom.observers.ResizeObserver
