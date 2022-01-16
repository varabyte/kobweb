package com.varabyte.kobwebx.gradle.markdown

internal fun String.escapeQuotes() = this.replace("\"", "\\\"")
