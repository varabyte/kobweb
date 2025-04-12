package com.varabyte.kobweb.common.navigation

import com.varabyte.kobweb.common.path.invariantSeparatorsPath
import com.varabyte.kobweb.common.text.camelCaseToKebabCase
import com.varabyte.kobweb.common.text.isSurrounded
import com.varabyte.kobweb.common.text.prefixIfNot
import java.io.File

/**
 * A class which represents a route text value, e.g. "a/b/slug", with relevant utility methods.
 *
 * A route that begins with a "/" is considered absolute or otherwise relative. A route that ends with a "/" indicates
 * that the route name is unspecified and the final slug should be taken from some reference file that the route will
 * be associated with.
 */
@JvmInline
value class Route(private val routeStr: String) {
    val isDynamic get() = routeStr.split("/").any { it.isSurrounded("{", "}") }

    override fun toString() = routeStr

    /**
     * Resolve a route with a reference path to some file to generate an absolute path.
     *
     * For example, if the route is "d/e/f/slug", and the reference path is "a/b/c/filename.kt", then the resolved path
     * will be "/a/b/c/d/e/f/slug".
     *
     * If the route doesn't include an explicit slug, then it will be inferred from the reference path's filename. For
     * example, if the route is "d/e/f/" and the reference path is "a/b/c/filename.kt", then the resolved path will be
     * "/a/b/c/d/e/f/filename".
     *
     * Finally, note that the route name "index" is special, and it resolves to the empty string. For example, if the
     * route is "d/e/f/index" and the reference path is "a/b/c/filename.kt", then the resolved path will be
     * "/a/b/c/d/e/f/".
     *
     * @param referencePath A path to some file, e.g. "a/b/c/filename.kt", that the route will be resolved relative to.
     *   If the path is empty or ends with a slash, an [IllegalArgumentException] will be thrown. You should think about
     *   the file as providing context to the route, such as a `@Page("...")` annotation with a custom route inside a
     *   Kotlin source file (which would be the reference path in that case).
     */
    fun resolve(referencePath: String): String {
        @Suppress("NAME_SHADOWING") val referencePath = referencePath.invariantSeparatorsPath.prefixIfNot("/")
        require(!referencePath.endsWith('/')) {
            "Reference path should specify a file, but got a directory: $referencePath"
        }

        // TODO: Dynamic routes should be resolvable too. For example,
        //  "a/{}/c/{}/e/{}".resolve("a/b/c/d/e/filename.txt") should resolve to "/a/b/c/d/e/filename" but fail if
        //  the path being resolved doesn't match the dynamic structure of the route
        //  We've left it out for now because it's not obvious anyone needs this at this point.
        require(!isDynamic) { "Cannot (currently) resolve a dynamic route" }
        val prefix = if (routeStr.startsWith('/')) {
            routeStr.substringBeforeLast('/')
        } else {
            referencePath.substringBeforeLast('/')
        }

        val extra = if (!routeStr.startsWith('/') && '/' in routeStr) {
            "/" + routeStr.substringBeforeLast('/')
        } else {
            ""
        }

        fun String.emptyIfIndex() = if (this == "index") "" else this
        val slugFromReferencePath = File(referencePath).nameWithoutExtension.camelCaseToKebabCase()
        val slugFromRoute = routeStr.substringAfterLast('/')
        val slug = when {
            slugFromRoute.isNotEmpty() -> slugFromRoute.emptyIfIndex()
            else -> slugFromReferencePath.emptyIfIndex()
        }

        return "$prefix$extra/$slug".prefixIfNot("/")
    }
}
