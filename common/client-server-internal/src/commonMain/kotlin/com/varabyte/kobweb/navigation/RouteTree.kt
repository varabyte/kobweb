package com.varabyte.kobweb.navigation

import com.varabyte.kobweb.navigation.RouteTree.ResolvedEntry
import com.varabyte.kobweb.util.text.PatternMapper

/**
 * A convenience method for pulling out all dynamic values from a [RouteTree.resolve] result.
 *
 * For example, if the route "/users/{user}/posts/{post}" was registered with the [RouteTree], then calling
 * `resolve("/users/bitspittle/posts/123")!!.captureDynamicValues()` would return a map with the following entries:
 *  - "user" to "bitspittle"
 *  - "post" to "123"
 */
fun List<ResolvedEntry<*>>.captureDynamicValues(): Map<String, String> {
    val entries = this
    return buildMap {
        entries.forEach { entry ->
            if (entry.node is RouteTree.DynamicNode) {
                put(entry.node.name, entry.capturedRoutePart)
            }
        }
    }
}

fun List<ResolvedEntry<*>>.toRouteString() = "/" + joinToString("/") { it.capturedRoutePart }

/**
 * A tree data structure that represents a parsed route, such as `/example/path` or `/{dynamic}/path`
 */
class RouteTree<T> {
    sealed class Node<T>(val parent: Node<T>? = null, val name: String, var data: T?) {
        private val _children = mutableListOf<Node<T>>()
        val children: List<Node<T>> = _children

        protected open fun matches(name: String): Boolean {
            return this.name == name
        }

        fun createChild(routePart: String, data: T?): Node<T> {
            val node = if (routePart.startsWith('{') && routePart.endsWith('}')) {
                DynamicNode(this, routePart.substring(1, routePart.length - 1), data)
            } else {
                StaticNode(this, routePart, data)
            }
            _children.add(node)
            return node
        }

        fun findChild(routePart: String): Node<T>? = _children.find { it.matches(routePart) }

        /**
         * A sequence of all nodes from this node (including itself) in a breadth first order
         */
        val nodes
            get() = sequence<List<Node<T>>> {
                val nodeQueue = mutableListOf(this@Node)
                while (nodeQueue.isNotEmpty()) {
                    val node = nodeQueue.removeFirst()
                    val nodePath = mutableListOf<Node<T>>()
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

    class RootNode<T> : Node<T>(parent = null, name = "", data = null)

    sealed class ChildNode<T>(parent: Node<T>, name: String, data: T?) : Node<T>(parent, name, data)

    /** A node representing a normal part of the route, such as "example" in "/example/path" */
    class StaticNode<T>(parent: Node<T>, name: String, data: T?) : ChildNode<T>(parent, name, data)

    /** A node representing a dynamic part of the route, such as "{dynamic}" in "/{dynamic}/path" */
    class DynamicNode<T>(parent: Node<T>, name: String, data: T?) : ChildNode<T>(parent, name, data) {
        override fun matches(name: String) = true // Dynamic nodes eat all possible inputs
    }

    /**
     * Resolved entry within a route.
     *
     * For example, if the route "/a/b/c" is resolved, then there would be three resolved entries.
     *
     * In most cases, the node name and its captured route part will be the same. However, this is not true for dynamic
     * routes, where a route registered as "/users/{user}" when visiting "/users/bitspittle" would have a resolved entry
     * with a node name of "user" and a captured route part of "bitspittle".
     */
    class ResolvedEntry<T>(val node: Node<T>, val capturedRoutePart: String)

    private val root = RootNode<T>()

    private val redirects = mutableListOf<PatternMapper>()

    private fun resolveWithoutRedirects(route: String): List<ResolvedEntry<T>>? {
        val routeParts = route.split('/')

        val resolved = mutableListOf<ResolvedEntry<T>>()
        var currNode: Node<T> = root
        require(routeParts[0] == root.name) // Will be true as long as incoming route starts with '/'

        for (i in 1 until routeParts.size) {
            val routePart = routeParts[i]
            currNode = currNode.findChild(routePart) ?: return null
            resolved.add(ResolvedEntry(currNode, routePart))
        }

        return resolved.takeIf { it.isEmpty() || it.last().node.data != null }
    }

    @Suppress("NAME_SHADOWING")
    private fun resolveAllowingRedirects(route: String): List<ResolvedEntry<T>>? {
        val redirectedRoute = redirects.fold(route) { route, redirect ->
            redirect.map(route) ?: route
        }
        return resolveWithoutRedirects(redirectedRoute)
    }

    /**
     * Parse a route and associate its split up parts with [Node] instances.
     *
     * This is particularly useful for handling dynamic route parts (e.g. the "b" in a route registered like
     * "/a/{b}/c"). The returned node will contain the captured name.
     *
     * Partial routes will not get resolved! That is, "a/b" will not get resolved if there is no page associated with
     * it, even if "a/b/c" is registered.
     *
     * @param allowRedirects Set to true to allow redirects, registered by [registerRedirect], to be followed.
     *   Otherwise, the [route] passed in must be an exact match to a registered route.
     *
     * @return null if no matching route (associated with a `@Page`) was found. It is possible to return en empty list
     *   if the route being resolved is "/".
     */
    fun resolve(route: String, allowRedirects: Boolean = true): List<ResolvedEntry<T>>? {
        return if (allowRedirects) resolveAllowingRedirects(route) else resolveWithoutRedirects(route)
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
        val resolvedNodes = resolveAllowingRedirects(route) ?: return null
        return resolvedNodes.toRouteString()
    }

    /**
     * Returns true if [route] was registered via the [register] method.
     */
    fun isRegistered(route: String): Boolean {
        return checkRoute(route) != null
    }

    /**
     * Register [route] with this tree, or return false if it was already added.
     */
    fun register(route: String, data: T): Boolean {
        // Make sure the route isn't already registered.
        // Avoid considering redirects here; they should only be used at query time.
        if (resolveWithoutRedirects(route) != null) return false

        val routeParts = route.split('/')
        var currNode: Node<T> = root
        require(routeParts[0] == root.name) // Will be true if incoming route starts with '/'
        for (i in 1 until routeParts.size) {
            val routePart = routeParts[i]
            currNode = currNode.findChild(routePart) ?: currNode.createChild(
                routePart,
                data.takeIf { i == routeParts.lastIndex })
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
