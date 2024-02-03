package com.varabyte.kobwebx.gradle.markdown.util

import com.varabyte.kobweb.common.lang.toPackageName
import java.io.File

internal const val FRONTMATTER_KEY_ROUTE_OVERRIDE = "routeOverride"

// IMPORTANT! For all methods below which take a file, they must be relative to some markdown root folder.
internal object RouteUtils {
    /** Return the final route that this file will generate, assuming no route overrides. */
    private fun getDefaultRoute(file: File): String {
        val prefix = file.parentFile
            ?.invariantSeparatorsPath
            ?.let { "$it/" }
            ?: ""

        return "/$prefix${file.nameWithoutExtension.lowercase()}"
    }

    /**
     * Return the final route that will be generated for this markdown file.
     *
     * This will differ from [getDefaultRoute] if a route override is set in the frontmatter or if a filename converter
     * is present.
     */
    fun getRoute(
        file: File, frontMatterData: Map<String, List<String>>, filenameConverter: ((String) -> String)?
    ): String {
        return getRouteOverride(file, frontMatterData, filenameConverter) ?: getDefaultRoute(file)
    }

    fun getRouteOverride(
        file: File, frontMatterData: Map<String, List<String>>, filenameConverter: ((String) -> String)?
    ): String? {
        return getRouteOverride(file, frontMatterData[FRONTMATTER_KEY_ROUTE_OVERRIDE]?.singleOrNull(), filenameConverter)
    }

    fun getRouteOverride(file: File, routeOverride: String?, filenameConverter: ((String) -> String)?): String? {
        @Suppress("NAME_SHADOWING") // Shadow with a mutable var
        var routeOverride = routeOverride
        val inputFileName = file.name
        val defaultRoute = inputFileName.lowercase()
        if ((routeOverride == null || routeOverride.endsWith('/')) && filenameConverter != null && defaultRoute != "index") {
            routeOverride = routeOverride.orEmpty() + filenameConverter.invoke(inputFileName).takeIf { it != defaultRoute }
        }
        return routeOverride
    }
}
