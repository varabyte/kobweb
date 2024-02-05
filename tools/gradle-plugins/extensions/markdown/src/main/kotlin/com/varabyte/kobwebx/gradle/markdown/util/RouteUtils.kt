package com.varabyte.kobwebx.gradle.markdown.util

import com.varabyte.kobweb.common.text.camelCaseToKebabCase
import java.io.File

internal const val FRONTMATTER_KEY_ROUTE_OVERRIDE = "routeOverride"

// IMPORTANT! For all methods below which take a file, they must be relative to some markdown root folder.
internal object RouteUtils {
    // e.g. "a/b/c/test.md" -> "a/b/c/" and "test.md" -> ""
    // Also, Windows paths are converted to forward slashes
    private fun File.toRoutePrefix(): String {
        return parentFile?.invariantSeparatorsPath?.let { "$it/" } ?: ""
    }

    private val File.nameWithoutExtensionIfNotIndex
        get() = nameWithoutExtension.takeIf {
            !it.equals(
                "index",
                ignoreCase = true
            )
        }

    private fun File.toSlug(): String = this.nameWithoutExtensionIfNotIndex?.camelCaseToKebabCase().orEmpty()

    /**
     * Return the final route that will be generated for this markdown file.
     *
     * This final route may be partially or entirely affected by a route override set in the front matter or if a
     * `filenameToSlug` transformation algorithm has been set up in the markdown block.
     */
    fun getRoute(file: File, frontMatterData: Map<String, List<String>>): String {
        val defaultSlug = file.toSlug()
        val routeOverride = frontMatterData[FRONTMATTER_KEY_ROUTE_OVERRIDE]?.singleOrNull()
            ?.let { route -> if (route.endsWith('/')) "$route$defaultSlug" else route }
            // Front-matter instructs users to use "index" to indicate an index page, but we don't want to actually show
            // that to the user if we encounter that.
            ?.let { route -> if (route.endsWith("/index")) route.removeSuffix("index") else route }


        return if (routeOverride == null) {
            "/${file.toRoutePrefix()}$defaultSlug"
        } else {
            if (routeOverride.startsWith('/')) routeOverride else "/${file.toRoutePrefix()}$routeOverride"
        }
    }
}
