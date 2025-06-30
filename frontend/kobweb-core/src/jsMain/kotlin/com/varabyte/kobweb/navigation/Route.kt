package com.varabyte.kobweb.navigation

import com.varabyte.kobweb.browser.uri.decodeURIComponent
import org.w3c.dom.url.URL

class RouteException(value: String) :
    Exception("Failed to create a route. Routes should be valid URLs without a leading domain. Got: $value")

/**
 * A path component to a relative or absolute URL string without a domain, e.g. "/example/path" or "example/path".
 *
 * If this route is constructed with a leading domain, e.g. "http://whatever.com" or protocol,
 * e.g. "mailto:account@site.com", it will throw an exception
 */
class Route(pathQueryAndFragment: String) {
    private val pathQueryAndFragment = normalizeSlashes(pathQueryAndFragment)

    /**
     * Create a route using piecemeal components instead of passing in a giant URL string.
     *
     * @param path The value "/a/b/c" in route "/a/b/c?p=q&s=t#xyz". The leading slash is optional.
     * @param queryParams The map { "p" = "q"; "s" = "t" } in route "/a/b/c?p=q&s=t#xyz". Can be empty to indicate no
     *   params. Additionally, map *values* can be empty to indicate key-only parameters like "?p&s"
     * @param fragment The value "xyz" in route "/a/b/c?p=q&s=t#xyz". If null, the final route will be fragment-less.
     *   Note that the empty-string fragment is a valid case which tells the browser to navigate to the top of the page.
     */
    constructor(path: String, queryParams: Map<String, String>, fragment: String?) :
        this(buildString {
            append(path)
            if (queryParams.isNotEmpty()) {
                append('?')
                append(queryParams.map { (key, value) ->
                    buildString {
                        append(key)
                        if (value.isNotEmpty()) {
                            append('=')
                            append(value)
                        }
                    }
                }.joinToString("&"))
            }
            if (fragment != null) {
                append('#')
                append(fragment)
            }
        })

    companion object {
        /**
         * Split a route into a pairing of its path part and query params / fragments part
         *
         * ```
         * "path" -> "path" to ""
         * "path?key=value" -> "path" to "?key=value"
         * "path#frag" -> "path" to "#frag"
         * "path?key=value#frag" -> "path" to "?key=value#frag"
         * "path#frag?key=value" -> "path" to "#frag?key=value"
         * ```
         */
        fun partition(pathQueryAndFragment: String): Pair<String, String> {
            val pathPart = pathQueryAndFragment.substringBefore('?').substringBefore('#')
            return pathPart to pathQueryAndFragment.removePrefix(pathPart)
        }

        /**
         * Remove any unnecessary slashes from a URL.
         *
         * For example, normalizing "https://///example.com///a//b///c//" will return "https://example.com/a/b/c/".
         *
         * This is useful because occasionally a user may add an extra slash unintentionally as a typo. Passing such a
         * string into the URL class will result in an exception at construction time.
         */
        fun normalizeSlashes(url: String): String {
            val (origin, rest) = url.split("://", limit = 2).let { splitResult ->
                if (splitResult.size == 1) "" to splitResult[0] else "${splitResult[0]}://" to splitResult[1].trimStart('/')
            }

            val (path, queryAndFragment) = partition(rest)
            return "$origin${path.replace(Regex("//+"), "/")}$queryAndFragment"
        }

        /** Returns true if a route, i.e. a path without an origin */
        fun isRoute(path: String) = tryCreate(path) != null
        fun tryCreate(path: String) = try {
            Route(path)
        } catch (_: RouteException) {
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
            URL(this.pathQueryAndFragment)
            false // If here, we have a value like "https://a/b/c", bad!
        } catch (_: Throwable) {
            true // If here, we have a value like "/a/b/c", good!
        }

        if (!isValidRoute) {
            throw RouteException(pathQueryAndFragment)
        }
    }

    private val url = URL(this.pathQueryAndFragment, "http://unused.com")

    /** The path part of the original URL (i.e. the part without query parameters or a fragment) */
    val path: String
    /** The query parameters of the original URL (decoded, in case originally URL encoded) */
    val queryParams: Map<String, String>
    /** The fragment of the original URL (decoded, in case originally URL encoded) */
    val fragment: String?

    init {
        val isAbsolute = this.pathQueryAndFragment.startsWith("/")

        // We use the URL class to avoid doing complex URL resolution logic (e.g. handling ".." and other relative
        // operations). We pass in a dummy base because without it, URL rejects relative paths.

        // Note: If this is supposed to represent a relative route, the URL pathname will still include a leading "/", so we
        // have to drop it.
        path = if (isAbsolute) url.pathname else url.pathname.drop(1).also { check(url.pathname.first() == '/') }
        queryParams = mutableMapOf<String, String>().apply {
            if (url.search.isEmpty()) return@apply
            url.search.removePrefix("?").split('&').forEach { queryParam ->
                // Handle all three params cases...
                // 1) Common: `url?key=value&...`
                // 2) No value: `url?key&...`
                // 3) Value with equal sign in it: `url?id=aj3=zk50i&...`
                val keyValue = queryParam.split('=', limit = 2)
                val key = keyValue[0]
                this[key] = keyValue.elementAtOrNull(1)?.let { decodeURIComponent(it) } ?: ""
            }
        }
        fragment = url.hash.takeIf { it.startsWith("#") }?.removePrefix("#")?.let { decodeURIComponent(it) }
    }

    /**
     * Make a copy of this Route, optionally changing one or more of its components.
     *
     * For example, `route.copy("newpath")` will create a route with a new path but keeping the same query parameters
     * and fragments as before, if any were present.
     */
    fun copy(
        path: String = this.path,
        queryParams: Map<String, String> = this.queryParams,
        fragment: String? = this.fragment,
    ): Route {
        return Route(path, queryParams, fragment)
    }

    override fun toString() = "$path${url.search}${url.hash}"
}

val Route.slug: String get() = path.substringAfterLast("/")
