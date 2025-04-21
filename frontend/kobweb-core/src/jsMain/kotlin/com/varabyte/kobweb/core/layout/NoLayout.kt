package com.varabyte.kobweb.core.layout

import com.varabyte.kobweb.core.Page

/**
 * An annotation which declares that a [Page]-annotated function is explicitly opting out of using a layout.
 *
 * This can be particularly useful if you applied a layout to all pages under a route but want to opt-out some of them.
 *
 * ```
 * // pages/Layout.kt
 * @file:Layout(".components.layouts.PageLayout")
 *
 * package pages
 *
 * import com.varabyte.kobweb.core.layout.Layout
 *
 * // pages/admin/Listing.kt
 * @Page
 * @NoLayout
 * fun ListingPage() {
 *   // Undecorated page
 * }
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
annotation class NoLayout()

const val NO_LAYOUT_FQN = "com.varabyte.kobweb.core.layout.NoLayout"
