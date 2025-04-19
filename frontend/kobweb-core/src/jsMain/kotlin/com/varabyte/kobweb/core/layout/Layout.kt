package com.varabyte.kobweb.core.layout

import androidx.compose.runtime.*

/**
 * An annotation which declares that a [Composable] function is one that should be wrapped by a parent layout.
 *
 * A `Layout` annotation takes a path to a composable method which is designed to serve as the parent layout for this
 * method. For example, a declaration might look like `@Layout(".components.layout.ExampleLayout")`.
 *
 * This target layout method may optionally accept a `ctx: PageContext` argument and must end with a
 * `content: @Composable () -> Unit` lambda parameter.
 *
 * The motivation behind this feature is it allows us to convert pages from this:
 * ```
 * @Page
 * @Composable
 * fun BlogPage() {
 *   BlogLayout {
 *     /* ... */
 *   }
 * }
 * ```
 * to this:
 * ```
 * @Page
 * @Layout(".components.layouts.BlogLayout")
 * @Composable
 * fun BlogPage() {
 *   /* ... */
 * }
 * ```
 * which, in addition to reducing one level of nesting, plays nicer with Compose by correcting the composable hierarchy
 * to what it should be.
 *
 * For example, above, users will mentally model the page as "App > BlogLayout > BlogPage" intuitively (even if, in
 * practice, the actual hierarchy is "App > BlogPage > BlogLayout").
 *
 * Without using `@Layout`, a page will still render just fine. However, if users navigate between different pages,
 * Compose will lose any state normally stored inside the layout, because from its point of view, it sees the "layout"
 * composable as a child to each page, so each one is considered separate.
 *
 * In other words, the remembered `state` value in a layout like this:
 * ```
 * @Composable
 * fun ExampleLayout(content: @Composable () -> Unit) {
 *   val state = remember { /* ... */ }
 * }
 * ```
 * will be remembered across pages that use `@Layout` to reference the layout method, but it will be discarded on pages
 * that manually call `ExampleLayout` directly themselves:
 * ```
 * // Navigating between the following two pages will result in `ExampleLayout` from resetting any remembered state.
 *
 * @Page
 * @Composable
 * fun FirstPage() {
 *   ExampleLayout { /* ... */ }
 * }
 *
 * @Page
 * @Composable
 * fun SecondPage() {
 *   ExampleLayout { /* ... */ }
 * }
 * ```
 *
 * Users will often want to pass arguments from a child page to its parent layout. If so, the `PageContext` class
 * includes a `data` map they can use for this purpose:
 *
 * ```
 * @Composable
 * fun PageLayout(ctx: PageContext, content: @Composable () -> Unit) {
 *   val title = ctx.data["title"] as? String ?: ""
 *   LaunchedEffect(title) {
 *     document.title = title
 *   }
 * }
 *
 * @Page
 * @Composable
 * @Layout(".components.layout.PageLayout")
 * fun HomePage(ctx: PageContext) {
 *   LaunchedEffect(Unit) {
 *     ctx.data["title"] = "Home"
 *   }
 * }
 * ```
 *
 * Note that a layout can delegate to another layout, like so:
 * ```
 * @Composable()
 * fun PageLayout(content: @Composable () -> Unit) { /* ... */ }
 *
 * @Composable
 * @Layout(".components.layout.PageLayout")
 * fun BlogLayout(content: @Composable () -> Unit) { /* ... */ }
 *
 * @Page
 * @Composable
 * @Layout(".components.layout.BlogLayout")
 * fun BlogPage() { /* ... */ }
 * ```
 * which, before layouts, used to look like this:
 * ```
 * @Composable()
 * fun PageLayout(content: @Composable () -> Unit) { /* ... */ }

 * @Composable
 * fun BlogLayout(content: @Composable () -> Unit) {
 *   PageLayout(content)
 * }
 *
 * @Page
 * @Composable
 * @Layout(".components.layout.BlogLayout")
 * fun BlogPage() {
 *   BlogLayout { /* ... */ }
 * }
 * ```
 *
 * And finally, note that the `@Layout` annotation can be applied to a file as well, which, as long as it is nested
 * under the root page package, can let users specify a default layout for all pages under that package. For example,
 * you can set up default layouts for all pages in your site like so:
 *
 * ```
 * // pages/Layout.kt
 * @file:Layout(".components.layouts.PageLayout")
 *
 * package pages
 *
 * import com.varabyte.kobweb.core.layout.Layout
 *
 * // pages/blog/Layout.kt
 * @file:Layout(".components.layouts.BlogLayout")
 *
 * package pages
 *
 * import com.varabyte.kobweb.core.layout.Layout
 * ```
 *
 * If a `@Layout` is annotated on a page directly, that will always take precedence. Otherwise, the most specific
 * `@Layout` will be used. For example, if a page is in the `pages.blog` package, it will use the `BlogLayout` above,
 * not the `PageLayout`.
 *
 * Ultimately, users are heavily encouraged to use `@Layout` for their pages (as opposed to manually calling a layout
 * composable method directly inside their page), as remembering layout state across pages is very useful. For example,
 * your layout can contain a sidebar with collapsable sections. When you reference this layout via the `@Layout`
 * annotation, it will remember which sections are collapsed even as you navigate across pages.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class Layout(@Suppress("unused") val fqn: String)
