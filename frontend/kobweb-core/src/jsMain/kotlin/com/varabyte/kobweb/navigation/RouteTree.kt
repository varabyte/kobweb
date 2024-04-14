package com.varabyte.kobweb.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.util.kebabCaseToCamelCase
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.util.text.PatternMapper
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
        companion object {
            // When true, we tweak the route name testing logic to be a bit more lenient to support the legacy way
            // Kobweb used to generate routes. This should only get set to true if `legacyRouteRedirectStrategy` is
            // enabled, which we actually encourage users to disable as soon as possible. This code will be deleted
            // before Kobweb 1.0.
            var UseLegacySearch: Boolean = false
        }

        private val _children = mutableListOf<Node>()
        val children: List<Node> = _children

        protected open fun matches(name: String): Boolean {
            return this.name == name || if (UseLegacySearch && this.name.contains('-')) {
                val isLastPart = children.isEmpty()
                if (isLastPart) {
                    // The name "example-page" would have been "examplepage" before
                    this.name.replace("-", "") == name
                } else {
                    // The name "example-page" would have been "examplePage" before
                    this.name.kebabCaseToCamelCase() == name
                }
            } else false
        }

        fun createChild(routePart: String, method: PageMethod?): Node {
            val node = if (routePart.startsWith('{') && routePart.endsWith('}')) {
                DynamicNode(this, routePart.substring(1, routePart.length - 1), method)
            } else {
                StaticNode(this, routePart, method)
            }
            _children.add(node)
            return node
        }

        fun findChild(routePart: String): Node? = _children.find { it.matches(routePart) }

        /**
         * A sequence of all nodes from this node (including itself) in a breadth first order
         */
        val nodes
            get() = sequence<List<Node>> {
                val nodeQueue = mutableListOf(this@Node)
                while (nodeQueue.isNotEmpty()) {
                    val node = nodeQueue.removeFirst()
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

    /** A node representing a dynamic part of the route, such as "{dynamic}" in "/{dynamic}/path" */
    class DynamicNode(parent: Node, name: String, method: PageMethod?) : ChildNode(parent, name, method) {
        override fun matches(name: String) = true // Dynamic nodes eat all possible inputs
    }

    private fun List<ResolvedEntry>.toRouteString() = "/" + joinToString("/") { it.capturedRoutePart }

    /**
     * Resolved entry within a route.
     *
     * For example, if the route "/a/b/c" is resolved, then there would be three resolved entries.
     *
     * In most cases, the node name and its captured route part will be the same. However, this is not true for dynamic
     * routes, where a route registered as "/users/{user}" when visiting "/users/bitspittle" would have a resolved entry
     * with a node name of "user" and a captured route part of "bitspittle".
     */
    private class ResolvedEntry(val node: Node, val capturedRoutePart: String)

    private val root = RootNode()

    private val redirects = mutableListOf<PatternMapper>()

    var errorHandler: ErrorPageMethod = { errorCode -> ErrorPage(errorCode) }

    var legacyRouteRedirectStrategy: Router.LegacyRouteRedirectStrategy = Router.LegacyRouteRedirectStrategy.WARN

    /**
     * Parse a route and associate its split up parts with [Node] instances.
     *
     * This is particularly useful for handling dynamic route parts (e.g. the "b" in a route registered like
     * "/a/{b}/c"). The returned node will contain the captured name.
     *
     * Note that this method does not handle redirect logic. Call [resolveAllowingRedirects] if you want to handle that
     * case.
     *
     * @return null if no matching route was found. It is possible to return en empty list if the route being resolved
     *   is "/".
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

    @Suppress("NAME_SHADOWING")
    private fun resolveAllowingRedirects(route: String, showLegacyWarning: Boolean): List<ResolvedEntry>? {
        val redirectedRoute = redirects.fold(route) { route, redirect ->
            redirect.map(route) ?: route
        }
        var resolvedEntries = resolve(redirectedRoute)

        if (resolvedEntries == null && legacyRouteRedirectStrategy != Router.LegacyRouteRedirectStrategy.DISALLOW) {
            Node.UseLegacySearch = true
            try {
                resolvedEntries = resolve(route)?.also { resolvedNodes ->
                    if (showLegacyWarning) {
                        console.warn("Legacy route \"$route\" is automatically being redirected to \"${resolvedNodes.toRouteString()}\". The site owner can disable this by setting `kobweb.app.legacyRouteRedirectStrategy` to `DISALLOW` in the site's build script, or they can register an explicit redirect in the `conf.yaml` file which would also make this warning go away.")
                    }
                }
            } finally {
                Node.UseLegacySearch = false
            }
        }

        return resolvedEntries
    }



    /**
     * Check if a route is registered, and if so, return the route that it was registered as.
     *
     * @return the actual registered route, or null if the route is not registered (at which point, this might be a
     * 404 error).
     */
    private fun checkRoute(route: String): String? {
        require(root.children.isNotEmpty()) { "No routes were ever registered. This is unexpected and probably means no `@Page` was defined (or pages were defined in the wrong place where Kobweb couldn't discover them)." }
        require(route.startsWith('/')) { "When checking a route, it must begin with a slash. Got: \"$route\"" }

        // Only show legacy warning when trying to actually navigate to the route (in `createPageData`).
        val resolvedNodes = resolveAllowingRedirects(route, showLegacyWarning = false) ?: return null
        return resolvedNodes.toRouteString()
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
        // Make sure the route isn't already registered.
        // Avoid considering redirects here; they should only be used at query time.
        if (resolve(route) != null) return false

        val routeParts = route.split('/')
        var currNode: Node = root
        require(routeParts[0] == root.name) // Will be true if incoming route starts with '/'
        for (i in 1 until routeParts.size) {
            val routePart = routeParts[i]
            currNode = currNode.findChild(routePart) ?: currNode.createChild(
                routePart,
                method.takeIf { i == routeParts.lastIndex })
        }

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
    fun registerRedirect(redirectRoute: String, actualRoute: String) {
        redirects.add(PatternMapper("^$redirectRoute\$", actualRoute))
    }

    internal fun createPageData(route: Route): PageData {
        val errorPageMethod = @Composable { errorHandler(404) }
        val resolvedEntries = resolveAllowingRedirects(
            route.path,
            showLegacyWarning = legacyRouteRedirectStrategy == Router.LegacyRouteRedirectStrategy.WARN
        ) ?: return PageData(
            errorPageMethod,
            PageContext.RouteInfo(route, emptyMap())
        )

        val pageMethod: PageMethod = resolvedEntries.last().node.method ?: errorPageMethod

        val dynamicParams = mutableMapOf<String, String>()
        resolvedEntries.forEach { resolvedEntry ->
            if (resolvedEntry.node is DynamicNode) {
                dynamicParams[resolvedEntry.node.name] = resolvedEntry.capturedRoutePart
                if (
                    legacyRouteRedirectStrategy != Router.LegacyRouteRedirectStrategy.DISALLOW
                    && resolvedEntry.node.name.contains('-')
                ) {
                    // We can't be sure if the legacy version of this hyphenated string was a camelCase one (in the
                    // case of route parts generated from packages) or a lower-case one (in the case of route parts
                    // generated from filenames). That is, "example-path" now might have been either "examplepath" OR
                    // "examplePath" OR "example_path" in previous versions of Kobweb. It's not too harmful in just
                    // supporting all to be extra safe.
                    dynamicParams[resolvedEntry.node.name.replace("-", "")] = resolvedEntry.capturedRoutePart
                    dynamicParams[resolvedEntry.node.name.kebabCaseToCamelCase()] = resolvedEntry.capturedRoutePart
                    dynamicParams[resolvedEntry.node.name.replace('-', '_')] = resolvedEntry.capturedRoutePart
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
