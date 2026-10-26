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
     * Any problem here should be treated as an error, as each one either produces a route that can never match a URL
     * or one that is almost certainly not what the user intended.
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
            if (dynamicSegment != null) {
                // Any "..." prefix and "?" suffix have already been stripped from the name, so this also catches
                // mistakes like "{rest...}" or "{.....rest}"
                if (!dynamicSegment.name.all { it.isLetterOrDigit() || it == '-' || it == '_' }) {
                    add("`$segment` is not a valid dynamic segment. $DYNAMIC_SEGMENT_FORMS")
                }
                return@forEach
            }

            if (segment.any { it == '{' || it == '}' }) {
                add("`$segment` has curly braces but is not a valid dynamic segment. $DYNAMIC_SEGMENT_FORMS To use curly braces literally, percent-encode them as `%7B` and `%7D`.")
            }
            // Kobweb matches routes against the URL path exactly as the browser reports it, which is never decoded, so
            // a route containing any of these can never match
            val invalidChars = segment.codePoints().toArray()
                .map { codePoint -> String(Character.toChars(codePoint)) }
                .filter { char -> !char.isValidInUrlPath() && char != "{" && char != "}" }
                .distinct()
            if (invalidChars.isNotEmpty()) {
                add(buildString {
                    append("`$segment` contains characters which can't be part of a URL path as is: ")
                    append(invalidChars.joinToString { char -> "${char.describe()} (`${char.percentEncode()}`)" })
                    append(". To include them in the URL, write their percent-encoded value instead.")
                })
            }
        }
    }

    override fun toString() = value

    private companion object {
        const val DYNAMIC_SEGMENT_FORMS =
            "Dynamic segments should look like `{name}`, `{name?}`, `{...name}`, or `{...name?}` (or `{}` to use the file name), where the name only contains letters, digits, `-`, and `_`."

        /**
         * Return false if the browser won't keep this character (a single code point) as is in a URL path.
         *
         * `?` and `#` start the query string and fragment, and the rest are percent-encoded by the browser (see
         * [the path percent-encode set](https://url.spec.whatwg.org/#path-percent-encode-set)), including all
         * non-ASCII characters.
         */
        fun String.isValidInUrlPath() = length == 1 && single() in '\u0021'..'\u007E' && single() !in "\"#<>?`{}"

        fun String.describe() = when {
            this == " " -> "a space"
            length == 1 && single().let { it.isWhitespace() || it.isISOControl() } -> "U+%04X".format(single().code)
            else -> "`$this`"
        }

        fun String.percentEncode() = toByteArray(Charsets.UTF_8).joinToString("") { "%%%02X".format(it) }
    }
}

/**
 * Log an error for each problem found in this override (see [RouteOverride.findProblems]).
 *
 * @param annotation The annotation this override came from, e.g. `@Page("...")`, used to describe the problem and
 *   where it is.
 */
fun RouteOverride.reportProblems(annotation: KSAnnotation, logger: KSPLogger) {
    // KSP doesn't always know where an annotation is (e.g. one on a property), in which case we point at what it's
    // attached to instead, so the message still shows a file and line
    val symbol = annotation.takeIf { it.location is FileLocation } ?: annotation.parent
    findProblems().forEach { problem ->
        logger.error("`@${annotation.shortName.asString()}(\"$this\")` is invalid. $problem", symbol)
    }
}
