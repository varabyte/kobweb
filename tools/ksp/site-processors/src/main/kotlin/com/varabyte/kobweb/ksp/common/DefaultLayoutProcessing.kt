package com.varabyte.kobweb.ksp.common

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFile
import com.varabyte.kobweb.ksp.symbol.getAnnotationsByName
import com.varabyte.kobweb.project.common.PackageUtils

// pair corresponds to (current package, override)
// in theory should only be one, but we'll return a sequence just in case

/**
 * Return any default layouts defined against the current file's package.
 *
 * This looks like:
 * ```
 * @file:Layout(".components.layouts.BlogLayout")
 *
 * package pages.blog
 * ```
 *
 * and will return `"com.example.pages.blog" to "com.example.components.layouts.BlogLayout"
 *
 * NOTE: We visit the annotation per-file instead of directly as visiting directly doesn't let us read the package
 *
 * @return The pairing of the file's package to the fqn of the layout method it contains, or null if this file has no
 *   layout annotation defined.
 */
fun getDefaultLayout(
    file: KSFile,
    projectGroup: String,
    qualifiedPackage: String,
    layoutAnnotationFqn: String,
    logger: KSPLogger,
): Pair<String, String>? {
    return file.getAnnotationsByName(layoutAnnotationFqn).singleOrNull()?.let { layoutAnnotation ->
        val currPackage = file.packageName.asString()
        if (currPackage.startsWith(qualifiedPackage)) {
            val layoutMethodFqn =
                PackageUtils.resolvePackageShortcut(projectGroup, layoutAnnotation.arguments.first().value!!.toString())

            currPackage to layoutMethodFqn
        } else {
            logger.warn(
                "Skipped over `@file:${layoutAnnotation.shortName.asString()}` annotation. It is defined under package `$currPackage` but must exist under `$qualifiedPackage`.",
                layoutAnnotation
            )
            null
        }
    }
}
