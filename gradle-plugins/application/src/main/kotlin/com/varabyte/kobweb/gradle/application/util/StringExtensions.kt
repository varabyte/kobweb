package com.varabyte.kobweb.gradle.application.util

internal fun String.addSuffix(suffix: String): String {
    return if (!this.endsWith(suffix)) "$this$suffix" else this
}

// e.g. "example-text" to "ExampleText"
internal fun String.kebabCaseToTitleCamelCase(): String {
    require(this.isNotBlank())
    return this.split("-").joinToString("") { it.capitalize() }
}
