package com.varabyte.kobweb.compose.dom

import org.w3c.dom.Element
import com.varabyte.kobweb.browser.dom.clearFocus

@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.dom.clearFocus` instead (that is, `compose` → `browser`).")
fun Element.clearFocus() {
    @Suppress("DEPRECATION")
    this.clearFocus()
}
