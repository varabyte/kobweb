package com.varabyte.kobweb.core

import androidx.compose.runtime.*
import com.varabyte.kobweb.navigation.Router
import kotlinx.browser.window

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
class PageContext(val router: Router) {
    companion object {
        internal val active by lazy { mutableStateOf<PageContext?>(null) }
    }

    internal val mutableParams = mutableMapOf<String, String>()

    /**
     * The slug for the current page, stripped of any fragments and parameters.
     *
     * This is a convenience function, so you don't have to remember the right `window.location`
     * field to call.
     */
    val slug: String get() = window.location.pathname

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
    val params: Map<String, String> = mutableParams

    /**
     * The post-hash fragment of a URL, if specified.
     *
     * For example, `/a/b/c/#fragment-id` will be the value `fragment-id`
     */
    var fragment: String? = null
        internal set
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
fun rememberPageContext(): PageContext = remember { PageContext.active.value ?: error("`rememberPageContext` called outside of the scope of a `@Page` annotated method.") }