package com.varabyte.kobweb.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.PageContext
import kotlinx.browser.document
import kotlinx.browser.window

/** How to affect the current history when navigating to a new location */
enum class UpdateHistoryMode {
    /**
     * Push the new URL onto the stack, meaning if the user presses back, they return to the current URL.
     *
     * This is the most common and expected behavior when navigating within this site.
     */
    PUSH,

    /**
     * Overwrite the current URL, meaning if the user presses back, they'll go back to the URL before that one.
     *
     * This is most often useful if you aren't really navigating but are instead just changing query parameters on some
     * current page that represent transient state changes.
     */
    REPLACE
}

/**
 * The class responsible for navigating to different pages in a user's app.
*/
class Router {
    private val activePageData = mutableStateOf<PageData?>(null)
    private val routeTree = RouteTree()

    init {
        window.onpopstate = {
            updateActivePage(document.location!!.pathname, allowExternalPaths = false)
        }
    }

    /**
     * See docs for [navigateTo]
     *
     * Returns true if we updated the active page ourselves or false if we didn't (which means the URL instead goes to
     * an external site)
     */
    private fun updateActivePage(pathQueryAndFragment: String, allowExternalPaths: Boolean = true): Boolean {
        val (pathAndQuery, fragment) = pathQueryAndFragment.split('#', limit = 2).let {
            if (it.size == 1) { it[0] to null } else it[0] to it[1]
        }

        val (path, query) = pathAndQuery.split('?', limit = 2).let {
            if (it.size == 1) { it[0] to null } else it[0] to it[1]
        }.let { pair ->
            // Note: If the user typed just a fragment, e.g. "#id", that means search the current page for an element
            // with that ID
            if (pair.first.isBlank() && fragment != null) {
                window.location.pathname to pair.second
            } else pair
        }

        if (!Route.isLocal(path)) {
            require(allowExternalPaths) { "Navigation to external URL \"$pathQueryAndFragment\" not expected by callee" }
            // Open external links in a new tab
            // TODO(#90): Allow configuring other options. In place would be window.location.assign(...)
            window.open(pathQueryAndFragment, target = "_blank")
            return false
        }

        activePageData.value = routeTree.createPageData(this, path, query, fragment)
        return true
    }

    @Suppress("unused") // Called by generated code
    @Composable
    fun renderActivePage() {
        val data = activePageData.value
            ?: error("Call 'navigateTo' at least once before calling 'renderActivePage'")

        PageContext.active.value = data.pageContext
        data.pageMethod.invoke()
    }

    /**
     * It's possible a link might just target a fragment, e.g. "#id", which we should handle by keeping the current
     * URL but with this new hash.
     */
    private fun String.addPathIfMissing(): String {
        return this.takeIf { !it.startsWith('#') } ?:
        (window.location.href.substringBefore('#').removePrefix(window.location.origin) + this)
    }

    /**
     * Register a route, mapping it to some target composable method that will get called when that path is requested by
     * the browser.
     *
     * Routes should be internal, rooted paths, so:
     *
     * * Good: `/path`
     * * Good: `/path/with/subparts`
     * * Bad: `path`
     * * Bad: `http://othersite.com/path`
     *
     * Paths can also be dynamic routes, i.e. with parts that will consume values typed into the URL and exposed as
     * variables to the page. To accomplish this, use curly braces for that part of the path.
     *
     * For example: `/users/{user}/posts/{post}`
     *
     * In that case, if the user visited `/users/123456/posts/321`, then that composable method will be visited, with
     * `user = 123456` and `post = 321` passed down in the `PageContext`.
     *
     * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
     *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
     *   staying in the same domain.
     */
    @Suppress("unused") // Called by generated code
    fun register(route: String, autoPrefix: Boolean = true, pageMethod: PageMethod) {
        require(Route.isLocal(route) && route.startsWith('/')) { "Registration only allowed for internal, rooted routes, e.g. /example/path. Got: $route" }
        require(routeTree.register(RoutePrefix.prependIf(autoPrefix, route), pageMethod)) { "Registration failure. Path is already registered: $route" }
    }

    fun setErrorHandler(errorHandler: ErrorPageMethod) {
        routeTree.errorHandler = errorHandler
    }

    /**
     * For reference to the parts of a URL, see the [standards](https://www.rfc-editor.org/rfc/rfc3986#section-3.3)
     * documentation.
     *
     * @param pathQueryAndFragment The path to a page, including (optional) search params and hash,
     *   e.g. "/example/path?arg=1234#fragment"
     * @param allowExternalPaths If true, the path passed in can be for URLs pointing at a totally different site.
     * @param updateHistoryMode How this new path should affect the history. See [UpdateHistoryMode] docs for more
     *   details. Note that this value will be ignored if [pathQueryAndFragment] refers to an external link.
     */
    fun navigateTo(pathQueryAndFragment: String, allowExternalPaths: Boolean = true, updateHistoryMode: UpdateHistoryMode = UpdateHistoryMode.PUSH) {
        @Suppress("NAME_SHADOWING") // Intentionally transformed
        val pathQueryAndFragment = pathQueryAndFragment.addPathIfMissing()

        if (updateActivePage(pathQueryAndFragment, allowExternalPaths)) {
            // Update URL to match page we navigated to
            "${window.location.origin}$pathQueryAndFragment".let { url ->
                if (window.location.href != url) {
                    when (updateHistoryMode) {
                        UpdateHistoryMode.PUSH -> window.history.pushState(window.history.state, "", url)
                        UpdateHistoryMode.REPLACE -> window.history.replaceState(window.history.state, "", url)
                    }
                }

                // Even if the URL hasn't changed, still scroll to the target element if you can. Sometimes a user might
                // scroll the page and then re-enter the same URL to go back.
                if (url.contains('#')) {
                    document.getElementById(url.substringAfter('#'))?.scrollIntoView(js("{behavior: \"smooth\"}"))
                }
            }
        }
    }
}