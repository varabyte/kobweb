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
