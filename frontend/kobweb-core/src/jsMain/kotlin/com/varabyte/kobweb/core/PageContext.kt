package com.varabyte.kobweb.core

import androidx.compose.runtime.*
import com.varabyte.kobweb.navigation.Router

/**
 * Various contextual information useful for a page.
 *
 * Access it using [rememberPageContext] either in the page itself or within any composable nested inside of it.
 *
 * ```
 * @Page
 * @Composable
 * fun SettingsPage() {
 *    val ctx = rememberPageContext()
 *    val userName = ctx.params["username"] ?: "Unknown user"
 *    ...
 * }
 */
// Don't put properties in constructors so their header comments have room to breathe.
@Suppress("CanBePrimaryConstructorProperty")
class PageContext internal constructor(val router: Router, route: String, params: Map<String, String>, fragment: String? = null) {
    companion object {
        internal val active = mutableStateOf<PageContext?>(null)
    }

    /**
     * The slug for the current page.
     *
     * In the URL: "https://example.com/a/b/c/slug?x=1&y=2#id", the slug is "slug"
     */
    val slug: String = route.substringAfterLast('/')

    /**
     * The current route.
     *
     * This property is equivalent to `window.location.pathname` but provided here as a convenience property.
     *
     * It makes for a very useful key to use in, say, a `LaunchedEffect`, if you want to trigger some logic that should
     * be fired when the current page changes but not if query parameters or the hash fragments change:
     *
     * ```
     * val ctx = rememberPageContext()
     * LaunchedEffect(ctx.route) { ... }
     * ```
     */
    val route: String = route

    /**
     * Params extracted either from the URL's query parameters OR from a dynamic route
     *
     * For example:
     *
     * ```
     * /users/posts?user=123&post=11
     * ```
     *
     * and/or
     *
     * ```
     * /users/123/posts/11
     *
     * # for a URL registered as "/users/{user}/posts/{post}"
     * ```
     *
     * will generate a mapping of "user" to 123 and "post" to 11
     */
    val params: Map<String, String> = params

    /**
     * The post-hash fragment of a URL, if specified.
     *
     * For example, `/a/b/c/#fragment-id` will be the value `fragment-id`
     */
    val fragment: String? = fragment

    override fun equals(other: Any?): Boolean {
        return (other is PageContext
                && other.router === router
                // Note: No need to check slug, it's a subset of route
                && other.route == route
                && other.params == params
                && other.fragment == fragment
                )
    }

    override fun hashCode(): Int {
        var result = router.hashCode()
        result = 31 * result + route.hashCode()
        result = 31 * result + params.hashCode()
        result = 31 * result + fragment.hashCode()
        return result
    }

    fun copy(route: String = this.route, params: Map<String, String> = this.params, fragment: String? = this.fragment): PageContext {
        return PageContext(router, route, params, fragment)
    }
}

/**
 * A property which indicates if this current page is being rendered as part of a Kobweb export.
 *
 * While it should be rare that you'll need to use it, it can be useful to check if you want to avoid doing some
 * side-effect that shouldn't happen at export time, like sending page visit analytics to a server for example.
 */
val PageContext.isExporting: Boolean get() = params.containsKey("_kobwebIsExporting")

/**
 * Returns the active page's context.
 *
 * This will throw an exception if not called within the scope of a `@Page` annotated composable.
 */
@Composable
fun rememberPageContext() = remember { derivedStateOf { PageContext.active.value ?: error("`rememberPageContext` called outside of the scope of a `@Page` annotated method.") } }.value