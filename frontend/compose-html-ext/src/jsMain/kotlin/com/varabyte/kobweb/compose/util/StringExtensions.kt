package com.varabyte.kobweb.compose.util

// e.g. "ExampleText" to "example-text"
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

// CSS text content should always be surrounded by quotes, but this is a pretty subtle requirement that's easy to miss
// and causes silent failures. The person is passing in a String so their intention is clear. Let's just quote it
// for them if they don't have it!
internal fun String.wrapQuotesIfNecessary() = if (this.length >= 2 && this.first() == '"' && this.last() == '"') {
    this
} else {
    "\"${this.replace("\"", "\\\"")}\""
}
