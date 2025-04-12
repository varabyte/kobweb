package com.varabyte.kobweb.navigation

/**
 * Represents a subpath under which the user's entire site is served under.
 *
 * Its value is always normalized to begin and end with a slash if set (e.g. "/prefix/").
 *
 * This class is useful to handle the cases where a Kobweb site is rooted under some known nested subfolder. Once
 * configured, Kobweb APIs can handle prepending it automatically on the user's behalf. This is useful in cases where
 * relative paths can't easily be used, e.g. using "/" from anywhere in your site as a link to go back to the home page.
 *
 * This class is essentially an immutable singleton - it is global to the site, and, once set, it cannot be changed.
 */
interface BasePath {
    /**
     * The string value of this base path.
     *
     * This value will always be normalized with leading and trailing slashes unless it is empty, e.g. "/prefix/"
     */
    val value: String

    /**
     * Prepend this base path in front of some target [path], if it is absolute (i.e. starting with a slash).
     *
     * If the path is a relative path, it will be returned unchanged.
     *
     * For example, if this site's base path is "prefix", then...
     *
     * * `path == "subdir"` -> `"subdir"`
     * * `path == "/subdir"` -> `"/prefix/subdir"`
     *
     * By only working on absolute routes, we'll never accidentally prepend a base path onto a sub-route where any
     * middle parts are missing.
     */
    fun prependTo(path: String): String

    @Deprecated("Use `prependTo` instead, as this is consistent with the build script version and also is clearer.", ReplaceWith("prependTo(path)"))
    fun prepend(path: String) = prependTo(path)

    companion object : BasePath {
        fun set(value: String) {
            BasePathImpl._instance = BasePathImpl(value)
        }

        override val value get() = BasePathImpl.instance.value
        override fun prependTo(path: String): String = BasePathImpl.instance.prependTo(path)
    }
}

/** Conditionally prepend the base path only if the passed in condition is true. */
fun BasePath.Companion.prependIf(condition: Boolean, path: String): String {
    return if (condition) prependTo(path) else path
}

/** Remove the base path from some target *absolute* path (relative paths will be returned as is). */
fun BasePath.Companion.remove(path: String): String {
    // dropLast because value always ends with a slash
    return if (value.isNotEmpty()) path.removePrefix(value.dropLast(1)) else path
}

@Suppress("ObjectPropertyName") // Ignore bad naming, it's internal to this module anyway
internal class BasePathImpl(value: String) : BasePath {
    companion object {
        var _instance: BasePathImpl? = null
            get() {
                if (field == null) {
                    field = BasePathImpl("")
                }
                return field
            }
            set(value) {
                check(field == null) { "Cannot overwrite base path once set" }
                field = value
            }
        val instance: BasePathImpl get() = _instance!!
    }

    override val value = value.takeIf { it.isNotBlank() }
        ?.let { if (it.startsWith('/')) it else "/$it" }
        ?.let { if (it.endsWith('/')) it else "$it/" }
        ?: ""

    override fun prependTo(path: String): String {
        if (value.isBlank()) return path
        if (!path.startsWith("/")) return path

        return value.dropLast(1) + path
    }

    override fun toString() = value
}

