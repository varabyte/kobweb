package com.varabyte.kobweb.ksp.common

import com.varabyte.kobweb.common.text.isSurrounded

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

    init {
        if(isOptional && !isCatchAll) {
            error("Optional dynamic segments must also be catch-all (i.e. a \"?\" suffix requires a \"...\" prefix). Got: \"$rawValue\"")
        }
    }

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
    }

}