package com.varabyte.kobweb.navigation

import com.varabyte.kobweb.core.RouteInfo

internal fun RouteTree<PageMethod>.createPageData(route: Route, errorPageMethod: PageMethod): PageData {
    val self = this
    val resolved = self.resolve(route.path, allowRedirects = true)
        ?: return PageData(errorPageMethod, RouteInfo(route, emptyMap()))

    val pageMethod: PageMethod = resolved.last().node.data ?: errorPageMethod
    val dynamicParams = mutableMapOf<String, String>()
    resolved.forEach { resolvedEntry ->
        if (resolvedEntry.node is RouteTree.DynamicNode) {
            dynamicParams[resolvedEntry.node.name] = resolvedEntry.capturedRouteSegment
        }
    }

    return PageData(
        pageMethod,
        // Update RouteInfo with the latest path, just in case a redirect happened
        RouteInfo(route.copy(resolved.toRouteString()), dynamicParams)
    )
}
