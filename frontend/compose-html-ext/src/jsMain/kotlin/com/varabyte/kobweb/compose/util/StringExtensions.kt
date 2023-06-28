package com.varabyte.kobweb.compose.util

/**
 * Convert a String for a name that is using TitleCamelCase into kebab-case.
 *
 * For example, "ExampleText" to "example-text"
 *
 * Note that there's special handling for acronyms, so "ExampleABC" will be converted to "example-abc", and "ABCExample"
 * will be converted to "abc-example" (not "example-a-b-c" and "a-b-c-example").
 */
fun String.titleCamelCaseToKebabCase(): String {
    require(this.isNotBlank())

    val result = StringBuilder()
    var lastIsUpper = false // Used to distinguish "E" as the place to break new words in "ABCExample"

    this.forEachIndexed { i, c ->
        val isUpper = c.isUpperCase()
        if (isUpper) {
            // Break new words when either:
            // - right before the last capital followed by a lowercase (e.g. "E" in "ABCExample")
            // - right before the first capital following a lowercase (e.g. "A" in "ExampleABC")
            if (result.isNotEmpty() && (!lastIsUpper || (i < this.lastIndex && this[i + 1].isLowerCase()))) {
                result.append("-")
            }
        }
        result.append(c.lowercase())
        lastIsUpper = isUpper
    }

    return result.toString()
}

/**
 * Convert a String for a name that is using kebab-case into TitleCamelCase.
 *
 * For example, "example-text" to "ExampleText"
 *
 * This is often but NOT ALWAYS the inverse of [titleCamelCaseToKebabCase], if there were acronyms in the original
 * text. For example, "ABCExample" will get converted to "abc-example", which, when inversed, will become "AbcExample".
 */
fun String.kebabCaseToTitleCamelCase(): String {
    require(this.isNotBlank())
    // The suggested replacement for "capitalize" is awful
    @Suppress("DEPRECATION") return this.split("-").joinToString("") { it.capitalize() }
}


/**
 * Quote a CSS string (unless it is already quoted).
 *
 * CSS text content should always be surrounded by quotes, but this is a pretty subtle requirement that's easy to miss
 * and causes silent failures.
 *
 * This method will investigate a string that is intended to be used as CSS text content and wrap it with quotes (unless
 * it is already properly wrapped).
 *
 * For example:
 * * `test` -> `"test"`
 * * `"test"` -> `"test"` (unchanged)
 * * `"Is the test today?" he asked` -> `"\"Is the test today?\" he asked"`
 */
fun String.wrapQuotesIfNecessary() = if (this.length >= 2 && this.first() == '"' && this.last() == '"') {
    this
} else {
    "\"${this.replace("\"", "\\\"")}\""
}
