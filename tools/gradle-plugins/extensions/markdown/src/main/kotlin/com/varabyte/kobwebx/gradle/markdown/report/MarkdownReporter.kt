package com.varabyte.kobwebx.gradle.markdown.report

import com.varabyte.kobweb.gradle.core.util.Reporter

/**
 * Like [Reporter], but every report comes with the [MarkdownLocation] it's about.
 *
 * How to show that location (e.g. as a full path or a short one) is left to the implementation.
 */
internal interface MarkdownReporter {
    fun error(location: MarkdownLocation, message: String)
    fun warn(location: MarkdownLocation, message: String)
}

/** Returns a plain [Reporter] whose reports are all about [location]. */
internal fun MarkdownReporter.at(location: MarkdownLocation): Reporter {
    val markdownReporter = this
    return object : Reporter {
        override fun error(message: String) = markdownReporter.error(location, message)
        override fun warn(message: String) = markdownReporter.warn(location, message)
    }
}
