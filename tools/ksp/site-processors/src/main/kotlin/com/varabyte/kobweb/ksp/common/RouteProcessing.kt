package com.varabyte.kobweb.ksp.common

import com.google.devtools.ksp.symbol.KSFile
import com.varabyte.kobweb.ksp.util.toPackageName
import com.varabyte.kobweb.ksp.util.toRoute
import com.varabyte.kobweb.ksp.util.toSlug
import com.varabyte.kobweb.project.common.DynamicRouteSegment
import com.varabyte.kobweb.project.common.PackageName

/**
 * The full route to use (minus the slug), if this override starts with a slash, as that means the user set the full
 * route explicitly, e.g. "/d/e/f/" for "/d/e/f/other".
 */
private val RouteOverride.routeWithoutSlug: String?
    get() = value.takeIf { it.startsWith('/') }?.let { it.substringBeforeLast('/') + '/' }

/**
 * Subdirectories to append to the package's route, if this override does NOT start with a slash but contains at least
 * one, e.g. "d/e/f/" for "d/e/f/other".
 */
private val RouteOverride.routeExtra: String?
    get() = value.takeIf { !it.startsWith('/') && it.contains('/') }?.let { it.substringBeforeLast('/') + '/' }

/**
 * The slug to use instead of the file's, unless this override ends with a slash (which means "keep the file's slug"),
 * e.g. "other" for "d/e/f/other".
 */
private val RouteOverride.slug: String?
    get() = value.takeIf { !it.endsWith('/') }?.substringAfterLast('/')

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
    routeOverride: RouteOverride?,
    supportEmptyDynamicSegments: Boolean,
): String {
    val slugFromFile = toSlug()
    val routeWithoutSlug = routeOverride?.routeWithoutSlug ?: packageName.toPackageName().toRoute(packageRoot, packageMappings)
    val routeExtra = routeOverride?.routeExtra.orEmpty()
    val slug = routeOverride?.slug?.let { value ->
        val dynamicRouteSegment = DynamicRouteSegment.tryCreate(value)
        if (supportEmptyDynamicSegments && dynamicRouteSegment != null && dynamicRouteSegment.isInferred) {
            dynamicRouteSegment.withReplacedName(slugFromFile).rawValue
        } else value
    } ?: slugFromFile

    return "$routeWithoutSlug$routeExtra$slug"
}
