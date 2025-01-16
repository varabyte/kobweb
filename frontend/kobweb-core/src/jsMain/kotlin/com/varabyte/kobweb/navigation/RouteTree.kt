package com.varabyte.kobweb.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.PageContext

internal fun RouteTree<PageMethod>.createPageData(route: Route, errorPageContent: @Composable (errorCode: Int) -> Unit): PageData {
    val errorPageMethod = @Composable { errorPageContent(404) }
    val self = this
    val resolved = self.resolve(route.path, allowRedirects = true)
        ?: route.path
            // Backwards compatibility hack. For the longest time, Kobweb's router was designed to be unaware of the
            // site prefix, and code that called it was responsible for prepending it, for example like so:
            // `ctx.router.tryRoutingTo(BasePath.prepend("/")`.
            // However, we've seen casees where new users were surprised by this behavior -- that `Link("/")` works but
            // `ctx.router.tryRoutingTo("/")` doesn't. In fact, even as a veteran Kobweb developer myself, I found I
            // would have not assumed this to be the case.
            // So I decided to update the Router code to auto-prepend the base path if set. However, this means that
            // some projects in the wild could break. So, here, we try to detect the case where both a user and Kobweb
            // prepending the base path each, and if so, fall back to a valid route (but emit a warning).
            .takeIf { BasePath.value.isNotEmpty() }
            ?.run {
                // e.g. "/prefix/" --> "/prefix/prefix/"
                val duplicatedRoutePrefix = BasePath.value.dropLast(1) + BasePath.value
                if (route.path.startsWith(duplicatedRoutePrefix)) {
                    self.resolve(route.path.replace(duplicatedRoutePrefix, BasePath.value), allowRedirects = true)
                        .also { resolved ->
                            if (resolved != null) {
                                console.warn("Please report to the site owner: detected a case where the site's base path was prepended an extra time (`${route.path}`). `navigateTo` and `tryRoutingTo` now auto-prepend a prefix themselves, so the site owner should search their code for `BasePath.prepend` or `RoutePrefix.prepend` and remove any that have become unnecessary.")
                            }
                        }
                } else null
            } ?: return PageData(
                errorPageMethod,
                PageContext.RouteInfo(route, emptyMap())
            )

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
        PageContext.RouteInfo(
            Route(resolved.toRouteString(), route.queryParams, route.fragment),
            dynamicParams
        )
    )
}
