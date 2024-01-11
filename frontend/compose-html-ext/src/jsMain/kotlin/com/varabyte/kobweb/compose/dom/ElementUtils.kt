package com.varabyte.kobweb.compose.dom

import com.varabyte.kobweb.browser.dom.ancestors
import com.varabyte.kobweb.browser.dom.descendantsBfs
import com.varabyte.kobweb.browser.dom.descendantsDfs
import com.varabyte.kobweb.browser.dom.generateUniqueId
import org.w3c.dom.Document
import org.w3c.dom.HTMLElement

/** Generates an element ID guaranteed unique for the current [Document]. */
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.dom.generateUniqueId` instead (that is, `compose` → `browser`).")
fun Document.generateUniqueId(len: Int = 6, prefix: String = "") = generateUniqueId(len, prefix)

/**
 * Returns a sequence of all ancestors of this element, starting with the element itself.
 *
 * If you want to exclude the element itself from the sequence, simply call `drop(1)` on the returned sequence.
 */
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.dom.ancestors` instead (that is, `compose` → `browser`).")
val HTMLElement.ancestors get() = ancestors

/**
 * Returns a sequence of all descendants of this element, starting with the element itself, in a breadth-first manner.
 *
 * If you want to exclude the element itself from the sequence, simply call `drop(1)` on the returned sequence.
 */
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.dom.descendantsBfs` instead (that is, `compose` → `browser`).")
val HTMLElement.descendantsBfs get() = descendantsBfs

/**
 * Returns a sequence of all descendants of this element, starting with the element itself, in a depth-first manner.
 *
 * If you want to exclude the element itself from the sequence, simply call `drop(1)` on the returned sequence.
 */
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.dom.descendantsDfs` instead (that is, `compose` → `browser`).")
val HTMLElement.descendantsDfs get() = descendantsDfs
