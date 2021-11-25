package com.varabyte.kobweb.core

import androidx.compose.runtime.*

/**
 * An annotation which identifies a [Composable] function as one which will be used to render a page.
 *
 * The page's filename will be used to generate its slug, e.g. "pages/account/Profile.kt" ->
 * "/account/profile".
 *
 * The name "index" is special, and if encountered, it will be understood that this is a default page given the current
 * URL. For example, "pages/blog/Index.kt" will be rendered when the user visits "/blog".
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Page