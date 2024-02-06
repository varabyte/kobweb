package com.varabyte.kobweb.browser.util

/**
 * Convert a String for a name that is using camelCase into kebab-case.
 *
 * For example, "exampleText" to "example-text"
 *
 * Note that there's special handling for acronyms, so "exampleABC" will be converted to "example-abc" (not
 * "example-a-b-c").
 */
fun String.camelCaseToKebabCase(): String {
    require(this.isNotBlank())

    var lastIsUpper = false // Used to distinguish "E" as the place to break new words in "ABCExample"

    val str = this
    return buildString {
        str.forEachIndexed { i, c ->
            val isUpper = c.isUpperCase()
            val cFinal = if (isUpper) {
                if (this.isNotEmpty()) {
                    // Break new words when either:
                    // - right before the last capital followed by a lowercase (e.g. "E" in "ABCExample")
                    // - right before the first capital following a lowercase (e.g. "A" in "ExampleABC")
                    if (!lastIsUpper || (i < str.lastIndex && str[i + 1].isLowerCase())) {
                        append("-")
                    }
                }
                c.lowercase()
            } else c
            append(cFinal)
            lastIsUpper = isUpper
        }
    }
}

/**
 * Convert a String for a name that is using TitleCamelCase into kebab-case.
 *
 * For example, "ExampleText" to "example-text"
 *
 * Same as [camelCaseToKebabCase], there is special handling for acronyms. See those docs for examples.
 */
// Note: There's really no difference between title case and camel case when going to kebab case, but both are
// provided for symmetry with the reverse methods, and also for expressing intention clearly.
fun String.titleCamelCaseToKebabCase() = camelCaseToKebabCase()

/**
 * Convert a String for a name that is using kebab-case into camelCase.
 *
 * For example, "example-text" to "exampleText"
 *
 * This is often but NOT ALWAYS the inverse of [camelCaseToKebabCase], if there were acronyms in the original
 * text. For example, "exampleABC" will get converted to "example-abc", which, when inversed, will become "exampleAbc".
 */
fun String.kebabCaseToCamelCase(): String {
    // The suggested replacement for "decapitalize" is harder to read and not necessary here.
    @Suppress("DEPRECATION")
    return kebabCaseToTitleCamelCase().decapitalize()
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
    // The suggested replacement for "capitalize" is harder to read and not necessary here.
    @Suppress("DEPRECATION")
    return this.split("-").joinToString("") { it.capitalize() }
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
