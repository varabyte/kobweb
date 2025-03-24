package com.varabyte.kobweb.project.common

object PackageUtils {
    /**
     * Given a value that is potentially a package shortcut (e.g. ".pages"), get its fully qualified name,
     * e.g. ".pages" -> "org.example.pages"
     *
     * If it is an absolute package (e.g. "org.example.pages"), it will be returned as-is.
     *
     * Finally, if the package is set to "." by itself, that will just return the group.
     */
    fun resolvePackageShortcut(group: String, pkg: String): String {
        return when {
            pkg == "." -> group
            pkg.startsWith('.') -> "$group$pkg"
            else -> pkg
        }
    }

    /**
     * Given a package (e.g. "a.b.c"), return its path (e.g. "a/b/c")
     */
    fun packageToPath(pkg: String) = pkg.replace('.', '/')
}
