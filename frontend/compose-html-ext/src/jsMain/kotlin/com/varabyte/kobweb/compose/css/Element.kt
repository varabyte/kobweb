package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.browser.util.wrapQuotesIfNecessary
import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.kobweb.compose.css.functions.Gradient
import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/appearance
sealed interface Appearance : StylePropertyValue {
    companion object : CssGlobalValues<Appearance> {
        // CSS Basic User Interface Module Level 4 values
        val None get() = "none".unsafeCast<Appearance>()
        val Auto get() = "auto".unsafeCast<Appearance>()
        val MenuListButton get() = "menulist-button".unsafeCast<Appearance>()
        val TextField get() = "textfield".unsafeCast<Appearance>()
    }
}

fun StyleScope.appearance(appearance: Appearance) {
    property("appearance", appearance)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/content
sealed interface Content : StylePropertyValue {
    sealed interface SingleValue : Content
    sealed interface Listable : SingleValue

    companion object : CssGlobalValues<Content> {
        private fun toContent(altText: String?, values: List<Listable>) = buildString {
            if (values.isEmpty()) return@buildString
            append(values.joinToString(" "))
            if (altText != null) append(" / ${altText.wrapQuotesIfNecessary()}")
        }.unsafeCast<Content>()

        fun of(url: CSSUrl) = url.toString().unsafeCast<Listable>()
        fun of(gradient: Gradient) = gradient.toString().unsafeCast<Listable>()
        fun of(text: String) = text.wrapQuotesIfNecessary().unsafeCast<Listable>()

        fun of(url: CSSUrl, altText: String) = list(altText, of(url))
        fun of(gradient: Gradient, altText: String) = list(altText, of(gradient))
        fun of(text: String, altText: String) = list(altText, of(text))

        fun list(vararg contents: Listable) = toContent(null, contents.toList())
        fun list(altText: String, vararg contents: Listable) = toContent(altText.takeIf { it.isNotBlank() }, contents.toList())

        // Non-combinable keywords
        val None get() = "none".unsafeCast<Content>()
        val Normal get() = "normal".unsafeCast<Content>()

        // Language / position-dependent keywords
        val CloseQuote get() = "close-quote".unsafeCast<Listable>()
        val NoCloseQuote get() = "no-close-quote".unsafeCast<Listable>()
        val NoOpenQuote get() = "no-open-quote".unsafeCast<Listable>()
        val OpenQuote get() = "open-quote".unsafeCast<Listable>()
    }
}

fun StyleScope.content(content: Content) {
    property("content", content)
}

// Needed temporarily until we can remove the deprecated `vararg` version
fun StyleScope.content(content: Content.Listable) {
    // Don't cast with "as", that breaks due to our internal unsafeCasting approach
    val content: Content = content
    content(content)
}
// Remove the previous method too after removing this method
@Deprecated("Use content(Content.list(...)) instead.", ReplaceWith("content(Content.list(*contents))"))
fun StyleScope.content(vararg contents: Content.Listable) {
    content(Content.list(*contents))
}
@Deprecated("Use content(Content.list(...)) instead.", ReplaceWith("content(Content.list(altText, *contents))"))
fun StyleScope.content(altText: String, vararg contents: Content.Listable) {
    content(Content.list(altText, *contents))
}

/** Convenience function for an extremely common case, setting content to text. */
fun StyleScope.content(value: String) {
    content(Content.of(value))
}

fun CSSUrl.toContent() = Content.of(this)
fun Gradient.toContent() = Content.of(this)
