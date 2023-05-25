package com.varabyte.kobweb.compose.util

/**
 * Convert a String for a name that is using TitleCamelCase and convert it to kebab case.
 *
 * For example, "ExampleText" to "example-text"
 */
fun String.titleCamelCaseToKebabCase(): String {
    require(this.isNotBlank())

    val currentWord = StringBuilder()
    val words = mutableListOf<String>()

    this.forEach { c ->
        if (c.isUpperCase()) {
            if (currentWord.isNotEmpty()) {
                words.add(currentWord.toString())
                currentWord.clear()
            }
        }
        currentWord.append(c)
    }
    words.add(currentWord.toString())

    return words.joinToString("-") { it.decapitalize() }
}

/**
 * Quote a CSS string (unless it is already quoted).
 *
 * CSS text content should always be surrounded by quotes, but this is a pretty subtle requirement that's easy to miss
 * and causes silent failures.
 *
 * This method will investigate a string that is intended to be used as CSS text content and wrap it with quotes (unless
 * it is already properly wrapped.
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
