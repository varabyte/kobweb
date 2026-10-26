package com.varabyte.kobweb.project.common

private fun String.isSurrounded(prefix: String, suffix: String = prefix) = this.startsWith(prefix) && this.endsWith(suffix)

/**
 * Class which represents a dynamic segment in a route (e.g. "{example}" in "/a/{example}/z").
 */
class DynamicRouteSegment(val rawValue: String) {
    init {
        require(rawValue.isSurrounded("{", "}")) {
            "Dynamic route segments should be surrounded by curly braces. Got: \"$rawValue\""
        }
    }

    private val content = rawValue.removeSurrounding("{", "}")

    val isCatchAll get() = content.startsWith("...")
    val isOptional get() = content.endsWith("?")

    val name = content.removePrefix("...").removeSuffix("?")

    val isInferred get() = name.isEmpty()

    /**
     * Convert this dynamic route segment into a dynamic segment with a new name.
     *
     * This is a useful way to convert an inferred dynamic segment, e.g. "{}", into a named one, e.g. "{example}".
     */
    fun withReplacedName(newName: String): DynamicRouteSegment {
        return DynamicRouteSegment(buildString {
            append('{')
            if (isCatchAll) append("...")
            append(newName)
            if (isOptional) append('?')
            append('}')
        })
    }

    companion object {
        fun tryCreate(rawValue: String): DynamicRouteSegment? {
            return try {
                DynamicRouteSegment(rawValue)
            } catch (_: IllegalArgumentException) {
                null
            }
        }

        /**
         * Return any dynamic segment names used more than once in this route, e.g. "id" in "/a/{id}/b/{...id}".
         *
         * Dynamic values are read by name, so if two segments share a name, only one of their values can be read.
         */
        fun findRepeatedNames(route: String): Set<String> {
            return route.split('/')
                .mapNotNull { tryCreate(it)?.name }
                .groupingBy { it }.eachCount()
                .filterValues { it > 1 }.keys
        }
    }

}