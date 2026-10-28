package com.varabyte.kobweb.project.common

/**
 * A declaration in the user's code that's registered at some route, e.g. a page or an API method.
 *
 * @property fqn The fully qualified name of the declaration.
 * @property route The route it's registered at, e.g. "/users/{user}".
 */
interface RouteEntry {
    val fqn: String
    val route: String
}

/**
 * Report any problems that would happen when registering all of these routes into the same route tree at runtime.
 *
 * These checks mirror the rules Kobweb's runtime `RouteTree` enforces (or the cases where it would silently never call
 * a route's handler), so that the user finds out at compile time instead of when their site or server starts up.
 *
 * @param routeLabel The kind of route, used in error messages, e.g. "page" for "The page route "/about" ...".
 */
fun Iterable<RouteEntry>.assertValidRoutes(routeLabel: String, handleError: (String) -> Unit) {
    val entriesByRoute = this.groupBy { it.route }
    entriesByRoute.forEach { (route, entries) ->
        if (entries.size > 1) {
            handleError("The $routeLabel route \"$route\" was defined more than once. Used by:\n${entries.joinToString("\n") { entry -> " - ${entry.fqn}" }}")
        }
    }

    this.forEach { entry ->
        // This is easiest to hit by accident when names are inferred from different places, e.g. a package mapped to
        // "{}" under "users.user" plus a page with `@Page("{}")` in "User.kt" results in "/users/{user}/{user}"
        val repeatedNames = DynamicRouteSegment.findRepeatedNames(entry.route)
        if (repeatedNames.isNotEmpty()) {
            handleError("The $routeLabel route \"${entry.route}\" (${entry.fqn}) uses the dynamic segment name(s) ${repeatedNames.joinToString { "\"$it\"" }} more than once, so only one of the values can be read. Please rename one of them.")
        }

        val segments = entry.route.split('/')
        segments.forEachIndexed { i, segment ->
            val dynamicSegment = DynamicRouteSegment.tryCreate(segment) ?: return@forEachIndexed
            if (dynamicSegment.isInferred) {
                handleError("The $routeLabel route \"${entry.route}\" (${entry.fqn}) has a dynamic segment with no name, \"$segment\", so its value can't be read. Please give it a name.")
            }
            // Catch-all and optional segments end a route at runtime, so nothing can follow them (not even the empty
            // segment after a trailing slash)
            if ((dynamicSegment.isCatchAll || dynamicSegment.isOptional) && i < segments.lastIndex) {
                handleError("The $routeLabel route \"${entry.route}\" (${entry.fqn}) has the segment \"$segment\" before its end, but catch-all and optional segments must be the last segment of a route.")
            }
        }
    }

    reportConflictingDynamicSegments(routeLabel, handleError)

    // Collect all entries that end with an optional segment, e.g. "/a/b/c/{d?}", as we'll check if their parent dir is
    // already registered separately, e.g. "/a/b/c/". If so, that means the former route can probably drop the ?, e.g.
    // "/a/b/c/{d}", as the empty case will always be handled by the static route.
    this.filter { entry ->
        // Only the last segment in the route can be optional, so we only have to check that one
        DynamicRouteSegment.tryCreate(entry.route.substringAfterLast('/'))?.isOptional ?: false
    }.forEach { entry ->
        val parentDir = entry.route.substringBeforeLast('/') + "/"
        val finalSegment = entry.route.substringAfterLast('/')
        val matchingEntry = entriesByRoute[parentDir]?.first()
        if (matchingEntry != null) {
            handleError(
                buildString {
                    append("The $routeLabel route \"${entry.route}\" (${entry.fqn}) ends with a trailing optional segment, ")
                    append("but \"$parentDir\" (${matchingEntry.fqn}) is also registered and will always take precedence in that case. ")
                    append("To fix this issue, remove the question mark, ")
                    append("i.e. \"$finalSegment\" -> \"${finalSegment.replace("?}", "}")}\".")
                }
            )
        }
    }
}

/**
 * Report routes whose dynamic segments can't share a route tree.
 *
 * At runtime, all routes are merged into a single tree, where each node can have at most one dynamic child. So two
 * routes which share a parent but have different dynamic segments in the same position can't both be registered, e.g.
 * "/users/{id}" and "/users/{name}", or "/{a}/x" and "/{b}/y", or "/docs/{page}" and "/docs/{...rest}".
 */
private fun Iterable<RouteEntry>.reportConflictingDynamicSegments(routeLabel: String, handleError: (String) -> Unit) {
    val root = RouteNode()
    this.forEach { entry ->
        var node = root
        for (segment in entry.route.split('/')) {
            node = if (DynamicRouteSegment.tryCreate(segment) != null) {
                val dynamicChild = node.dynamicChild ?: DynamicRouteNode(segment, entry).also { node.dynamicChild = it }
                if (dynamicChild.segment != segment) {
                    handleError("The $routeLabel routes \"${dynamicChild.owner.route}\" (${dynamicChild.owner.fqn}) and \"${entry.route}\" (${entry.fqn}) use different dynamic segments in the same position (\"${dynamicChild.segment}\" and \"$segment\"), which can't both be registered. Please use the same dynamic segment in both.")
                    break
                }
                dynamicChild.node
            } else {
                node.staticChildren.getOrPut(segment) { RouteNode() }
            }
        }
    }
}

/** A node in the route tree built by [reportConflictingDynamicSegments], one per route segment. */
private class RouteNode {
    val staticChildren = mutableMapOf<String, RouteNode>()
    var dynamicChild: DynamicRouteNode? = null
}

/**
 * The single dynamic child of a [RouteNode], e.g. for "{id}", remembering the first entry that registered it.
 */
private class DynamicRouteNode(val segment: String, val owner: RouteEntry) {
    val node = RouteNode()
}
