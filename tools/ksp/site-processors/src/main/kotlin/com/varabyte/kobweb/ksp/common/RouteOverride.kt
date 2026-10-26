package com.varabyte.kobweb.ksp.common

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSAnnotation
import com.varabyte.kobweb.project.common.DynamicRouteSegment

/**
 * A user-specified value which changes some or all of a generated route, e.g. the `"d/e/f/{}"` in `@Page("d/e/f/{}")`.
 *
 * See [KSFile.toRoute][com.varabyte.kobweb.ksp.common.toRoute] for how this is applied.
 */
@JvmInline
value class RouteOverride(val value: String) {
    /**
     * Return a description of each problem found in this override, e.g. a malformed dynamic segment like `{post`.
     *
     * These are not necessarily fatal (the override can still be applied as written), but the resulting route is
     * probably not what the user intended.
     */
    fun findProblems(): List<String> = buildList {
        // A leading slash means "start from the root" and a trailing slash means "keep the slug", so neither results
        // in an empty segment
        val segments = value.removePrefix("/").removeSuffix("/").split('/')
        if (value != "/" && segments.any { it.isEmpty() }) {
            add("It contains an empty segment.")
        }

        segments.filter { it.isNotEmpty() }.forEach { segment ->
            val dynamicSegment = DynamicRouteSegment.tryCreate(segment)
            when {
                dynamicSegment != null -> {
                    // Any "..." prefix and "?" suffix have already been stripped from the name, so leftover dots catch
                    // mistakes like "{rest...}" or "{.....rest}"
                    if (dynamicSegment.name.any { it in "{}?#." || it.isWhitespace() }) {
                        add("`$segment` is not a valid dynamic segment. $DYNAMIC_SEGMENT_FORMS")
                    }
                }
                segment.any { it == '{' || it == '}' } -> {
                    add("`$segment` has curly braces but is not a valid dynamic segment, so it will be treated as literal text. $DYNAMIC_SEGMENT_FORMS")
                }
                segment.any { it == '?' || it == '#' } -> {
                    add("`$segment` contains a `?` or `#`, which can't be part of a URL path.")
                }
                segment.any { it.isWhitespace() } -> {
                    add("`$segment` contains whitespace.")
                }
            }
        }
    }

    override fun toString() = value

    private companion object {
        const val DYNAMIC_SEGMENT_FORMS =
            "Dynamic segments should look like `{name}`, `{name?}`, `{...name}`, or `{...name?}` (or `{}` to use the file name)."
    }
}

/**
 * Log a warning for each problem found in this override (see [RouteOverride.findProblems]).
 *
 * These are warnings rather than errors so that existing sites which happen to contain one keep building.
 *
 * @param annotation The annotation this override came from, e.g. `@Page("...")`, used to describe the problem and
 *   where it is.
 */
fun RouteOverride.warnAboutProblems(annotation: KSAnnotation, logger: KSPLogger) {
    // KSP doesn't always know where an annotation is (e.g. one on a property), in which case we point at what it's
    // attached to instead, so the warning still shows a file and line
    val symbol = annotation.takeIf { it.location is FileLocation } ?: annotation.parent
    findProblems().forEach { problem ->
        logger.warn("`@${annotation.shortName.asString()}(\"$this\")` may not work as expected. $problem", symbol)
    }
}
