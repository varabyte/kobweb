package com.varabyte.kobweb.gradle.application.util

internal fun String.addSuffix(suffix: String): String {
    return if (!this.endsWith(suffix)) "$this$suffix" else this
}
