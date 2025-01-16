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
                put(entry.node.name, entry.capturedRouteSegment)
            }
        }
    }
}

fun List<ResolvedEntry<*>>.toRouteString() = joinToString("/") { it.capturedRouteSegment }

// e.g. "{dynamic}"
private fun String.isDynamicSegment() = this.startsWith('{') && this.endsWith('}')

/**
 * A tree data structure that represents a parsed route, such as `/example/path` or `/{dynamic}/path`
 */
class RouteTree<T : Any> {
    sealed class Node<T>(val parent: Node<T>? = null, val sourceRouteSegment: String) {
        internal var _data: T? = null
        val data: T? get() = _data

        private val _staticChildren = mutableListOf<StaticNode<T>>()
        private var _dynamicChild: DynamicNode<T>? = null
        val children: List<Node<T>> get() = buildList {
            addAll(_staticChildren)
            _dynamicChild?.let { add(it) }
        }

        /**
         * The raw name for this node.
         *
         * For most nodes, it will just match the route segment which it is associated with, but for dynamic routes,
         * this will be the value with dynamic decorations (i.e. curly braces) removed.
         */
        open val name: String = sourceRouteSegment

        protected open val isRouteTerminator: Boolean = false

        /**
         * See [accepts] for more information.]
         */
        enum class AcceptResult {
            NONE,
            SINGLE,
            ALL,
        }

        /**
         * Given a list of route segments, return a [AcceptResult] indicating if this node would accept them or not.
         *
         * For a concrete example, the incoming route "/a/b/c" would get converted to ["", "a", "b", "c"]. The root node
         * should always accept the first empty string, leaving remaining children nodes to check ["a", "b", "c"].
         *
         * At this point...
         * - a static node "a" would match the first "a" and return [AcceptResult.SINGLE].
         * - a static node "z" would reject the first "a" and return [AcceptResult.NONE].
         * - a dynamic node "{dynamic}" would accept the first "a" and return [AcceptResult.SINGLE].
         * - a dynamic node "{...dynamic}" would accept everything and return [AcceptResult.ALL].
         *
         * Note that this will always be called during route resolution time (meaning all route segments are values
         * typed in by a user in a browser URL bar).
         *
         * IMPORTANT: [routeSegments] will always contain at least one element (so there is no need to waste time
         * checking if it is empty).
         */
        abstract fun accepts(routeSegments: List<String>): AcceptResult

        /**
         * Check if this node matches the given route segment.
         *
         * This method can be called either during the registration phase or at route resolution time. If the former,
         * it might contain a dynamic route segment like "{dynamic}". If the latter, it will contain the actual value.
         */
        abstract fun matches(routeSegment: String): Boolean

        fun findChild(routeSegment: String): Node<T>? {
            return children.firstOrNull { it.matches(routeSegment) }
        }

        // Callers should ensure they only call this if `findChild` would have returned null
        fun createChild(routeSegment: String): Node<T> {
            check(findChild(routeSegment) == null) { "Node.createChild called unexpectedly. Please report this issue." }

            check(!isRouteTerminator) {
                error("User attempted to register an invalid route. \"$sourceRouteSegment\" must be the last segment of the route, but it was followed by \"$routeSegment\".")
            }

            return if (routeSegment.isDynamicSegment()) {
                DynamicNode(this, routeSegment).also { _dynamicChild = it  }
            } else {
                StaticNode(this, routeSegment).also { _staticChildren.add(it) }
            }
        }

