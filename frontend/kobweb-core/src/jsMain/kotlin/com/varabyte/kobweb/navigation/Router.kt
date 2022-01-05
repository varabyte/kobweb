package com.varabyte.kobweb.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.PageContext
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Page
@Composable
fun ErrorPage(errorCode: Int) {
    Div {
        Text("Error code: $errorCode")
    }
}

private typealias PageMethod = @Composable () -> Unit

private class PageData(
    val pageMethod: PageMethod,
    val pageContext: PageContext,
)

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
    private val pages = mutableMapOf<String, PageMethod>()

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
        val pathParts = pathAndQuery.split('?', limit = 2)
        val path = pathParts[0]

        if (!Path.isLocal(path)) {
            require(allowExternalPaths) { "Navigation to \"$pathAndQuery\" not expected by callee" }
            // Open external links in a new tab
            // TODO(#90): Allow configuring other options. In place would be window.location.assign(...)
            window.open(pathAndQuery, target = "_blank")
            return false
        }

        val pageMethod = pages[path] ?: { ErrorPage(404) }
        val ctx = PageContext(this)
        if (pathParts.size == 2) {
            pathParts[1].split("&").forEach { param ->
                val (key, value) = param.split('=', limit = 2)
                ctx.mutableParams[key] = value
            }
        }

        activePageData.value = PageData(pageMethod, ctx)
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

    @Suppress("unused") // Called by generated code
    fun register(path: String, page: PageMethod) {
        if (Path.isLocal(path)) {
            pages[path] = page
        }
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