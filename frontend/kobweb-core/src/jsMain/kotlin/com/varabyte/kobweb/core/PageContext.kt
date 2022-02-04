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
class PageContext(val router: Router) {
    companion object {
        internal val active by lazy { mutableStateOf<PageContext?>(null) }
    }

    internal val mutableParams = mutableMapOf<String, String>()

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

@Composable
fun rememberPageContext(): PageContext = remember { PageContext.active.value!! }