        /**
         * Resolve a route into a list of [ResolvedEntry] instances, or null if no registered route would handle it.
         *
         * This method attempts to resolve static routes first but will check dynamic routes otherwise. For example,
         * if "/a" and "/{else}" were registered, then "/a" would match the static "/a" route and "/b" would match the
         * dynamic "/{else}" route. Meanwhile, "/a/b" would not match any route and return null.
         */
        fun resolve(routeSegments: List<String>): List<ResolvedEntry<T>>? {
            val consumeResult = accepts(routeSegments)
            val (consumedPart, remainingSegments) = when (consumeResult) {
                AcceptResult.NONE -> return null
                AcceptResult.SINGLE -> routeSegments.first() to routeSegments.drop(1)
                AcceptResult.ALL -> routeSegments.joinToString("/") to emptyList()
            }

            // If here, this means we have only consumed segment of a route, e.g. the site registered "/a/b/c" but the
            // user only visited "/a/b". In this case, we should return null (which will ultimately show a 404 to
            // the user).
            if (remainingSegments.isEmpty() && data == null) return null

            // Helper function to avoid allocating a list if we can
            fun createResolvedEntry() = listOf(ResolvedEntry(this, consumedPart))
            if (remainingSegments.isEmpty()) return createResolvedEntry()

            return children.asSequence()
                .mapNotNull { it.resolve(remainingSegments) }
                .firstOrNull()
                ?.let { createResolvedEntry() + it }
        }

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
                    nodeQueue.addAll(node.children)
                }
            }
    }

    class RootNode<T> : Node<T>(parent = null, sourceRouteSegment = "") {
        override fun matches(routeSegment: String): Boolean = routeSegment.isEmpty()

        override fun accepts(routeSegments: List<String>): AcceptResult {
            check(routeSegments.first() == "")
            return AcceptResult.SINGLE
        }
    }

    sealed class ChildNode<T>(parent: Node<T>, sourceRouteSegment: String) : Node<T>(parent, sourceRouteSegment)

    /** A node representing a normal, static route segment, such as "example" in "/example/path" */
    class StaticNode<T>(parent: Node<T>, sourceRouteSegment: String) : ChildNode<T>(parent, sourceRouteSegment) {
        override fun matches(routeSegment: String): Boolean = routeSegment == sourceRouteSegment

        override fun accepts(routeSegments: List<String>): AcceptResult {
            if (matches(routeSegments.first())) return AcceptResult.SINGLE
            return AcceptResult.NONE
        }
    }

    /** A node representing a dynamic route segment, such as "{dynamic}" in "/{dynamic}/path" */
    class DynamicNode<T>(parent: Node<T>, sourceRouteSegment: String) : ChildNode<T>(parent, sourceRouteSegment) {

        /**
         * Result of parsing text like "{name}", "{...name}", or "{...name?}".
         */
        private data class Info(val name: String, val match: Match) {
            companion object {
                fun tryCreateFrom(routeSegment: String): Info? {
                    if (!routeSegment.isDynamicSegment()) return null

                    var name = routeSegment
                    name = name.removeSurrounding("{", "}")
                    val match = if (name.startsWith("...")) {
                        name = name.removePrefix("...")
                        if (name.endsWith("?")) {
                            name = name.removeSuffix("?")
                            Match.REST_OPTIONAL
                        } else {
                            Match.REST
                        }
                    } else {
                        Match.SINGLE
                    }

                    return Info(name, match)
                }
            }
        }

        private enum class Match {
            /**
             * This node matches a single segment of the route.
             *
             * For example: "/users/{user}/posts/{post}"
             *
             * This would match a URL like "/users/bitspittle/posts/123"
             */
            SINGLE,

            /**
             * This node consumes the rest of the route.
             *
             * For example: "/games/{...game-details}"
             *
             * This would match a URL like "/games/frogger", "/games/space-invaders/easy", etc. This would NOT match
             * "/games" by itself, as the node expects at least one more segments of the route.
             */
            REST,

            /**
             * Like [REST] but also match if there are no more segments of the route.
             */
            REST_OPTIONAL,
        }

        private val info = Info.tryCreateFrom(sourceRouteSegment) ?: error("Expected a dynamic route segment here, but got \"$sourceRouteSegment\"")
        override val name = info.name

        // REST match types consume the rest of the route so therefore can't have any following route segments
        override val isRouteTerminator = info.match != Match.SINGLE

        override fun matches(routeSegment: String): Boolean {
            Info.tryCreateFrom(routeSegment)?.let { info ->
                // Hack: It's kind of weird to throw an error as a side effect of a check, but it's convenient for now,
                // and this method is only used internally. We can revisit if this becomes a problem later.
                // During the registration phase, we should reject attempts to register more multiple dynamic nodes with
                // different names. In other words, "/{a}/x" and "/{a}/y" is OK, but "/{a}/x" and "/{b}/y" is not.
                // Neither is "/{a}" and "/{...a}".
                if (this.info != info) {
                    error("User is attempting to register a dynamic route that conflicts with another dynamic route already set up. \"$routeSegment\" is being registered but \"${sourceRouteSegment}\" already exists.")
                }
                return true
            }
            return false
        }

        override fun accepts(routeSegments: List<String>): AcceptResult {
            return when (info.match) {
                Match.SINGLE -> AcceptResult.SINGLE
                Match.REST -> if (routeSegments.first() != "") AcceptResult.ALL else AcceptResult.NONE
                Match.REST_OPTIONAL -> AcceptResult.ALL
            }
        }
    }

    /**
     * Resolved entry within a route.
     *
     * For example, if the route "/a/b/c" is resolved, then there would be three resolved entries.
     *
     * In most cases, the node name and its captured route segment will be the same. However, this is not true for
     * dynamic routes, where a route registered as "/users/{user}" when visiting "/users/bitspittle" would have a
     * resolved entry with a node name of "user" and a captured route segment of "bitspittle".
     */
    class ResolvedEntry<T>(val node: Node<T>, val capturedRouteSegment: String)

    private val root = RootNode<T>()

    private val redirects = mutableListOf<PatternMapper>()

    private fun resolveWithoutRedirects(route: String): List<ResolvedEntry<T>>? {
        return root.resolve(route.split('/'))
    }

    @Suppress("NAME_SHADOWING")
    private fun resolveAllowingRedirects(route: String): List<ResolvedEntry<T>>? {
        val redirectedRoute = redirects.fold(route) { route, redirect ->
            redirect.map(route) ?: route
        }
        return resolveWithoutRedirects(redirectedRoute)
    }

    /**
     * Parse a route and associate its split up segments with [Node] instances.
     *
     * This is particularly useful for handling dynamic route segments (e.g. the "b" in a route registered like
     * "/a/{b}/c"). The returned node will contain the captured name.
     *
     * Partial routes will not get resolved! That is, "/a/b" will not get resolved if there is no page associated with
     * it, even if "/a/b/c" is registered.
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
        val routeSegments = route.split('/').toMutableList()
        var currNode: Node<T> = root
        require(root.matches(routeSegments.removeFirst())) // Will be true if incoming route starts with '/'

        while (routeSegments.isNotEmpty()) {
            val nextRouteSegment = routeSegments.removeFirst()
            currNode = currNode.findChild(nextRouteSegment) ?: currNode.createChild(nextRouteSegment)
        }

        if (currNode.data != null) return false
        currNode._data = data
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
