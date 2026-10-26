package com.varabyte.kobweb.project.common

import java.nio.file.Path
import kotlin.io.path.Path

/**
 * A fully qualified package name, e.g. "com.example.pages".
 *
 * An empty value represents the root package.
 *
 * Note that this class does not validate its value. Packages from user code are reported by KSP, which has already
 * validated them, but without any backticks (e.g. package com.example.`my-site` is reported as
 * "com.example.my-site"), so we can't hold them to Kotlin's syntax rules ourselves. Values that come from elsewhere,
 * e.g. a package configured in a Gradle block, should be checked by whatever code reads them.
 */
@JvmInline
value class PackageName(val value: String) {
    val isRoot get() = value.isEmpty()

    /**
     * Return true if this package is the same as [other] or lives somewhere underneath it.
     *
     * This compares whole package segments, so "a.b.c" is under "a.b", but "a.bc" is not.
     */
    fun isSameOrSubpackageOf(other: PackageName): Boolean {
        return other.isRoot || value == other.value || value.startsWith("${other.value}.")
    }

    /**
     * Resolve a package [shortcut] relative to this package, e.g. ".pages" -> "org.example.pages"
     *
     * See [resolveShortcut] for the full resolution rules.
     */
    fun resolve(shortcut: PackageShortcut) = PackageName(resolveShortcut(shortcut.value))

    /**
     * Resolve a fully qualified name [shortcut] relative to this package, e.g. ".components.layouts.PageLayout" ->
     * "org.example.components.layouts.PageLayout"
     *
     * See [resolveShortcut] for the full resolution rules.
     */
    fun resolve(shortcut: FqNameShortcut) = FqName(resolveShortcut(shortcut.value))

    /**
     * Given a value that is potentially a shortcut (e.g. ".pages"), resolve it relative to this package.
     *
     * For example, if this package is "org.example", then ".pages" resolves to "org.example.pages" and
     * ".components.widgets.Button" resolves to "org.example.components.widgets.Button".
     *
     * If the value doesn't start with a period, it is already absolute and will be returned as-is. Finally, a value of
     * "." by itself resolves to this package.
     */
    private fun resolveShortcut(shortcut: String): String {
        return when {
            shortcut == "." -> value
            shortcut.startsWith('.') -> if (isRoot) shortcut.removePrefix(".") else "$value$shortcut"
            else -> shortcut
        }
    }

    override fun toString() = value
}

/**
 * A package which may be written relative to some base package, e.g. ".pages", or absolutely, e.g. "com.example.pages".
 *
 * Use [PackageName.resolve] to convert this into a [PackageName].
 */
@JvmInline
value class PackageShortcut(val value: String) {
    override fun toString() = value
}

/**
 * A fully qualified name which may be written relative to some base package, e.g. ".components.layouts.PageLayout",
 * or absolutely, e.g. "com.example.components.layouts.PageLayout".
 *
 * Use [PackageName.resolve] to convert this into a [FqName].
 */
@JvmInline
value class FqNameShortcut(val value: String) {
    override fun toString() = value
}

/**
 * Convert this package into a relative path, e.g. "a.b.c" -> "a/b/c"
 */
fun PackageName.toPath(): Path = Path(value.replace('.', '/'))
