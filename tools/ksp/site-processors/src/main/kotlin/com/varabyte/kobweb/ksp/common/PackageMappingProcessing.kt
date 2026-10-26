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
 * segments are rejected, as a package is never the last part of a route.
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

    val dynamicRouteSegment = DynamicRouteSegment.tryCreate(value) ?: return value
    if (dynamicRouteSegment.isCatchAll || dynamicRouteSegment.isOptional) {
        logger.error("Package mapping for \"$this\" cannot be a catch-all or optional dynamic segment, as a package is never the last part of a route. Got: \"$value\"", symbol)
        return null
    }

    return if (dynamicRouteSegment.isInferred) {
        // e.g. "{}" under a.b.pkg resolves to "{pkg}"
        dynamicRouteSegment.withReplacedName(this.value.substringAfterLast('.').toRouteSegment()).rawValue
    } else value
}

/**
 * Return the package mapping declared in this file, if any.
 *
 * NOTE: We visit the annotation per-file instead of directly as visiting directly doesn't let us read the package
 *
 * @return null if this file doesn't declare a package mapping, or if it's invalid (in which case a warning or error
 *   will have been logged).
 */
fun KSFile.getPackageRouteMapping(
    qualifiedPagesPackage: PackageName,
    packageMappingFqn: String,
    logger: KSPLogger,
): PackageRouteMapping? {
    val packageMappingAnnotation = getAnnotationsByName(packageMappingFqn).singleOrNull() ?: return null
    val currPackage = packageName.toPackageName()
    if (!currPackage.isSameOrSubpackageOf(qualifiedPagesPackage)) {
        logger.warn(
            "Skipped over `@file:${packageMappingAnnotation.shortName.asString()}` annotation. It is defined under package `$currPackage` but must exist under `$qualifiedPagesPackage`.",
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
