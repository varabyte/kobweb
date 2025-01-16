package com.varabyte.kobweb.ksp.common

import com.google.devtools.ksp.symbol.KSFile
import com.varabyte.kobweb.ksp.util.RouteUtils
import com.varabyte.kobweb.ksp.util.toSlug

/**
 * Process all incoming information to produce a final route.
 *
 * This is useful, for example, to determine the route for a method annotated with `@Page`
 *
 * For example, a method annotated with `@Page` in a file named `Example.kt`
 * and in the package `pages.a.b.c` will generate a route `/a/b/c/example`.
 *
 * This method also has logic to handle dynamic route cases.
 *
 * @param packageRoot The package to the folder that's considered the parent of all files that we're converting. For
 *   example, `mysite.pages` as a route would mean `mysite.pages.user.settings` would correspond to the route
 *   `/user/settings`
 * @param pkg The package of the file we're planning to convert into a route. This will be used unless a route override
 *   is specified.
 * @param file The file containing the target method we're converting into a route.
 * @param routeOverride If specified, this will influence the final route. Depending on whether it starts with and/or
 *   ends with a slash, it will either replace the entire route or affect parts of it.
 * @param packageMappings A map of package to route mappings. See [RouteUtils.convertPackageToRoute] for more information.
 * @param supportEmptyDynamicSegments Whether to support the special "{}" route segment. This is allowed on the frontend
 *   (where it captures values from the user's URL) but not on the backend.
 */
fun processRoute(
    packageRoot: String,
    pkg: String,
    file: KSFile,
    routeOverride: String?,
    packageMappings: Map<String, String>,
    supportEmptyDynamicSegments: Boolean,
): String {
    val slugFromFile = file.toSlug()
    val routeWithoutSlug = if (routeOverride != null && routeOverride.startsWith("/")) {
        // If route override starts with "/" it means the user set the full route explicitly
        routeOverride.substringBeforeLast('/') + '/'
    } else {
        RouteUtils.convertPackageToRoute(packageRoot, packageMappings, pkg)
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
            // {} is a special value which means infer from the current file,
            // e.g. `Slug.kt` -> `"{slug}"`
            if (!supportEmptyDynamicSegments || value != "{}") value else "{${slugFromFile}}"
        }
    } else {
        slugFromFile
    }

    return "$routeWithoutSlug$routeExtra$slug"
}
