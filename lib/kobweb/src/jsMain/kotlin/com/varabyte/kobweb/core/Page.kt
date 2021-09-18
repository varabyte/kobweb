package com.varabyte.kobweb.core

import androidx.compose.runtime.Composable

/**
 * An annotation which identifies a [Composable] function as one which will be used to render a page.
 *
 * By default, the page's filename will be used to generate its slug, e.g. "pages/account/ProfilePage.kt" ->
 * "/account/profile", but you can provide a specific override for a page if you want one.
 *
 * The name "index" is special, and if encountered, it will be understood that this is a default page given the current
 * URL. For example, "pages/blog/Index.kt" (or "pages/blog/IndexPage.kt", or `@Page("index")` in a file that lives in
 * the `blog` folder) will be rendered when the user visits "/blog".
 *
 * Finally, there must not be any duplicate page names given a directory scope. If Kobweb encounters this, it will log
 * an error and discard duplicates arbitrarily.
 */
annotation class Page(
    val slug: String = ""
)