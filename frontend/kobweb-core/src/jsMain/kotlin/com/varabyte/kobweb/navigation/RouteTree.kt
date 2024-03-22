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
    sealed class Node(val parent: Node? = null, val name: String, var method: PageMethod?) {
        private val _children = mutableListOf<Node>()
        val children: List<Node> = _children

        protected open fun matches(name: String): Boolean {
            return this.name == name
        }

        fun createChild(routePart: String, method: PageMethod?, legacyRouteRedirectStrategy: Router.LegacyRouteRedirectStrategy): Node {
            val node = if (routePart.startsWith('{') && routePart.endsWith('}')) {
                DynamicNode(this, routePart.substring(1, routePart.length - 1), method)
            } else {
                StaticNode(this, routePart, method).also { node ->
                    val addRedirectNode = legacyRouteRedirectStrategy != Router.LegacyRouteRedirectStrategy.DISALLOW
                    if (addRedirectNode && routePart.contains('-')) {
                        _children.add(RedirectNode(this, node.name.replace("-", ""), node, isLegacyRedirect = true))
                    }
                }
            }
            _children.add(node)
            return node
        }

        fun createRedirect(routePart: String, targetNode: Node): Node {
            val node = RedirectNode(this, routePart, targetNode)
            _children.add(node)
            return node
        }

        open fun findChild(routePart: String): Node? =
            _children.partition { it !is RedirectNode }.let { (normalNodes, proxyNodes) ->
                // Deprioritize proxy nodes in searches; user defined nodes take precedence
                return (normalNodes + proxyNodes).find { it.matches(routePart) }
            }

        /**
         * A sequence of all nodes from this node (including itself) in a breadth first order
         */
        val nodes
            get() = sequence<List<Node>> {
                val nodeQueue = mutableListOf(this@Node)
                while (nodeQueue.isNotEmpty()) {
                    val node = nodeQueue.removeFirst()
                    if (node is RedirectNode) continue

                    val nodePath = mutableListOf<Node>()
                    nodePath.add(node)
                    var parent = node.parent
                    while (parent != null) {
                        nodePath.add(0, parent)
                        parent = parent.parent
                    }
                    yield(nodePath)
                    nodeQueue.addAll(node._children)
                }
            }
    }

    class RootNode : Node(parent = null, name = "", method = null)

    sealed class ChildNode(parent: Node, name: String, method: PageMethod?) : Node(parent, name, method)

    /** A node representing a normal part of the route, such as "example" in "/example/path" */
    class StaticNode(parent: Node, name: String, method: PageMethod?) : ChildNode(parent, name, method)

    /**
     * A transient node points at another node, useful for handling redirects.
     *
     * This node should be considered for internal use only and should be filtered out of any APIs that return nodes to
     * the user.
     */
    class RedirectNode(parent: Node, name: String, val targetNode: Node, isLegacyRedirect: Boolean = false) :
        ChildNode(parent, name, targetNode.method) {

        var isLegacyRedirect: Boolean = isLegacyRedirect
            // This can get changed if a manual redirect route is registered after a legacy redirect route was created.
            // This is kind of hacky but this code should get removed in ~6 months anyway, and the classes here are all
            // internal anyway so it's considered acceptable.
            internal set

        init {
            check(name != targetNode.name) { "Invalid proxy node contains same name as target node ($name). Please report this issue at https://github.com/varabyte/kobweb/issues/" }
        }

        override fun matches(name: String): Boolean {
            return this.name.equals(name, ignoreCase = true)
        }

        override fun findChild(routePart: String) = targetNode.findChild(routePart)
    }

    /** A node representing a dynamic part of the route, such as "{dynamic}" in "/{dynamic}/path" */
    class DynamicNode(parent: Node, name: String, method: PageMethod?) : ChildNode(parent, name, method) {
        override fun matches(name: String) = true // Dynamic nodes eat all possible inputs
    }

    private class ResolvedEntry(val node: Node)

    private fun List<ResolvedEntry>.toRouteString() = "/" + joinToString("/") { it.node.name }

    private val root = RootNode()

    var errorHandler: ErrorPageMethod = { errorCode -> ErrorPage(errorCode) }

    var legacyRouteRedirectStrategy: Router.LegacyRouteRedirectStrategy = Router.LegacyRouteRedirectStrategy.WARN

    enum class ResolveRedirectStrategy {
        EXCLUDE,
        INCLUDE,
        FOLLOW,
    }

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
    private fun resolve(route: String, resolveRedirectStrategy: ResolveRedirectStrategy): List<ResolvedEntry>? {
        val routeParts = route.split('/')

        val resolved = mutableListOf<ResolvedEntry>()
        var currNode: Node = root
        require(routeParts[0] == root.name) // Will be true as long as incoming route starts with '/'

        for (i in 1 until routeParts.size) {
            val routePart = routeParts[i]
            currNode = currNode.findChild(routePart) ?: return null

            if (currNode is RedirectNode) {
                currNode = when (resolveRedirectStrategy) {
                    ResolveRedirectStrategy.EXCLUDE -> return null
                    ResolveRedirectStrategy.INCLUDE -> currNode
                    // Our redirect node target is either a page *or* a folder. If a folder, it means it's really
                    // pointing at a special index page (with no name). If pointing at an index page, return the parent
                    // folder. In this way, a redirect pointing at "example/" will return the node associated with
                    // "example" and not the invisibly named index page.
                    ResolveRedirectStrategy.FOLLOW -> currNode.targetNode.takeIf { it.name.isNotEmpty() }
                        ?: currNode.targetNode.parent!!
                }
            }

            resolved.add(ResolvedEntry(currNode))
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

        val resolvedEntries = resolve(route, ResolveRedirectStrategy.FOLLOW) ?: return null
        if (resolvedEntries.lastOrNull()?.node?.method == null) return null

        val resolvedString = resolvedEntries.toRouteString()

        if (resolve(
                route,
                ResolveRedirectStrategy.INCLUDE
            )?.any { (it.node as? RedirectNode)?.isLegacyRedirect == true } == true
        ) {
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

    private fun register(
        routeParts: List<String>,
        createNode: Node.(String, Boolean) -> Node,
        filterNode: (Node) -> Boolean = { true }
    ) {
        var currNode: Node = root
        require(routeParts[0] == root.name) // Will be true if incoming route starts with '/'
        for (i in 1 until routeParts.size) {
            val routePart = routeParts[i]
            currNode = currNode.findChild(routePart)?.takeIf { filterNode(it) }
                ?: currNode.createNode(routePart, i == routeParts.lastIndex)
        }
    }

    /**
     * Register [route] with this tree, or return false if it was already added.
     */
    fun register(route: String, method: PageMethod): Boolean {
        // Ignore proxy nodes during creation; they should only matter at query time.
        if (resolve(route, ResolveRedirectStrategy.EXCLUDE) != null) return false

        register(
            route.split('/'),
            createNode = { routePart, isFinalNode ->
                createChild(routePart, method.takeIf { isFinalNode }, legacyRouteRedirectStrategy)
            },
            filterNode = { it !is RedirectNode }
        )

        return true
    }

    /**
     * Register an intermediate route that will immediately redirect to another route when a user tries to visit it.
     *
     * For example, say that after a team rename, we want to move "/team/old-name/" to "/team/new-name/". At this point,
     * someone would actually move to page to the new location (so content would now be hosted at "/team/new-name") and
     * then add a redirect: `registerRedirect("/team/old-name/", "/team/new-name/")`. Now, when someone tries to visit
     * "/team/old-name/", they will be immediately redirected to "/team/new-name/".
     */
    fun registerRedirect(redirectRoute: String, actualRoute: String): Boolean {
        // The target route that we are redirecting to must exist first
        val targetNode = resolve(actualRoute, ResolveRedirectStrategy.EXCLUDE)?.lastOrNull()?.node
            ?: return false

        (resolve(
            redirectRoute,
            ResolveRedirectStrategy.INCLUDE
        )?.lastOrNull()?.node as? RedirectNode)?.let { maybeLegacyRedirectNode ->
            // If a route was already automatically registered as a legacy route, let's just take over responsibility
            // for it
            maybeLegacyRedirectNode.isLegacyRedirect = false
            return true
        }

        register(
            redirectRoute.split('/'),
            createNode = { routePart, isFinalNode ->
                if (isFinalNode) {
                    createRedirect(routePart, targetNode)
                } else {
                    createChild(routePart, null, legacyRouteRedirectStrategy)
                }
            },
        )

        return true
    }

    internal fun createPageData(route: Route): PageData {
        val errorPageMethod = @Composable { errorHandler(404) }
        val resolvedEntries = resolve(route.path, ResolveRedirectStrategy.FOLLOW) ?: return PageData(
            errorPageMethod,
            PageContext.RouteInfo(route, emptyMap())
        )

        val pageMethod: PageMethod = resolvedEntries.last().node.method ?: errorPageMethod

        val dynamicParams = mutableMapOf<String, String>()
        resolvedEntries.forEach { resolvedEntry ->
            if (resolvedEntry.node is DynamicNode) {
                val routePart = resolvedEntry.node.name

                dynamicParams[resolvedEntry.node.name] = routePart
                if (legacyRouteRedirectStrategy != Router.LegacyRouteRedirectStrategy.DISALLOW && resolvedEntry.node.name.contains(
                        '-'
                    )
                ) {
                    // We can't be sure if the legacy version of this hyphenated string was a camelCase one (in the
                    // case of route parts generated from packages) or a lower-case one (in the case of route parts
                    // generated from filenames). That is, "example-path" now might have been either "examplepath" OR
                    // "examplePath" OR "example_path" in previous versions of Kobweb. It's not too harmful in just
                    // supporting all to be extra safe.
                    dynamicParams[resolvedEntry.node.name.replace("-", "")] = routePart
                    dynamicParams[resolvedEntry.node.name.kebabCaseToCamelCase()] = routePart
                    dynamicParams[resolvedEntry.node.name.replace('-', '_')] = routePart
                }
            }
        }

        return PageData(
            pageMethod,
            // Update RouteInfo with the latest path, just in case a redirect happened
            PageContext.RouteInfo(
                Route(resolvedEntries.toRouteString(), route.queryParams, route.fragment),
                dynamicParams
            )
        )
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
