package com.varabyte.kobweb.ksp.common

import com.varabyte.kobweb.ksp.util.RouteUtils

/**
 * Process all incoming information to produce a final route.
 *
 * This is useful, for example, to determine the route for a method annotated with `@Page`
 *
 * For example, a method annotated with `@Page` in a file named `Example.kt`
 * and in the package `pages.a.b.c` will generate a route `/a/b/c/example`.
 *
 * This method also has logic to handle dynamic route cases.
 */
fun processRoute(
    pkg: String,
    slugFromFile: String,
    routeOverride: String?,
    qualifiedPackage: String,
    packageMappings: Map<String, String>,
    supportDynamicRoute: Boolean,
): String {
    val slugPrefix = if (routeOverride != null && routeOverride.startsWith("/")) {
        // If route override starts with "/" it means the user set the full route explicitly
        routeOverride.substringBeforeLast('/')
    } else {
        RouteUtils
            .resolve(packageMappings, pkg)
            .removePrefix(qualifiedPackage.replace('.', '/'))
    }

    val prefixExtra =
        if (routeOverride != null && !routeOverride.startsWith("/") && routeOverride.contains("/")) {
            // If route override did NOT begin with slash, but contains at least one subdir, it means append
            // subdir to base route
            "/" + routeOverride.substringBeforeLast("/")
        } else {
            ""
        }

    val slug = if (routeOverride != null && routeOverride.last() != '/') {
        routeOverride.substringAfterLast("/").let { value ->
            // {} is a special value which means infer from the current file,
            // e.g. `Slug.kt` -> `"{slug}"`
            if (!supportDynamicRoute || value != "{}") value else "{${slugFromFile}}"
        }
    } else {
        slugFromFile
    }
    return "$slugPrefix$prefixExtra/$slug"
}
