package com.varabyte.kobweb.core

import androidx.compose.runtime.*

/**
 * An annotation which identifies a [Composable] function as one which will be used to render a page.
 *
 * The page's filename will be used to generate its slug, e.g. "pages/account/Profile.kt" ->
 * "/account/profile".
 *
 * Additionally, the name transformation lowercases the route, e.g. "pages/WelcomeIntro.kt" -> "/welcomeintro"
 *
 * The name "index" is special, and if encountered, it will be understood that this is a default page given the current
 * URL. For example, "pages/blog/Index.kt" will be rendered when the user visits "/blog".
 *
 * ## routeOverride
 *
 * Note that the route generated for this page is quite customizable by setting the [routeOverride] parameter. In
 * general, you should NOT set it, as this will make it harder for people to navigate your project and find where a
 * URL is coming from.
 *
 * However, if you do set it, in most cases, it is expected to just be a single, lowercase word, which changes the slug
 * used for this page (instead of the file name). You can use the name "index" here which will be treated specially as
 * mentioned above.
 *
 * But wait, there's more!
 *
 * If the value starts with a slash, it will be treated as a full path starting at the site's root. If the value ends
 * with a slash, it means the override represents a change in the URL path but the slug will still be derived from
 * the filename.
 *
 * Some examples should clear up the various cases. Let's say the site is `com.example` and this `@Page` is defined in
 * `package pages.a.b.c` in file `Slug.kt`:
 *
 * ```
 * @Page -> example.com/a/b/c/slug
 * @Page("other") -> example.com/a/b/c/other
 * @Page("index") -> example.com/a/b/c/
 * @Page("d/e/f/") -> example.com/a/b/c/d/e/f/slug
 * @Page("d/e/f/other") -> example.com/a/b/c/d/e/f/other
 * @Page("/d/e/f/") -> example.com/d/e/f/slug
 * @Page("/") -> example.com/slug
 * ```
 *
 * And one last item worth mentioning is the special dynamic route indicator, `{}`. Basically, if any part of the route
 * override contains a value surrounded by curly braces, that means we should generate a final URL with the current page
 * being part of a dynamic route. An empty `"{}"` means the name of the current file will be used:
 *
 * ```
 * @Page -> example.com/a/b/c/slug
 * @Page("{}") -> example.com/a/b/c/{slug}
 * @Page("{slug}") -> example.com/a/b/c/{slug}
 * @Page("d/e/f/{}") -> example.com/a/b/c/d/e/f/{slug}
 * @Page("/d/e/f/{}") -> example.com/d/e/f/{slug}
 * @Page("/d/{id}/f/{}") -> example.com/d/{id}/f/{slug}
 * ```
 *
 * See also [PackageMapping] which can be used to change any intermediate part of a route.
 *
 * @param routeOverride If specified, override the logic for generating a route for this page as documented in this
 *   header doc.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Page(val routeOverride: String = "")
