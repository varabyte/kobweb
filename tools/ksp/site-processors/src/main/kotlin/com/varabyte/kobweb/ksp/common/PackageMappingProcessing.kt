package com.varabyte.kobweb.ksp.common

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFile
import com.varabyte.kobweb.ksp.symbol.getAnnotationsByName
import com.varabyte.kobweb.ksp.util.RouteUtils

// pair corresponds to (current package, override)
// in theory should only be one, but we'll return a sequence just in case

// We visit the annotation per-file instead of directly as visiting directly doesn't let us read the package
fun getPackageMappings(
    file: KSFile,
    qualifiedPagesPackage: String,
    packageMappingFqn: String,
    logger: KSPLogger,
): Sequence<Pair<String, String>> {
    return file.getAnnotationsByName(packageMappingFqn).mapNotNull { packageMappingAnnotation ->
        val currPackage = file.packageName.asString()
        if (currPackage.startsWith(qualifiedPagesPackage)) {
            val override = packageMappingAnnotation.arguments.first().value!!.let {
                val value = it.toString()
                val dynamicRouteSegment = DynamicRouteSegment.tryCreate(value)
                if (dynamicRouteSegment != null && dynamicRouteSegment.isInferred) {
                    if (dynamicRouteSegment.isCatchAll) {
                        error("Package mappings cannot be set to a catch-all dynamic route. In \"$currPackage\", got: \"$value\"")
                    }
                    // e.g. "{}" under a.b.pkg resolves to "{pkg}"
                    dynamicRouteSegment.withReplacedName(
                        RouteUtils.packageSegmentToRouteSegment(currPackage.substringAfterLast('.'))
                    ).rawValue
                } else value
            }
            currPackage to override
        } else {
            logger.warn(
                "Skipped over `@file:${packageMappingAnnotation.shortName.asString()}` annotation. It is defined under package `$currPackage` but must exist under `$qualifiedPagesPackage`.",
                packageMappingAnnotation
            )
            null
        }
    }
}
