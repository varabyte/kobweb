package com.varabyte.kobweb.ksp.util

import com.google.devtools.ksp.symbol.KSFile
import com.varabyte.kobweb.common.text.camelCaseToKebabCase
import com.varabyte.kobweb.ksp.symbol.nameWithoutExtension

object RouteUtils {
    /**
     * Perform a default transformation of any package part to a final route part.
     *
     * Note that this should only be used when a specific package mapping wasn't provided by the user (which is expected
     * in a vast majority of cases).
     *
     * @param packagePart A package part, e.g. "b" in "a.b.c"
     */
    fun packagePartToRoutePart(packagePart: String): String {
        require('.' !in packagePart) { "Invalid package part: $packagePart" }
        return packagePart.dropWhile { it == '_' }
            .dropLastWhile { it == '_' }
            .camelCaseToKebabCase()
    }


    /**
     * Given a package, e.g. "corp.ourteam.ourvalues", and some mappings, e.g. "corp.ourteam" to "our-team" and
     * "corp.ourteam.ourvalues" to "our-values"", generate a final route, e.g. "corp/our-team/our-values/".
     *
     * We also perform some additional processing on any remaining package parts that don't have mappings:
     * - convert camelCase to kebab-case
     * - remove leading underscores (because these are usually used as a workaround for numbers, e.g. _123)
     * - remove trailing underscores (because these are usually used as a workaround for reserved words, e.g. `fun_`)
     *
     * If for some reason you intentionally wanted to have a leading underscore in your URL part, the recommended
     * solution is to use a package mapping for that, e.g. "corp.internal" to "_internal"
     */
    fun resolve(packageMappings: Map<String, String>, pkg: String): String {
        // We have a bunch of potential package to URL mappings, which work on fully qualified packages, so
        // we process each part of the package separately, going back to front. An example will help here.
        //
        // If we had the following mappings:
        //
        //   site.pages.blogs._2021._12 -> 12
        //   site.pages.blogs._2021 -> 2021
        //
        // then we'd transform the following fully-qualified package by first building up a list of parts in
        // reverse. So:
        //
        //   site.pages.blogs._2021._12.tutorials
        //
        // is processed like so (* means a mapping match was found):
        //
        //   site.pages.blogs._2021._12.tutorials ---> [tutorials]
        //   site.pages.blogs._2021._12 (*)       ---> [12, tutorials]
        //   site.pages.blogs._2021 (*)           ---> [2021, 12, tutorials]
        //   site.pages.blogs                     ---> [blogs, 2021, 12, tutorials]
        //   site.pages                           ---> [pages, blogs, 2021, 12, tutorials]
        //   site                                 ---> [site, pages, blogs, 2021, 12, tutorials]
        //
        // At which point, we're done, and we can just join the final list together to a path:
        //
        //   site/pages/blogs/2021/12/tutorials
        @Suppress("NAME_SHADOWING") // Make pkg writable
        var pkg = pkg
        val transformedParts = mutableListOf<String>()
        while (pkg.isNotEmpty()) {
            val transformedPart = packageMappings[pkg] ?: packagePartToRoutePart(pkg.substringAfterLast('.'))
            transformedParts.add(0, transformedPart)
            pkg = (pkg.takeIf { it.contains('.') } ?: "").substringBeforeLast('.')
        }
        return transformedParts.joinToString("/")
    }
}

/**
 * Given some file (which likely represents an annotation's containing file), return the slug for the file.
 */
fun KSFile.toSlug() = nameWithoutExtension.camelCaseToKebabCase()
