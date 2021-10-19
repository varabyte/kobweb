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
 *
 * @param slug If set, will be used as the path for this page instead of the file path. If the value starts with a
 *   slash, then it means the value represents the full slug path; otherwise, it will be appended onto a root extracted
 *   from the current filename, e.g.
 *   `"pages/account/Utilities.kt" + "Page("admin/settings") = "pages/account/admin/settings"`
 *   This parameter is provided for flexibility, but it is recommended to use it only as a last resort, as most people
 *   will expect a `@Page` to be tied to the current layout.
 * @param useMethodName If [slug] is not set and this value is true, then the method name (instead of the file name)
 *   will be used to generate the final slug.
 */
annotation class Page(
    val slug: String = "",
    val useMethodName: Boolean = false,
)