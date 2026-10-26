package com.varabyte.kobweb.core

import androidx.compose.runtime.*

/**
 * An annotation which identifies a [Composable] function as one which will be used to render a page.
 *
 * The page's filename will be used to generate its slug, e.g. "pages/account/Profile.kt" ->
 * "/account/profile".
 *
 * Additionally, the name is converted into kebab-case, e.g. "pages/WelcomeIntro.kt" -> "/welcome-intro"
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
 * However, if you do set it, in most cases, it should just be a single word, which changes the slug used for this page
 * (instead of the file name). Lowercase is recommended, but the value is used exactly as written, since URL paths are
 * case-sensitive (e.g. `@Page("AboutUs")` generates ".../AboutUs", which can be useful for preserving legacy URLs).
 * You can use the name "index" here which will be treated specially as mentioned above. (This check is also
 * case-sensitive, so "Index" is treated like any other name.)
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
 * ## URL validity
 *
 * The routeOverride value must be valid in a URL path as written, since Kobweb doesn't decode it. Characters that the
 * browser would percent-encode, such as spaces, curly braces (outside of a dynamic segment), and non-ASCII characters,
 * as well as `?` and `#`, result in a build error. If you want any of these in your URL, write their percent-encoded
 * value instead, e.g. `@Page("about%20us")`.
 *
 * ## Reserved routes
 *
 * Routes whose first segment is `api` (e.g. "/api" or "/api/users") are reserved for the Kobweb server, which serves
 * API endpoints there as well as internal endpoints (such as the one that powers live reloading during development).
 * This applies to static sites too, since they are served by the same server when you run them locally. Using such a
 * route for a page results in a build error.
 *
 * @param routeOverride If specified, override the logic for generating a route for this page as documented in this
 *   header doc.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Page(val routeOverride: String = "")
