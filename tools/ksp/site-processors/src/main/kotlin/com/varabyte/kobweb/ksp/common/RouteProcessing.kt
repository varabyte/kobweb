package com.varabyte.kobweb.ksp.common

import com.google.devtools.ksp.symbol.KSFile
import com.varabyte.kobweb.ksp.util.toPackageName
import com.varabyte.kobweb.ksp.util.toRoute
import com.varabyte.kobweb.ksp.util.toSlug
import com.varabyte.kobweb.project.common.DynamicRouteSegment
import com.varabyte.kobweb.project.common.PackageName

/**
 * Produce the final route for a declaration (e.g. a method annotated with `@Page`) inside this file.
 *
 * For example, a method annotated with `@Page` in a file named `Example.kt`
 * and in the package `pages.a.b.c` will generate a route `/a/b/c/example`.
 *
 * This method also has logic to handle dynamic route cases.
 *
 * @param packageRoot The package to the folder that's considered the parent of all files that we're converting. For
 *   example, `mysite.pages` as a route would mean `mysite.pages.user.settings` would correspond to the route
 *   `/user/settings`
 * @param packageMappings A map of package to route mappings. See [PackageName.toRoute] for more information.
 * @param routeOverride If specified, this will influence the final route. Depending on whether it starts with and/or
 *   ends with a slash, it will either replace the entire route or affect parts of it.
 * @param supportEmptyDynamicSegments Whether to support the special "{}" route segment. This is allowed on the frontend
 *   (where it captures values from the user's URL) but not on the backend.
 */
fun KSFile.toRoute(
    packageRoot: PackageName,
    packageMappings: Map<PackageName, String>,
    routeOverride: String?,
    supportEmptyDynamicSegments: Boolean,
): String {
    val slugFromFile = toSlug()
    val routeWithoutSlug = if (routeOverride != null && routeOverride.startsWith("/")) {
        // If route override starts with "/" it means the user set the full route explicitly
        routeOverride.substringBeforeLast('/') + '/'
    } else {
        packageName.toPackageName().toRoute(packageRoot, packageMappings)
    }

    val routeExtra =
        if (routeOverride != null && !routeOverride.startsWith("/") && routeOverride.contains("/")) {
            // If route override did NOT begin with slash, but contains at least one subdir, it means append
            // subdir to base route
            routeOverride.substringBeforeLast("/") + "/"
        } else {
            ""
        }

    val slug = if (routeOverride != null && routeOverride.last() != '/') {
        routeOverride.substringAfterLast("/").let { value ->
            val dynamicRouteSegment = DynamicRouteSegment.tryCreate(value)
            if (supportEmptyDynamicSegments && dynamicRouteSegment != null && dynamicRouteSegment.isInferred) {
                dynamicRouteSegment.withReplacedName(slugFromFile).rawValue
            } else value
        }
    } else {
        slugFromFile
    }

    return "$routeWithoutSlug$routeExtra$slug"
}
