package com.varabyte.kobweb.project.common

import com.varabyte.kobweb.common.text.camelCaseToKebabCase

/**
 * Convert a name, e.g. a package segment or a file name, into a URL-friendly route segment.
 *
 * This converts camelCase to kebab-case, replaces underscores with hyphens, collapses multiple hyphens into one, and
 * removes any leading or trailing hyphens. Leading and trailing underscores are dropped as a result, which is useful
 * because they are usually workarounds for names that are otherwise not allowed (e.g. `_2021` for a number or `fun_`
 * for a keyword).
 *
 * For example:
 * - `ourTeam` -> `our-team`
 * - `_2021` -> `2021`
 * - `fun_` -> `fun`
 * - `___forced__--example_--_` -> `forced-example`
 */
fun String.toRouteSegment(): String {
    return camelCaseToKebabCase().replace(Regex("[_-]+"), "-").trim('-')
}
