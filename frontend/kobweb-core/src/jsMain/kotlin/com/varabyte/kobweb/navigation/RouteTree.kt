package com.varabyte.kobweb.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.PageContext

internal fun RouteTree<PageMethod>.createPageData(route: Route, errorPageContent: @Composable (errorCode: Int) -> Unit): PageData {
    val errorPageMethod = @Composable { errorPageContent(404) }
    val resolved = this.resolve(route.path, allowRedirects = true)
        ?: return PageData(
            errorPageMethod,
            PageContext.RouteInfo(route, emptyMap())
        )

    val pageMethod : PageMethod = resolved.last().node.data ?: errorPageMethod
    val dynamicParams = mutableMapOf<String, String>()
    resolved.forEach { resolvedEntry ->
        if (resolvedEntry.node is RouteTree.DynamicNode) {
            dynamicParams[resolvedEntry.node.name] = resolvedEntry.capturedRoutePart
        }
    }

    return PageData(
        pageMethod,
        // Update RouteInfo with the latest path, just in case a redirect happened
        PageContext.RouteInfo(
            Route(resolved.toRouteString(), route.queryParams, route.fragment),
            dynamicParams
        )
    )
}
