package com.varabyte.kobweb.common.navigation

import com.varabyte.kobweb.common.text.ensureSurrounded

/**
 * Represents a subpath under which the user's entire site is served under.
 *
 * Its value is always normalized to begin and end with a slash if set (e.g. "/prefix/").
 *
 * This class is useful to handle the cases where a Kobweb site is rooted under some known nested subfolder. Once
 * configured, Kobweb APIs can handle prepending it automatically on the user's behalf. This is useful in cases where
 * relative paths can't easily be used, e.g. using "/" from anywhere in your site as a link to go back to the home page.
 */
class BasePath(value: String) {
    val value = value.takeIf { it.isNotBlank() }?.ensureSurrounded("/") ?: ""

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
    fun prependTo(path: String): String {
        if (value.isBlank()) return path
        if (!path.startsWith("/")) return path

        return value.dropLast(1) + path
    }

    override fun toString() = value
}