package com.varabyte.kobweb.gradle.application.project

import com.varabyte.kobweb.common.error.KobwebException
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
}