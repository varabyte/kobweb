package com.varabyte.kobweb.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.PageContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

typealias PageMethod = @Composable () -> Unit
/**
 * Typealias for a composable method which takes an error code as its first and only argument (e.g. 404).
 *
 * Use [Router.setErrorHandler] to override with your own custom handler.
 */
typealias ErrorPageMethod = @Composable (Int) -> Unit

/**
 * The default error page logic used by Kobweb.
 */
@Page
@Composable
private fun ErrorPage(errorCode: Int) {
    Div {
        Text("Error code: $errorCode")
    }
}

internal class PageData(
    val pageMethod: PageMethod,
    val pageContext: PageContext,
)

/**
 * A tree data structure that represents a parsed path, such as `/example/path` or `/{dynamic}/path`
 */
internal class PathTree {
    sealed class Node(val name: String, var method: PageMethod?) {
        private val children = mutableListOf<Node>()

        protected open fun matches(name: String): Boolean { return this.name == name }

        fun createChild(pathPart: String, method: PageMethod?): Node {
            val node = if (pathPart.startsWith('{') && pathPart.endsWith('}')) {
                DynamicNode(pathPart.substring(1, pathPart.length - 1), method)
            } else {
                StaticNode(pathPart, method)
            }
            children.add(node)
            return node
        }

        fun findChild(pathPart: String): Node? = children.firstOrNull { it.matches(pathPart) }
    }

    class RootNode : Node("", null)
    class StaticNode(name: String, method: PageMethod?) : Node(name, method)
    class DynamicNode(name: String, method: PageMethod?) : Node(name, method) {
        override fun matches(name: String) = true // Dynamic nodes eat all possible inputs
    }

    private class ResolvedEntry(val node: Node, val pathPart: String)

    private val root = RootNode()

    var errorHandler: ErrorPageMethod = { errorCode -> ErrorPage(errorCode) }

    private fun resolve(path: String): List<ResolvedEntry>? {
        val pathParts = path.split('/')

        val resolved = mutableListOf<ResolvedEntry>()
        var currNode: Node = root
        require(pathParts[0] == root.name) // Will be true if incoming path starts with '/'

        for (i in 1 until pathParts.size) {
            val pathPart = pathParts[i]
            currNode = currNode.findChild(pathPart) ?: return null
            resolved.add(ResolvedEntry(currNode, pathPart))
        }

        return resolved
    }

    /** Register [path] with this tree, or return false if it was already added. */
    fun register(path: String, method: PageMethod): Boolean {
        if (resolve(path) != null) return false

        val pathParts = path.split('/')

        var currNode: Node = root
        require(pathParts[0] == root.name) // Will be true if incoming path starts with '/'
        for (i in 1 until pathParts.size) {
            val pathPart = pathParts[i]
            currNode = currNode.findChild(pathPart)
                ?: currNode.createChild(pathPart, method.takeIf { i == pathParts.lastIndex })
        }

        return true
    }

    fun createPageData(router: Router, path: String, query: String?): PageData {
        val resolvedEntries = resolve(path)
        val pageMethod: PageMethod = resolvedEntries?.last()?.node?.method ?: @Composable { errorHandler(404) }

        val ctx = PageContext(router)

        resolvedEntries?.forEach { resolvedEntry ->
            if (resolvedEntry.node is DynamicNode) {
                ctx.mutableParams[resolvedEntry.node.name] = resolvedEntry.pathPart
            }
        }

        query?.split("&")?.forEach { param ->
            val (key, value) = param.split('=', limit = 2)
            ctx.mutableParams[key] = value
        }

        return PageData(pageMethod, ctx)
    }
}