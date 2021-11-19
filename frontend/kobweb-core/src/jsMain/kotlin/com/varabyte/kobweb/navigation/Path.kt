package com.varabyte.kobweb.navigation

private const val HEX_REGEX = "[0-9A-F]"

// Matches: "/example/path", "/example/path/"
private val PATH_REGEX = Regex("""^/(([a-z0-9]|%${HEX_REGEX}${HEX_REGEX})+/?)*$""")

/**
 * A path component to a URL string. Does NOT include any query parameters.
 */
class Path(val value: String) {
    companion object {
        fun isLocal(path: String) = tryCreate(path) != null
        fun tryCreate(path: String) = try {
            Path(path)
        } catch (ex: IllegalArgumentException) {
            null
        }
    }

    init {
        require(value.matches(PATH_REGEX)) { "URL path not formatted properly: $value" }
    }
}