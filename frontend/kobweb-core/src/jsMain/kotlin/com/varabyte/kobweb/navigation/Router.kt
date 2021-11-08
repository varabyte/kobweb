package com.varabyte.kobweb.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.PageContext
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

/**
 * The class responsible for navigating to different pages in a user's app.
 */
class Router {
    private val activePageData = mutableStateOf<PageData?>(null)
    private val pages = mutableMapOf<String, PageMethod>()

    @Suppress("unused") // Called by generated code
    @Composable
    fun renderActivePage() {
        val data = activePageData.value
            ?: error("Call 'navigateTo' at least once before calling 'renderActivePage'")

        PageContext.active.value = data.pageContext
        data.pageMethod.invoke()
        PageContext.active.value = null
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
     */
    fun navigateTo(pathAndQuery: String, allowExternalPaths: Boolean = true) {
        val pathParts = pathAndQuery.split('?', limit = 2)
        val path = pathParts[0]

        if (!Path.isLocal(path)) {
            require(allowExternalPaths) { "Navigation to \"$pathAndQuery\" not expected by callee" }
            window.location.assign(pathAndQuery)
            return
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

        // Update URL to match page we navigated to
        "${window.location.origin}$pathAndQuery".let { url ->
            if (window.location.href != url) {
                window.history.replaceState(window.history.state, "", url)
            }
        }
    }
}