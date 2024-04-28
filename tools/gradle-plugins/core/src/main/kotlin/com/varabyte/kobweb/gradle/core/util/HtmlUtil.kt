package com.varabyte.kobweb.gradle.core.util

import kotlinx.html.HEAD
import kotlinx.html.TagConsumer
import kotlinx.html.stream.createHTML

object HtmlUtil {
    // Workaround for generating child nodes without the containing <head> tag
    // See: https://github.com/Kotlin/kotlinx.html/issues/228
    private inline fun <T, C : TagConsumer<T>> C.headFragment(crossinline block: HEAD.() -> Unit): T {
        HEAD(emptyMap(), this).block()
        return this.finalize()
    }

    // Use `xhtmlCompatible = true` to include a closing slash as currently kotlinx.html needs them when adding raw text.
    // See: https://github.com/Kotlin/kotlinx.html/issues/247
    /**
     * Serialize the child nodes created by [block], excluding the opening and closing `<head>` tag.
     *
     * This is useful when accumulating elements from several sources, which can then be wrapped altogether in a single
     * `<head>` tag.
     */
    fun serializeHeadContents(block: HEAD.() -> Unit): String =
        createHTML(prettyPrint = false, xhtmlCompatible = true).headFragment(block)
}
