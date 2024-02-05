package com.varabyte.kobweb.gradle.application.util

// e.g. "example-text" to "ExampleText"
internal fun String.kebabCaseToTitleCamelCase(): String {
    require(this.isNotBlank())
    // The suggested replacement for "capitalize" is awful
    @Suppress("DEPRECATION") return this.split("-").joinToString("") { it.capitalize() }
}

// e.g. "example-text" to "exampleText"
internal fun String.kebabCaseToCamelCase() = kebabCaseToTitleCamelCase().replaceFirstChar { it.lowercase() }
