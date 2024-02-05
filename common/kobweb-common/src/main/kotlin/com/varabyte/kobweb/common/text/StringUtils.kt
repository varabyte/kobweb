package com.varabyte.kobweb.common.text

fun String.prefixIfNot(prefix: String) = if (this.startsWith(prefix)) this else prefix + this
fun String.suffixIfNot(suffix: String) = if (this.endsWith(suffix)) this else this + suffix
fun String.ensureSurrounded(prefix: String, suffix: String = prefix) = this.prefixIfNot(prefix).suffixIfNot(suffix)
fun String.isSurrounded(prefix: String, suffix: String = prefix) = this.startsWith(prefix) && this.endsWith(suffix)

/**
 * Split a String that is using CamelCase into separate words at the case shifting boundaries.
 *
 * For example, "ExampleText" to ["Example", "Text"]
 *
 * Note that there's special handling for acronyms, so "ExampleABC" will be converted to ["Example", "ABC"], and
 * "ABCExample" will be converted to ["ABC", "Example"] (and not ["Example", "A", "B", "C"] and ["A", "B", "C", "Example"]).
 */
fun String.splitCamelCase(): List<String> {
    val currWord = StringBuilder()
    val words = mutableListOf<String>()

    var lastIsUpper = false // Used to distinguish "E" as the place to break new words in "ABCExample"

    this.forEachIndexed { i, c ->
        val isUpper = c.isUpperCase()
        if (isUpper) {
            // Break new words when either:
            // - right before the last capital followed by a lowercase (e.g. "E" in "ABCExample")
            // - right before the first capital following a lowercase (e.g. "A" in "ExampleABC")
            if (currWord.isNotEmpty() && (!lastIsUpper || (i < this.lastIndex && this[i + 1].isLowerCase()))) {
                words.add(currWord.toString())
                currWord.clear()
            }
        }
        currWord.append(c.lowercase())
        lastIsUpper = isUpper
    }
    if (currWord.isNotEmpty()) {
        words.add(currWord.toString())
    }

    return words
}

/**
 * Convert a String for a name that is using TitleCamelCase into kebab-case.
 *
 * For example, "ExampleText" to "example-text"
 *
 * Note that there's special handling for acronyms, so "ExampleABC" will be converted to "example-abc", and "ABCExample"
 * will be converted to "abc-example" (not "example-a-b-c" and "a-b-c-example").
 */
fun String.camelCaseToKebabCase(): String = splitCamelCase().joinToString("-") { it.lowercase() }
