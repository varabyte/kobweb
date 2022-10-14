package com.varabyte.kobwebx.gradle.markdown

internal fun String.escapeQuotes() = this.replace("\"", "\\\"")

/** Escape $ characters in strings where otherwise Kotlin might interpret them as the start of an expression */
internal fun String.escapeDollars() = this.replace("$", "\${'$'}")
