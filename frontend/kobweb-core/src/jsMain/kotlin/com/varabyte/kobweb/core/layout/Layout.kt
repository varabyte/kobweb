package com.varabyte.kobweb.core.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page

/**
 * An annotation which declares that a [Composable] function is either a layout or a page with a layout.
 *
 * ## Declaring layouts
 *
 * A `Layout` annotation can be used to tag a method as one that provides a layout, which is high-level UI scaffolding
 * that is sharable across many pages.
 *
 * A layout method may contain an optional first parameter which is a `PageContext` and must end with a
 * `@Composable () -> Unit` content callback:
 *
 * ```
 * // Note: You can omit `ctx: PageContext` if you don't need it
 * @Layout
 * @Composable
 * fun PageLayout(ctx: PageContext, content: @Composable () -> Unit) {
 *    /*...*/
 *    content()
 *    /*...*/
 * }
 * ```
 *
 * A layout may also be nested within another layout. In that case, the `@Layout` annotation should contain a path to
 * the other layout:
 *
 * ```
 * @Layout(".components.layouts.PageLayout")
 * @Composable
 * fun NestedLayout(content: @Composable () -> Unit) {
 *    /*...*/
 * }
 * ```
 *
 * ## Setting a page's layout
 *
 * A `Layout` annotation can also be added to a [Page] method, which in that case would indicate that this page is
 * nested inside that layout.
 *
 * ```
 * @Page
 * @Layout(".components.layouts.BlogLayout")
 * @Composable
 * fun BlogPage() {
 *   /* ... */
 * }
 * ```
 *
 * By setting up your pages like this, Kobweb will call the methods for you in a way that ensures the composition
 * hierarchy looks like what you'd intuitively expect: `App { Layout { Page() } } }`
 *
 * ## Default layouts
 *
 * Finally, the `@Layout` annotation can be applied to a file as well, which, as long as it lives under the root `pages`
 * package, can let users specify a default layout that will apply to all pages defined under that package.
 *
 * For example, you can set up default layouts for all pages in your site like so:
 *
 * ```
 * // pages/Layout.kt
 * @file:Layout(".components.layouts.PageLayout")
 *
 * package pages
 *
 * import com.varabyte.kobweb.core.layout.Layout
 * ```
 * ```
 * // pages/blog/Layout.kt
 * @file:Layout(".components.layouts.BlogLayout")
 *
 * package pages
 *
 * import com.varabyte.kobweb.core.layout.Layout
 * ```
 *
 * When multiple default layouts could apply to a page, the most specific one will be used. For example, if a page is in
 * the `pages.blog` package, it will use `BlogLayout` above, not `PageLayout`.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class Layout(@Suppress("unused") val fqn: String = "")
