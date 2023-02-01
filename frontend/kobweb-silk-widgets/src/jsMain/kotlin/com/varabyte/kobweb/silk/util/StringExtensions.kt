package com.varabyte.kobweb.silk.util

// e.g. "ExampleText" to "example_text"
internal fun String.titleCamelCaseToSnakeCase(): String {
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

    return words.joinToString("_") { it.decapitalize() }
}
