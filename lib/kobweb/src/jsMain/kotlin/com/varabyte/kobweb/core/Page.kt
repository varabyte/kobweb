package com.varabyte.kobweb.core

import androidx.compose.runtime.Composable

/**
 * An annotation which identifies a [Composable] function as one which will be used to render a page.
 *
 * By default, the page's filename will be used as its name, e.g. "HomePage.kt" -> "home", but you can provide a
 * specific override for a page if you want one.
 *
 * The name index is special, and if seen, it will be used to indicate this is a default page given the current URL.
 * For example, "pages/blog/Index.kt" (or "pages/blog/IndexPage.kt", or `@Page("index")`) will be rendered when the
 * user visits "pages/blog".
 *
 * Finally, there must not be any duplicate page names given a directory scope. If Kobweb encounters this, it will log
 * an error and discard duplicates arbitrarily.
 */
annotation class Page(
    val name: String = ""
)