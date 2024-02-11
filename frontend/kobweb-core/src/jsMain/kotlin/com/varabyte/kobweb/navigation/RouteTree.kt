package com.varabyte.kobweb.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.util.kebabCaseToCamelCase
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
    val routeInfo: PageContext.RouteInfo,
)

/**
 * A tree data structure that represents a parsed route, such as `/example/path` or `/{dynamic}/path`
 */
internal class RouteTree {
    sealed class Node(val name: String, var method: PageMethod?) {
        private val _children = mutableListOf<Node>()
        val children: List<Node> = _children

        protected open fun matches(name: String): Boolean {
            return this.name == name
        }

        fun createChild(routePart: String, method: PageMethod?): Node {
            val node = if (routePart.startsWith('{') && routePart.endsWith('}')) {
                DynamicNode(routePart.substring(1, routePart.length - 1), method)
            } else {
                StaticNode(routePart, method).also { node ->
                    if (routePart.contains('-')) {
                        _children.add(ProxyStaticNode(node))
                    }
                }
            }
            _children.add(node)
            return node
        }

        open fun findChild(routePart: String): Node? =
            _children.partition { it !is ProxyStaticNode }.let { (normalNodes, proxyNodes) ->
                // Deprioritize proxy nodes in searches; user defined nodes take precedence
                return (normalNodes + proxyNodes).find { it.matches(routePart) }
            }

        /**
         * A sequence of all nodes from this node (including itself) in a breadth first order
         */
        val nodes
            get() = sequence<List<Node>> {
                val parents = mutableMapOf<Node, Node>()

                val nodeQueue = mutableListOf(this@Node)
                while (nodeQueue.isNotEmpty()) {
                    val node = nodeQueue.removeFirst()
                    if (node is ProxyStaticNode) continue

                    val nodePath = mutableListOf<Node>()
                    nodePath.add(node)
                    var parent = parents[node]
                    while (parent != null) {
                        nodePath.add(0, parent)
                        parent = parents[parent]
                    }
                    yield(nodePath)
                    node._children.forEach { child -> parents[child] = node }
                    nodeQueue.addAll(node._children)
                }
            }
    }

    class RootNode : Node("", null)

    /** A node representing a normal part of the route, such as "example" in "/example/path" */
    class StaticNode(name: String, method: PageMethod?) : Node(name, method)

    /**
     * A transient node which allows for catching alternate spellings, such as "examplepath" for "example-path"
     *
     * This node was introduced in order to migrate Kobweb to use kebab-case for routes without breaking sites in the
     * wild using legacy lowercase routes.
     *
     * This node should be considered for internal use only and should be filtered out of any APIs that return nodes to
     * the user.
     */
    class ProxyStaticNode(val targetNode: StaticNode) : Node(targetNode.name.replace("-", ""), targetNode.method) {
        init {
            check(name != targetNode.name) { "Invalid proxy node contains same name as target node ($name). Please report this issue at https://github.com/varabyte/kobweb/issues/" }
        }

        override fun matches(name: String): Boolean {
            return this.name.equals(name, ignoreCase = true)
        }

        override fun findChild(routePart: String) = targetNode.findChild(routePart)
    }

    /** A node representing a dynamic part of the route, such as "{dynamic}" in "/{dynamic}/path" */
    class DynamicNode(name: String, method: PageMethod?) : Node(name, method) {
        override fun matches(name: String) = true // Dynamic nodes eat all possible inputs
    }

    private class ResolvedEntry(val node: Node, val routePart: String)

    private val root = RootNode()

    var errorHandler: ErrorPageMethod = { errorCode -> ErrorPage(errorCode) }

    var legacyRouteRedirectStrategy: Router.LegacyRouteRedirectStrategy = Router.LegacyRouteRedirectStrategy.WARN

    /**
     * Parse a route and associate its split up parts with a [Node] instance.
     *
     * Although resolving static nodes isn't particularly interesting (their route part will always be the same as their
     * name), this is important for connecting dynamic nodes to a concrete value. It can also be useful to identify
     * proxy static nodes, to see what the route part being captured here was originally (by checking
     * `targetNode.name`).
     *
     * @return null if no matching route was found. It is possible to return en empty list if the route being resolved
     *   is "/".
     */
    private fun resolve(
        route: String,
        excludeProxyNodes: Boolean = legacyRouteRedirectStrategy == Router.LegacyRouteRedirectStrategy.DISALLOW
    ): List<ResolvedEntry>? {
        val routeParts = route.split('/')

        val resolved = mutableListOf<ResolvedEntry>()
        var currNode: Node = root
        require(routeParts[0] == root.name) // Will be true as long as incoming route starts with '/'

        for (i in 1 until routeParts.size) {
            val routePart = routeParts[i]
            currNode = currNode.findChild(routePart) ?: return null
            if (currNode is ProxyStaticNode && excludeProxyNodes) {
                return null
            }
            resolved.add(ResolvedEntry(currNode, routePart))
        }

        return resolved
    }

