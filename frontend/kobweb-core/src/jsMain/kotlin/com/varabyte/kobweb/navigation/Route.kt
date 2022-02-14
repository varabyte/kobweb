package com.varabyte.kobweb.navigation

import org.w3c.dom.url.URL

/**
 * A path component to a relative or absolute URL string wihtout a domain, e.g. "/example/path" or "example/path".
 *
 * If this route is constructed with a leading domain, e.g. "http://whatever.com", it will essentially be ignored as
 * this class's API won't ever expose it.
 */
class Route(value: String) {
    companion object {
        fun isLocal(path: String) = !path.contains("://") && tryCreate(path) != null
        fun tryCreate(path: String) = try {
            Route(path)
        } catch (ex: Exception) {
            null
        }
    }

    // We use the URL class to avoid doing complex URL resolution logic (e.g. handling ".." and other relative
    // operations). We pass in a dummy base because without it, URL rejects relative paths.
    private val url = URL(value, "http://unused.com")

    private val isAbsolute = value.startsWith('/') || value.contains("://")

    // Note: If this is supposed to represent a relative route, the URL pathname will still include a leading "/", so we
    // have to drop it.
    val pathname get() = if (isAbsolute) url.pathname else url.pathname.drop(1).also { check(it == "/") }
    val search get() = url.search
    val hash get() = url.hash

    fun resolve(path: String): Route {
        val resolved = URL(path, url.toString())
        return Route(resolved.toString())
    }

    override fun toString() = "$pathname$search$hash"
}
