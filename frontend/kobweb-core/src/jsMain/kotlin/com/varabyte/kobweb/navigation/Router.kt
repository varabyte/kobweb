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
    private val pathTree = PathTree()

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
    private fun updateActivePage(pathAndQuery: String, allowExternalPaths: Boolean = true): Boolean {
        val (path, query) = pathAndQuery.split('?', limit = 2).let {
            if (it.size == 1) { it[0] to null } else it[0] to it[1]
        }

        if (!Path.isLocal(path)) {
            require(allowExternalPaths) { "Navigation to \"$pathAndQuery\" not expected by callee" }
            // Open external links in a new tab
            // TODO(#90): Allow configuring other options. In place would be window.location.assign(...)
            window.open(pathAndQuery, target = "_blank")
            return false
        }

        activePageData.value = pathTree.createPageData(this, path, query)
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
     */
    @Suppress("unused") // Called by generated code
    fun register(path: String, pageMethod: PageMethod) {
        require(Path.isLocal(path) && path.startsWith('/')) { "Registration only allowed for internal, rooted routes, e.g. /example/path. Got: $path" }
        require(pathTree.register(path, pageMethod)) { "Registration failure. Path is already registered: $path" }
    }

    fun setErrorHandler(errorHandler: ErrorPageMethod) {
        pathTree.errorHandler = errorHandler
    }

    /**
     * @param pathAndQuery The path to a page, including (optional) search params, e.g. "/example/path?arg=1234"
     * @param allowExternalPaths If true, the path passed in can be for URLs pointing at a totally different site.
     * @param updateHistoryMode How this new path should affect the history. See [UpdateHistoryMode] docs for more
     *   details. Note that this value will be ignored if [pathAndQuery] refers to an external link.
     */
    fun navigateTo(pathAndQuery: String, allowExternalPaths: Boolean = true, updateHistoryMode: UpdateHistoryMode = UpdateHistoryMode.PUSH) {
        if (updateActivePage(pathAndQuery, allowExternalPaths)) {
            // Update URL to match page we navigated to
            "${window.location.origin}$pathAndQuery".let { url ->
                if (window.location.href != url) {
                    when (updateHistoryMode) {
                        UpdateHistoryMode.PUSH -> window.history.pushState(window.history.state, "", url)
                        UpdateHistoryMode.REPLACE -> window.history.replaceState(window.history.state, "", url)
                    }
                }
            }
        }
    }
}