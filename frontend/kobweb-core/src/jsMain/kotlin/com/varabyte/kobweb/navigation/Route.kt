package com.varabyte.kobweb.navigation

import org.w3c.dom.url.URL

class RouteException(value: String) : Exception("Failed to create route for incoming value: $value")

/**
 * A path component to a relative or absolute URL string wihtout a domain, e.g. "/example/path" or "example/path".
 *
 * If this route is constructed with a leading domain, e.g. "http://whatever.com" or protocol,
 * e.g. "mailto:account@site.com", it will throw an exception
 */
class Route(value: String) {
    companion object {
        fun isLocal(path: String) = tryCreate(path) != null
        fun tryCreate(path: String) = try {
            Route(path)
        } catch (ex: RouteException) {
            null
        }

        /**
         * Convert a [URL] to a [Route].
         *
         * This will generally succeed unless the URL is some non-http protocol, at which point a blank URL will be
         * returned.
         */
        fun fromUrl(url: URL): Route = tryCreate(url.href.removePrefix(url.origin)) ?: Route("")
    }

    init {
        // Ideally, we either have an absolute route (like "/a/b/c") or a relative route (like "a/b/c"), but we might
        // have also been fed a full URL, e.g. "https://a/b/c" or even "mailto:a@b.com"
        // We can leverage the URL class to distinguish these cases - it will throw an exception if we try to
        // construct an ungrounded URL without a base URL. In other words, we WANT an exception to happen here.
        // Otherwise, it means our incoming value has a domain which breaks the assumptions and intention of this class
        val isValidRoute = try {
            URL(value)
            false // If here, we have a value like "https://a/b/c", bad!
        }
        catch (ex: Throwable) {
            true // If here, we have a value like "/a/b/c", good!
        }

        if (!isValidRoute) {
            throw RouteException(value)
        }
    }

    private val isAbsolute = value.startsWith("/")

    // We use the URL class to avoid doing complex URL resolution logic (e.g. handling ".." and other relative
    // operations). We pass in a dummy base because without it, URL rejects relative paths.
    private val url = URL(value, "http://unused.com")

    // Note: If this is supposed to represent a relative route, the URL pathname will still include a leading "/", so we
    // have to drop it.
    val pathname get() = if (isAbsolute) url.pathname else url.pathname.drop(1).also { check(it == "/") }
    val search get() = url.search
    val hash get() = url.hash

    override fun toString() = "$pathname$search$hash"
}
