package com.varabyte.kobweb.navigation

import org.w3c.dom.url.URL

/**
 * A path component to a relative URL string, e.g. "/example/path".
 *
 * If the passed-in value includes a domain, e.g. "http://whatever.com", or query parameters, they will be stripped.
 */
class Route(value: String) {
    // Pass in a dummy base because without it, URL rejects relative paths
    val value = URL(value, "http://unused.com").pathname

    companion object {
        fun isLocal(path: String) = !path.contains("://") && tryCreate(path) != null
        fun tryCreate(path: String) = try {
            Route(path)
        } catch (ex: Exception) {
            null
        }
    }
}
