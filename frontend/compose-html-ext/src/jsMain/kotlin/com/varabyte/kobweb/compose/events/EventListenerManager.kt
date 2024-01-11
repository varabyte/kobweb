package com.varabyte.kobweb.compose.events

/**
 * A simple manager class for handling listeners added to a target element.
 *
 * This class is useful for when you want to add listeners to an element and then remove them all later, say when some
 * wrapping class gets disposed.
 */
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.events.EventListenerManager` instead (that is, `compose` → `browser`).")
typealias EventListenerManager = com.varabyte.kobweb.browser.events.EventListenerManager
