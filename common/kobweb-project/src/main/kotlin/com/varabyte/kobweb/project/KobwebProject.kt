package com.varabyte.kobweb.project

import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kobweb.project.structure.ProjectData
import java.io.File
import java.nio.file.Path

/**
 * Construct a project that represents the Kobweb project rooted at the specified [path] and provides methods for
 * parsing it and gaining insights into the project (see: [parseData])
 *
 * @param path The path that represents the root of the Kobweb project (defaulting to the current working directory).
 *   A [KobwebException] is thrown if the path does not actually point to a Kobweb project.
 */
class KobwebProject(
    val path: Path = Path.of("")
) {
    companion object {
        /**
         * Given a value that is potentially a package shortcut (e.g. ".pages"), get its fully qualified name,
         * e.g. ".pages" -> "org.example.pages"
         */
        fun prefixQualifiedPackage(group: String, pkg: String): String {
            return when {
                pkg.startsWith('.') -> "$group$pkg"
                else -> pkg
            }
        }
    }

    val kobwebFolder = KobwebFolder.inPath(path)
        ?: throw KobwebException("Not a valid path to a Kobweb project (no .kobweb folder found): $path")

    /**
     * Parse the specified source files and return data about it.
     *
     * @param group The group of this project, e.g. "com.example.mysite"
     * @param pagesPackage The (possibly relative) path to the pages package, under which `@Page` annotations are searched
     *   for. A relative path like ".pages" will be prefixed based on the [group], i.e. `com.example.mysite.pages`
     * @param siteSources List of (Kotlin/JS) sources for the website client
     * @param apiSources List of (Kotlin/JVM) sources that define API handlers
     */
    fun parseData(
        group: String,
        pagesPackage: String,
        siteSources: List<File>,
        apiPackage: String,
        apiSources: List<File>,
    ) = ProjectData.from(group, pagesPackage, siteSources, apiPackage, apiSources)
}