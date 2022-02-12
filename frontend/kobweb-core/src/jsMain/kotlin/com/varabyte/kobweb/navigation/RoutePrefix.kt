package com.varabyte.kobweb.navigation

/**
 * Represents a prefix of a route, which is always normalized to begin and end with a slash if set.
 *
 * This class is useful to handle the cases where a Kobweb site is rooted under some known nested subfolder. Once
 * configured, Kobweb APIs can handle prepending it automatically on the user's behalf. This is useful in cases where
 * relative paths can't easily be used, e.g. using "/" as a link to go back to the home page.
 *
 * This class is essentially a singleton - it is global to the site, and, once set, it cannot be changed.
 */
interface RoutePrefix {
    /**
     * The string value of this route prefix.
     *
     * This value will always be normalized with leading and trailing slashes unless it is empty
     */
    val value: String

    /**
     * Prepend this route prefix onto some target absolute path.
     *
     * If the path is a relative path, it will be returned unchanged.
     */
    fun prepend(path: String): String

    companion object : RoutePrefix {
        fun set(value: String) {
            RoutePrefixImpl._instance = RoutePrefixImpl(value)
        }

        override val value get() = RoutePrefixImpl.instance.value
        override fun prepend(path: String): String = RoutePrefixImpl.instance.prepend(path)
    }
}

fun RoutePrefix.Companion.prependIf(condition: Boolean, path: String): String {
    return if (condition) RoutePrefix.prepend(path) else path
}

@Suppress("ObjectPropertyName") // Ignore bad naming, it's internal to this module anyway
internal class RoutePrefixImpl(value: String) : RoutePrefix {
    companion object {
        var _instance: RoutePrefixImpl? = null
            get() {
                if (field == null) {
                    field = RoutePrefixImpl("")
                }
                return field
            }
            set(value) {
                check(field == null) { "Cannot overwrite route prefix once set" }
                field = value
            }
        val instance: RoutePrefixImpl get() = _instance!!
    }

    override val value = value.takeIf { it.isNotBlank() }
        ?.let { if (it.startsWith('/')) it else "/$it" }
        ?.let { if (it.endsWith('/')) it else "$it/" }
        ?: ""

    override fun prepend(path: String): String {
        if (value.isBlank()) return path
        if (!path.startsWith("/")) return path

        return value.dropLast(1) + path
    }

    override fun toString() = value
}

