package com.varabyte.kobweb.gradle.core.project.common

object PackageUtils {
    /**
     * Given a value that is potentially a package shortcut (e.g. ".pages"), get its fully qualified name,
     * e.g. ".pages" -> "org.example.pages"
     */
    fun resolvePackageShortcut(group: String, pkg: String): String {
        return when {
            pkg.startsWith('.') -> "$group$pkg"
            else -> pkg
        }
    }
}
