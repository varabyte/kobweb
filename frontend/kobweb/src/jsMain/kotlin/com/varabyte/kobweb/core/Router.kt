package com.varabyte.kobweb.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
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

/**
 * The class responsible for navigating to different pages in a user's app.
 */
object Router {
    private val activePageMethod = mutableStateOf<PageMethod?>(null)
    private val pages = mutableMapOf<String, PageMethod>()

    @Suppress("unused") // Called by generated code
    @Composable
    fun renderActivePage() {
        activePageMethod.value?.invoke()
            ?: throw IllegalStateException("Call 'navigateTo' at least once before calling 'getActivePage'")
    }

    @Suppress("unused") // Called by generated code
    fun register(path: String, page: PageMethod) {
        if (Path.isLocal(path)) {
            pages[path] = page
        }
    }

    /**
     * @param allowExternalPaths If true, this method should handle
     */
    fun navigateTo(path: String, allowExternalPaths: Boolean = true) {
        if (!Path.isLocal(path)) {
            require(allowExternalPaths) { "Navigation to \"$path\" not expected by callee" }
            window.location.assign(path)
            return
        }

        val page = pages[path] ?: { ErrorPage(404) }
        activePageMethod.value = page

        // Update URL to match page we navigated to
        // TODO: Support query params
        "${window.location.origin}$path".let { url ->
            if (window.location.href != url.removeSuffix("/") ) {
                window.history.replaceState(window.history.state, "", url)
            }
        }
    }
}