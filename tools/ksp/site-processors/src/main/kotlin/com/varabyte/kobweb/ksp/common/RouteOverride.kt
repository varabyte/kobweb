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
     * The kinds of problem [findProblems] checks for.
     */
    enum class Problem {
        /** A segment is empty, e.g. `a//b`. */
        EMPTY_SEGMENT,

        /** A dynamic segment's name has characters other than letters, digits, `-`, and `_`, e.g. `{a.b}`. */
        INVALID_DYNAMIC_SEGMENT_NAME,

        /** A segment is `.` or `..`, which browsers remove from URLs. */
        RELATIVE_SEGMENT,

        /** A segment has curly braces but isn't a valid dynamic segment, e.g. `{post`. */
        STRAY_CURLY_BRACES,

        /** A segment has characters which can't be part of a URL path as is, e.g. a space. */
        INVALID_URL_CHARACTERS,

        /**
         * An inferred (`{}`), catch-all (`{...rest}`), or optional (`{example?}`) dynamic segment is somewhere other
         * than the last segment, e.g. `/dynamic/{}/route/{example}`.
         *
         * An inferred segment is named after the file, which only makes sense for the slug (this maintains the
         * general association between the file name and the slug), while catch-all and optional segments must end a
         * route. Note that a trailing slash means the file's slug comes next, so `{...rest}/` counts as well.
         */
        MISPLACED_DYNAMIC_SEGMENT,
    }

    /**
     * Return a description of each problem found in this override, e.g. a malformed dynamic segment like `{post`.
     *
     * Any problem here should be treated as an error, as each one either produces a route that can never match a URL
     * or one that is almost certainly not what the user intended.
     *
     * @param excluding Problems to not check for, e.g. because the caller applies its own rules for them.
     */
    fun findProblems(excluding: Set<Problem> = emptySet()): List<String> = buildList {
        fun Problem.isChecked() = this !in excluding

        // A leading slash means "start from the root" and a trailing slash means "keep the slug", so neither results
        // in an empty segment
        val segments = value.removePrefix("/").removeSuffix("/").split('/')
        if (Problem.EMPTY_SEGMENT.isChecked() && value != "/" && segments.any { it.isEmpty() }) {
            add("It contains an empty segment.")
        }

        segments.filter { it.isNotEmpty() }.forEach { segment ->
            val dynamicSegment = DynamicRouteSegment.tryCreate(segment)
            if (dynamicSegment != null) {
                // Any "..." prefix and "?" suffix have already been stripped from the name, so this also catches
                // mistakes like "{rest...}" or "{.....rest}"
                if (Problem.INVALID_DYNAMIC_SEGMENT_NAME.isChecked() && !dynamicSegment.name.all { it.isLetterOrDigit() || it == '-' || it == '_' }) {
                    add("`$segment` is not a valid dynamic segment. $DYNAMIC_SEGMENT_FORMS")
                }
                return@forEach
            }

            // Browsers remove these from the path (resolving them like relative directories) before Kobweb ever sees
            // it, e.g. "/a/../b" becomes "/b", so a route containing one can never match
            if (Problem.RELATIVE_SEGMENT.isChecked() && segment.replace("%2e", ".", ignoreCase = true) in listOf(".", "..")) {
                add("`$segment` is a relative path segment, which browsers remove from URLs, so the route can never match.")
            }
            if (Problem.STRAY_CURLY_BRACES.isChecked() && segment.any { it == '{' || it == '}' }) {
                add("`$segment` has curly braces but is not a valid dynamic segment. $DYNAMIC_SEGMENT_FORMS To use curly braces literally, percent-encode them as `%7B` and `%7D`.")
            }
            // Kobweb matches routes against the URL path exactly as the browser reports it, which is never decoded, so
            // a route containing any of these can never match
            if (Problem.INVALID_URL_CHARACTERS.isChecked()) {
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

        if (Problem.MISPLACED_DYNAMIC_SEGMENT.isChecked()) {
            val hasMisplacedDynamicSegment = value.substringBeforeLast("/", missingDelimiterValue = "").split("/").any { segment ->
                val dynamicSegment = DynamicRouteSegment.tryCreate(segment)
                dynamicSegment != null && (dynamicSegment.isInferred || dynamicSegment.isCatchAll || dynamicSegment.isOptional)
            }
            if (hasMisplacedDynamicSegment) {
                add("It contains an inferred, catch-all, and/or optional dynamic segment before the last segment, where they aren't allowed.")
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
         * `?` and `#` start the query string and fragment, `\` is treated as a `/` (for http and https URLs), and the
         * rest are percent-encoded by the browser (see
         * [the path percent-encode set](https://url.spec.whatwg.org/#path-percent-encode-set)), including all
         * non-ASCII characters.
         */
        fun String.isValidInUrlPath() = length == 1 && single() in '\u0021'..'\u007E' && single() !in "\"#<>?`{}\\"

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
 *
 * @return true if any problems were reported, in which case the declaration this override belongs to should be skipped.
 */
fun RouteOverride.reportProblems(annotation: KSAnnotation, logger: KSPLogger): Boolean {
    // KSP doesn't always know where an annotation is (e.g. one on a property), in which case we point at what it's
    // attached to instead, so the message still shows a file and line
    val symbol = annotation.takeIf { it.location is FileLocation } ?: annotation.parent
    val problems = findProblems()
    problems.forEach { problem ->
        logger.error("`@${annotation.shortName.asString()}(\"$this\")` is invalid. $problem", symbol)
    }
    return problems.isNotEmpty()
}
