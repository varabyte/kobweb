package com.varabyte.kobweb.gradle.application.project

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

    /**
     * Given a package (potentially empty) and a name, return the fully qualified path,
     * e.g. "" + "SomeClass" -> "SomeClass"; "example.pkg" + "SomeClass" -> "example.pkg.SomeClass"
     */
    fun prefixQualifiedPath(pkg: String, name: String): String {
        return when {
            pkg.isNotEmpty() -> "$pkg.$name"
            else -> name
        }
    }
}