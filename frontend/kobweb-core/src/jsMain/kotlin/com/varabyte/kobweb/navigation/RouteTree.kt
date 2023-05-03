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
 * A tree data structure that represents a parsed route, such as `/example/path` or `/{dynamic}/path`
 */
internal class RouteTree {
    sealed class Node(val name: String, var method: PageMethod?) {
        private val children = mutableListOf<Node>()

        protected open fun matches(name: String): Boolean { return this.name == name }

        fun createChild(routePart: String, method: PageMethod?): Node {
            val node = if (routePart.startsWith('{') && routePart.endsWith('}')) {
                DynamicNode(routePart.substring(1, routePart.length - 1), method)
            } else {
                StaticNode(routePart, method)
            }
            children.add(node)
            return node
        }

        fun findChild(routePart: String): Node? = children.firstOrNull { it.matches(routePart) }

        /** A sequence of all nodes from this node (including itself) in a breadth first order */
        val nodes get() = sequence<List<Node>> {
            val parents = mutableMapOf<Node, Node>()

            val nodeQueue = mutableListOf(this@Node)
            while (nodeQueue.isNotEmpty()) {
                val node = nodeQueue.removeFirst()
                val nodePath = mutableListOf<Node>()
                nodePath.add(node)
                var parent = parents[node]
                while (parent != null) {
                    nodePath.add(0, parent)
                    parent = parents[parent]
                }
                yield(nodePath)
                node.children.forEach { child -> parents[child] = node }
                nodeQueue.addAll(node.children)
            }
        }
    }

    class RootNode : Node("", null)
    class StaticNode(name: String, method: PageMethod?) : Node(name, method)
    class DynamicNode(name: String, method: PageMethod?) : Node(name, method) {
        override fun matches(name: String) = true // Dynamic nodes eat all possible inputs
    }

    private class ResolvedEntry(val node: Node, val routePart: String)

    private val root = RootNode()

    var errorHandler: ErrorPageMethod = { errorCode -> ErrorPage(errorCode) }

    /**
     * Parse a route and associate its split up parts with a [Node] instance.
     */
    private fun resolve(route: String): List<ResolvedEntry>? {
        val routeParts = route.split('/')

        val resolved = mutableListOf<ResolvedEntry>()
        var currNode: Node = root
        require(routeParts[0] == root.name) // Will be true as long as incoming route starts with '/'

        for (i in 1 until routeParts.size) {
            val routePart = routeParts[i]
            currNode = currNode.findChild(routePart) ?: return null
            resolved.add(ResolvedEntry(currNode, routePart))
        }

        return resolved
    }

    /**
     * Return true if the route was previously registered via [register], false otherwise.
     */
    fun isRegistered(route: String): Boolean {
        return resolve(route)?.last()?.node?.method != null
    }

    /**
     * Register [route] with this tree, or return false if it was already added.
     */
    fun register(route: String, method: PageMethod): Boolean {
        if (resolve(route) != null) return false

        val routeParts = route.split('/')

        var currNode: Node = root
        require(routeParts[0] == root.name) // Will be true if incoming route starts with '/'
        for (i in 1 until routeParts.size) {
            val routePart = routeParts[i]
            currNode = currNode.findChild(routePart)
                ?: currNode.createChild(routePart, method.takeIf { i == routeParts.lastIndex })
        }

        return true
    }

    internal fun createPageData(router: Router, route: Route): PageData {
        val resolvedEntries = resolve(route.path)
        val pageMethod: PageMethod = resolvedEntries?.last()?.node?.method ?: @Composable { errorHandler(404) }

        val params = mutableMapOf<String, String>()
        resolvedEntries?.forEach { resolvedEntry ->
            if (resolvedEntry.node is DynamicNode) {
                params[resolvedEntry.node.name] = resolvedEntry.routePart
            }
        }
        params.putAll(route.queryParams)

        return PageData(pageMethod, PageContext(router, route.path, params, route.fragment))
    }

    /**
     * A sequence of all nodes in this tree, in breadth first order.
     *
     * So if "/a/b/c", "/a/b/d", and "/a/x/y" are registered, this sequence will yield "/a", "/a/b/", "/a/x/", "/a/b/c",
     * "/a/b/d", and finally "a/x/y".
     *
     * The handler will be given the full path of parent nodes along with the current one, which can be used if
     * necessary to construct the full path.
     */
    val nodes get() = root.nodes
}

internal fun Iterable<RouteTree.Node>.toPathString(): String {
    return this.joinToString("/") { node -> node.name }
}
