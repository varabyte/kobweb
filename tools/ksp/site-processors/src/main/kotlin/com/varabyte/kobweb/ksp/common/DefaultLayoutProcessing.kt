package com.varabyte.kobweb.ksp.common

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFile
import com.varabyte.kobweb.ksp.symbol.getAnnotationsByName
import com.varabyte.kobweb.ksp.util.getArgumentValue
import com.varabyte.kobweb.ksp.util.toPackageName
import com.varabyte.kobweb.project.common.FqName
import com.varabyte.kobweb.project.common.FqNameShortcut
import com.varabyte.kobweb.project.common.PackageName

/**
 * A layout that should be applied to all pages under a package (and its subpackages), as declared by a
 * `@file:Layout` annotation.
 *
 * @property filePath The path of the file that declared this layout, useful for reporting conflicts.
 */
class DefaultLayout(val pkg: PackageName, val layoutFqn: FqName, val filePath: String)

/**
 * Return any default layout defined against this file's package.
 *
 * This looks like:
 * ```
 * @file:Layout(".components.layouts.BlogLayout")
 *
 * package pages.blog
 * ```
 *
 * and will return a [DefaultLayout] associating "com.example.pages.blog" with
 * "com.example.components.layouts.BlogLayout".
 *
 * NOTE: We visit the annotation per-file instead of directly as visiting directly doesn't let us read the package
 *
 * @return null if this file has no layout annotation defined, or if it's invalid (in which case a warning will have
 *   been logged).
 */
fun KSFile.getDefaultLayout(
    projectGroup: PackageName,
    qualifiedPagesPackage: PackageName,
    layoutAnnotationFqn: String,
    logger: KSPLogger,
): DefaultLayout? {
    return getAnnotationsByName(layoutAnnotationFqn).singleOrNull()?.let { layoutAnnotation ->
        val layoutTarget = layoutAnnotation.getArgumentValue()?.takeIf { it.isNotEmpty() }
        val currPackage = packageName.toPackageName()
        if (layoutTarget == null) {
            logger.warn(
                "Skipped over `@file:${layoutAnnotation.shortName.asString()}` as it doesn't specify a target.",
                layoutAnnotation
            )
            null
        }
        else if (currPackage.isSameOrSubpackageOf(qualifiedPagesPackage)) {
            DefaultLayout(currPackage, projectGroup.resolve(FqNameShortcut(layoutTarget)), filePath)
        } else {
            logger.warn(
                "Skipped over `@file:${layoutAnnotation.shortName.asString()}`. It is defined under package `$currPackage` but must exist under `$qualifiedPagesPackage`.",
                layoutAnnotation
            )
            null
        }
    }
}

/**
 * Report an error for any package that is given a default layout by more than one file.
 *
 * Otherwise, which layout a page ends up with would depend on processing order.
 */
fun Iterable<DefaultLayout>.reportPackagesWithMultipleLayouts(logger: KSPLogger) {
    groupBy { it.pkg }
        .filterValues { it.size > 1 }
        .forEach { (pkg, layouts) ->
            logger.error(buildString {
                appendLine("Package \"$pkg\" is given a default layout by multiple files. Please remove all but one:")
                layouts.map { it.filePath }.sorted().forEach { filePath -> appendLine(" - $filePath") }
            }.trimEnd())
        }
}
