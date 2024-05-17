package com.varabyte.kobweb.gradle.application.util

// TODO: merge with browser-ext com.varabyte.kobweb.browser.util.StringExtensions.kt?

// e.g. "example-text" to "ExampleText"
internal fun String.kebabCaseToTitleCamelCase(): String {
    require(this.isNotBlank())
    // The suggested replacement for "capitalize" is awful
    @Suppress("DEPRECATION") return this.split("-").joinToString("") { it.capitalize() }
}

// e.g. "example-text" to "exampleText"
internal fun String.kebabCaseToCamelCase() = kebabCaseToTitleCamelCase().replaceFirstChar { it.lowercase() }
