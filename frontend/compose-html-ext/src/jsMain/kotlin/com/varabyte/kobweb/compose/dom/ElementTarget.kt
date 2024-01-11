@file:Suppress("DEPRECATION")

package com.varabyte.kobweb.compose.dom

/** An interface for finding some target element, relative to some given initial element. */
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.dom.ElementTarget` instead (that is, `compose` → `browser`).")
typealias ElementTarget = com.varabyte.kobweb.browser.dom.ElementTarget