    /**
     * Check if a route is registered, and if so, return the route that it was registered as.
     *
     * @return the actual registered route, or null if the route is not registered (at which point, this might be a
     * 404 error).
     */
    private fun checkRoute(route: String): String? {
        require(root.children.isNotEmpty()) { "No routes were ever registered. This is unexpected and probably means no `@Page` was defined (or pages were defined in the wrong place where Kobweb couldn't discover them)."}

        require(route.startsWith('/')) { "When checking a route, it must begin with a slash. Got: \"$route\"" }
        fun List<ResolvedEntry>.toRegisteredRouteString() = "/" + joinToString("/") {
            if (it.node !is ProxyStaticNode) it.routePart else it.node.targetNode.name
        }

        val resolvedEntries = resolve(route) ?: return null
        if (resolvedEntries.lastOrNull()?.node?.method == null) return null

        val resolvedString = resolvedEntries.toRegisteredRouteString()

        if (resolvedEntries.any { it.node is ProxyStaticNode }) {
            if (legacyRouteRedirectStrategy == Router.LegacyRouteRedirectStrategy.DISALLOW) return null
            if (legacyRouteRedirectStrategy == Router.LegacyRouteRedirectStrategy.WARN) {
                console.warn("Legacy route \"$route\" is not itself registered but is being handled as \"$resolvedString\". The site owner can disable this redirect by setting `kobweb.app.legacyRouteRedirectStrategy` to `DISALLOW` in the site's build script.")
            }
        }

        return resolvedString
    }

    /**
     * Returns true if [route] was registered via the [register] method.
     *
     * Note that this method may return true for routes derived from registered routes, depending on how
     * [legacyRouteRedirectStrategy] is set. If it is set to warn, then if this method DOES return true, it
     * will emit a warning to the console.
     */
    fun isRegistered(route: String): Boolean {
        return checkRoute(route) != null
    }

    /**
     * Register [route] with this tree, or return false if it was already added.
     */
    fun register(route: String, method: PageMethod): Boolean {
        // Ignore proxy nodes during creation; they should only matter at query time.
        if (resolve(route, excludeProxyNodes = true) != null) return false

        val routeParts = route.split('/')

        var currNode: Node = root
        require(routeParts[0] == root.name) // Will be true if incoming route starts with '/'
        for (i in 1 until routeParts.size) {
            val routePart = routeParts[i]
            currNode = currNode.findChild(routePart).takeUnless { it is ProxyStaticNode }
                ?: currNode.createChild(routePart, method.takeIf { i == routeParts.lastIndex })
        }

        return true
    }

    internal fun createPageData(route: Route): PageData {
        val resolvedEntries = resolve(route.path)
        val pageMethod: PageMethod = resolvedEntries?.last()?.node?.method ?: @Composable { errorHandler(404) }

        val dynamicParams = mutableMapOf<String, String>()
        resolvedEntries?.forEach { resolvedEntry ->
            if (resolvedEntry.node is DynamicNode) {
                dynamicParams[resolvedEntry.node.name] = resolvedEntry.routePart
                if (legacyRouteRedirectStrategy != Router.LegacyRouteRedirectStrategy.DISALLOW && resolvedEntry.node.name.contains(
                        '-'
                    )
                ) {
                    // We can't be sure if the legacy version of this hyphenated string was a camelCase one (in the
                    // case of route parts generated from packages) or a lower-case one (in the case of route parts
                    // generated from filenames). That is, "example-path" now might have been either "examplepath" OR
                    // "examplePath" OR "example_path" in previous versions of Kobweb. It's not too harmful in just
                    // supporting all to be extra safe.
                    dynamicParams[resolvedEntry.node.name.replace("-", "")] = resolvedEntry.routePart
                    dynamicParams[resolvedEntry.node.name.kebabCaseToCamelCase()] = resolvedEntry.routePart
                    dynamicParams[resolvedEntry.node.name.replace('-', '_')] = resolvedEntry.routePart
                }
            }
        }
        return PageData(pageMethod, PageContext.RouteInfo(route, dynamicParams))
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
