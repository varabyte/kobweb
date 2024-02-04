package com.varabyte.kobwebx.gradle.markdown.util

import java.io.File

internal const val FRONTMATTER_KEY_ROUTE_OVERRIDE = "routeOverride"

// IMPORTANT! For all methods below which take a file, they must be relative to some markdown root folder.
internal object RouteUtils {
    // e.g. "a/b/c/test.md" -> "a/b/c/" and "test.md" -> ""
    // Also, Windows paths are converted to forward slashes
    private fun File.toRoutePrefix(): String {
        return parentFile?.invariantSeparatorsPath?.let { "$it/" } ?: ""
    }

    private val File.nameWithoutExtensionIfNotIndex get() = nameWithoutExtension.takeIf { !it.equals("index", ignoreCase = true) }
    private fun File.toSlug(): String = this.nameWithoutExtensionIfNotIndex?.lowercase().orEmpty()

    /**
     * Return the final route that will be generated for this markdown file.
     *
     * This final route may be partially or entirely affected by a route override set in the frontmatter or if a
     * `filenameToSlug` transformation algorithm has been set up in the markdown block.
     */
    fun getRoute(
        file: File, frontMatterData: Map<String, List<String>>, filenameToSlug: ((String) -> String)?
    ): String {
        val defaultSlug = file.toSlug()
        val routeOverride = getPageRouteOverride(file, frontMatterData, filenameToSlug)
            // Kobweb appends the file name
            ?.let { route -> if (route.endsWith('/')) "$route$defaultSlug" else route }
            // Kobweb removes trailing "index" slugs before the final route is presented to the user, so we do too.
            ?.let { route -> if (route.endsWith("/index")) route.removeSuffix("index") else route }

        return if (routeOverride == null) {
            "/${file.toRoutePrefix()}$defaultSlug"
        } else {
            if (routeOverride.startsWith('/')) routeOverride else "/${file.toRoutePrefix()}$routeOverride"
        }
    }

    private fun getPageRouteOverride(
        file: File,
        frontMatterData: Map<String, List<String>>,
        filenameToSlug: ((String) -> String)?
    ): String? {
        return getPageRouteOverride(file, frontMatterData[FRONTMATTER_KEY_ROUTE_OVERRIDE]?.singleOrNull(), filenameToSlug)
    }

    /**
     * Get the final route override that should go into the `@Page` annotation.
     *
     * Imagine a file named "ExamplePost.md". Then...
     *
     * | routeOverride | filenameToSlug | final value        |
     * | ------------- | -------------- | ------------------ |
     * | null          | null           | null               |
     * | null          | kebab-case     | example-post       |
     * | posts/example | *              | posts/example      |
     * | posts/        | null           | posts/             |
     * | posts/        | kebab-case     | posts/example-post |
     *
     * Essentially, a valid route override that ends with a concrete slug always takes complete precedence. If no route
     * override is set at all, then the converted file-to-slug value is used. The above table lists the remaining
     * combinations.
     */
    fun getPageRouteOverride(file: File, routeOverride: String?, filenameToSlug: ((String) -> String)?): String? {
        val defaultSlug = file.toSlug()
        val slugOverride = file.nameWithoutExtensionIfNotIndex?.let { filename ->
            filenameToSlug?.invoke(filename).takeIf { it != defaultSlug }
        }

        return when {
            routeOverride == null && slugOverride != null -> slugOverride
            routeOverride != null && !routeOverride.endsWith("/") -> routeOverride
            routeOverride != null && routeOverride.endsWith("/") -> routeOverride + slugOverride.orEmpty()
            else -> null
        }
    }
}
