package com.varabyte.kobwebx.gradle.markdown

/**
 * Escape quote characters in strings, useful if they're going to be converted to code.
 *
 * For example, convert "Hello there" into \"Hello there\" so that Markdown text can be converted into Kotlin code like
 * `Text("\"Hello there\"")`
 * */
internal fun String.escapeQuotes() = this.replace("\"", "\\\"")

/**
 * Escape $ characters in strings, useful if they're going to be converted to code where Kotlin might interpret them as
 * the start of some expression.
 *
 * For example, convert $100 into ${'$'}100 so that Markdown text can be converted into Kotlin code like
 * `Text("${'$'}100")`
 *
 * Note that we don't convert "$" to "\$" because that approach doesn't work if the text in code is wrapped with triple
 * quotes.
 */
internal fun String.escapeDollars() = this.replace("$", "\${'$'}")
