package com.varabyte.kobweb.browser.dom

import org.w3c.dom.Window

/**
 * Returns a [Selection] object representing the range of text selected by the user or the current position of the caret.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window/getSelection">Window.getSelection()</a>
 */
fun Window.getSelection(): Selection? = asDynamic().getSelection() as Selection?
