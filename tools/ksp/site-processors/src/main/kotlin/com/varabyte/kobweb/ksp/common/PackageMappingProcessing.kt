package com.varabyte.kobweb.ksp.common

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFile
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
 */
class PackageRouteMapping(val pkg: PackageName, val routeSegment: String)

/**
 * Return the package mapping declared in this file, if any.
 *
 * NOTE: We visit the annotation per-file instead of directly as visiting directly doesn't let us read the package
 *
 * @return null if this file doesn't declare a package mapping, or if it's invalid (in which case a warning will
 *   have been logged).
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
    val dynamicRouteSegment = DynamicRouteSegment.tryCreate(value)
    val routeSegment = if (dynamicRouteSegment != null && dynamicRouteSegment.isInferred) {
        if (dynamicRouteSegment.isCatchAll) {
            error("Package mappings cannot be set to a catch-all dynamic route. In \"$currPackage\", got: \"$value\"")
        }
        // e.g. "{}" under a.b.pkg resolves to "{pkg}"
        dynamicRouteSegment.withReplacedName(currPackage.value.substringAfterLast('.').toRouteSegment()).rawValue
    } else value

    return PackageRouteMapping(currPackage, routeSegment)
}
