package nekt.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

private class ErrorPage(private val errorCode: Int) : Page() {
    @Composable
    override fun render() {
        Div {
            Text("Error code: $errorCode")
        }
    }
}

/**
 * The class responsible for navigating to different pages in a user's app.
 */
object Router {
    private val activePageState = mutableStateOf<Page?>(null)
    private val pages = mutableMapOf<String, Page>()
    private val backHistory = mutableListOf<Page>()
    private val forwardHistory = mutableListOf<Page>()

    @Composable
    fun getActivePage(): Page = activePageState.value
        ?: throw IllegalStateException("Call 'navigateTo' at least once before calling 'getActivePage'")

    fun register(path: String, page: Page) {
        Path.check(path)
        pages[path] = page
    }

    fun navigateTo(path: String) {
        Path.check(path)

        val page = pages[path] ?: ErrorPage(404)
        forwardHistory.clear()
        activePageState.value?.let { activePage -> backHistory.add(activePage) }
        activePageState.value = page
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