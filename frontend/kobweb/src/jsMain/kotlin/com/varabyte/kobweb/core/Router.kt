package com.varabyte.kobweb.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
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
    private val backHistory = mutableListOf<PageMethod>()
    private val forwardHistory = mutableListOf<PageMethod>()

    @Composable
    fun renderActivePage() {
        activePageMethod.value?.invoke()
            ?: throw IllegalStateException("Call 'navigateTo' at least once before calling 'getActivePage'")
    }

    fun register(path: String, page: PageMethod) {
        Path.check(path)
        pages[path] = page
    }

    fun navigateTo(path: String) {
        Path.check(path)

        val page = pages[path] ?: { ErrorPage(404) }
        forwardHistory.clear()
        activePageMethod.value?.let { activePage -> backHistory.add(activePage) }
        activePageMethod.value = page
        // TODO: Set the URL bar with the updated path
    }

    fun goBack(): Boolean {
        if (backHistory.isEmpty()) return false

        forwardHistory.add(0, backHistory.removeLast())
        return true
    }

    fun goForward(): Boolean {
        if (forwardHistory.isEmpty()) return false

        backHistory.add(forwardHistory.removeAt(0))
        return true
    }
}