package com.varabyte.kobweb.ksp.common

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSNode
import com.varabyte.kobweb.ksp.symbol.getAnnotationsByName
import com.varabyte.kobweb.ksp.util.getArgumentValue
import com.varabyte.kobweb.ksp.util.toPackageName
import com.varabyte.kobweb.project.common.DynamicRouteSegment
import com.varabyte.kobweb.project.common.PackageName
import com.varabyte.kobweb.project.common.toRouteSegment

/**
 * A route segment to use in place of a package's name when generating routes, as declared by a
 * `@file:PackageMapping` annotation.
 *
 * For example, `@file:PackageMapping("{user}")` in a file under `pages.users.user` means pages in that package
 * get routes like "/users/{user}/..." instead of "/users/user/...".
 *
 * @property filePath The path of the file that declared this mapping, useful for reporting conflicts.
 */
class PackageRouteMapping(val pkg: PackageName, val routeSegment: String, val filePath: String)

/**
 * Convert the value of a `@PackageMapping` annotation declared in this package into the route segment it represents.
 *
 * An inferred dynamic segment, e.g. "{}", is named after this package. Blank values, catch-all segments, and optional
 * segments are rejected, as a package is never the last part of a route, as are values that can't be part of a URL path
 * or are malformed (see [RouteOverride.findProblems]). A value containing a slash is accepted but logged as a warning,
 * since a mapping is expected to be a single route segment.
 *
 * @param symbol The node to attach any errors to, e.g. the annotation the value came from.
 *
 * @return null if the value is invalid (in which case an error will have been logged).
 */
fun PackageName.toMappedRouteSegment(value: String, logger: KSPLogger, symbol: KSNode? = null): String? {
    if (value.isBlank()) {
        logger.error("Package mapping for \"$this\" is blank, which is not supported. If you want to remove this package from your routes, move its files into the parent package instead.", symbol)
        return null
    }

    if (value.contains('/')) {
        logger.warn("Package mapping `$value` for \"$this\" contains a slash, so it will add multiple segments to routes. Package mappings are expected to be a single route segment.", symbol)
    }
    // A package mapping is essentially a route override for a single segment, so the same problems apply, except for
    // where dynamic segments can go, which is stricter here (see below), as a package is never the last part of a route
    val problems = RouteOverride(value).findProblems(excluding = setOf(RouteOverride.Problem.MISPLACED_DYNAMIC_SEGMENT))
    if (problems.isNotEmpty()) {
        problems.forEach { problem -> logger.error("Package mapping `$value` for \"$this\" is invalid. $problem", symbol) }
        return null
    }

    // In practice, a package mapping will rarely (or never) contain a slash, but it technically can, so we review each
    // segment separately. One thing we can guarantee is that no segment here will be the last part of the route -- the
    // route will always be appended to with more.
    val segments = value.split('/')
    return segments.mapIndexed { i, segment ->
        val dynamicRouteSegment = DynamicRouteSegment.tryCreate(segment) ?: return@mapIndexed segment
        if (dynamicRouteSegment.isCatchAll || dynamicRouteSegment.isOptional) {
            logger.error("Package mapping for \"$this\" cannot contain a catch-all or optional dynamic segment, as a package is never the last part of a route. Got: \"$value\"", symbol)
            return null
        }

        if (dynamicRouteSegment.isInferred) {
            // As with route overrides, only the last segment can be inferred, as otherwise multiple segments would end
            // up with the same name, e.g. "{}/{}" -> "{pkg}/{pkg}"
            if (i < segments.lastIndex) {
                logger.error("Package mapping `$value` for \"$this\" is invalid, as only its last segment can be an inferred dynamic segment (`{}`). Please name the others explicitly.", symbol)
                return null
            }
            // e.g. "{}" under a.b.pkg resolves to "{pkg}"
            dynamicRouteSegment.withReplacedName(this.value.substringAfterLast('.').toRouteSegment()).rawValue
        } else segment
    }.joinToString("/")
}

/**
 * Return the package mapping declared in this file, if any.
 *
 * NOTE: We visit the annotation per-file instead of directly as visiting directly doesn't let us read the package
 *
 * @return null if this file doesn't declare a package mapping, or if it's invalid (in which case an error will have
 *   been logged).
 */
fun KSFile.getPackageRouteMapping(
    qualifiedPagesPackage: PackageName,
    packageMappingFqn: String,
    logger: KSPLogger,
): PackageRouteMapping? {
    val packageMappingAnnotation = getAnnotationsByName(packageMappingFqn).singleOrNull() ?: return null
    val currPackage = packageName.toPackageName()
    if (!currPackage.isSameOrSubpackageOf(qualifiedPagesPackage)) {
        logger.error(
            "`@file:${packageMappingAnnotation.shortName.asString()}` is defined under package `$currPackage` but must exist under `$qualifiedPagesPackage`.",
            packageMappingAnnotation
        )
        return null
    }

    val value = packageMappingAnnotation.getArgumentValue()!!
    val routeSegment = currPackage.toMappedRouteSegment(value, logger, packageMappingAnnotation) ?: return null

    return PackageRouteMapping(currPackage, routeSegment, filePath)
}

/**
 * Collect these package mappings into a map of package to route segment.
 *
 * Since the resulting routes would depend on processing order, an error is reported for any package that is mapped by
 * more than one file.
 *
 */
fun Iterable<PackageRouteMapping>.toRouteSegmentsByPackage(logger: KSPLogger): Map<PackageName, String> {
    groupBy { it.pkg }
        .filterValues { it.size > 1 }
        .forEach { (pkg, mappings) ->
            logger.error(buildString {
                appendLine("Package \"$pkg\" is mapped by multiple files. Please remove all but one:")
                mappings.map { it.filePath }.sorted().forEach { filePath -> appendLine(" - $filePath") }
            }.trimEnd())
        }

    return associate { it.pkg to it.routeSegment }
}
