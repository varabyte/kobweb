// Sealed class private constructors are useful, actually!
@file:Suppress("RedundantVisibilityModifier")

package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.browser.util.wrapQuotesIfNecessary
import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.kobweb.compose.css.functions.Gradient
import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/appearance
class Appearance private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<Appearance> {
        // CSS Basic User Interface Module Level 4 values
        val None get() = Appearance("none")
        val Auto get() = Appearance("auto")
        val MenuListButton get() = Appearance("menulist-button")
        val TextField get() = Appearance("textfield")
    }
}

fun StyleScope.appearance(appearance: Appearance) {
    property("appearance", appearance)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/content
sealed class Content private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    /** Content keywords that cannot be used in combination with any others. */
    sealed class SingleValue(value: String) : Content(value)
    private class Keyword(value: String) : SingleValue(value)

    /** Content keywords that can be used in combination with others. */
    sealed class Listable(value: String) : SingleValue(value)

    private class ListableKeyword(value: String) : Listable(value)
    private class Text(value: String) : Listable(value.wrapQuotesIfNecessary())

    private class Url(url: CSSUrl) : Listable(url.toString())
    private class Gradient(gradient: com.varabyte.kobweb.compose.css.functions.Gradient) :
        Listable(gradient.toString())

    private class ValueList(altText: String?, values: List<Listable>) : Content(
        buildString {
            if (values.isEmpty()) return@buildString
            append(values.joinToString(" "))
            if (altText != null) append(" / ${altText.wrapQuotesIfNecessary()}")
        }
    )

    companion object : CssGlobalValues<Content> {
        fun of(url: CSSUrl): Listable = Url(url)
        fun of(gradient: com.varabyte.kobweb.compose.css.functions.Gradient): Listable = Gradient(gradient)
        fun of(text: String): Listable = Text(text)

        fun of(url: CSSUrl, altText: String): Content = list(altText, of(url))
        fun of(gradient: com.varabyte.kobweb.compose.css.functions.Gradient, altText: String): Content = list(altText, of(gradient))
        fun of(text: String, altText: String): Content = list(altText, of(text))

        fun list(vararg contents: Listable): Content = ValueList(null, contents.toList())
        fun list(altText: String, vararg contents: Listable): Content = ValueList(altText.takeIf { it.isNotBlank() }, contents.toList())

        // Non-combinable keywords
        val None get(): Content = Keyword("none")
        val Normal get(): Content = Keyword("normal")

        // Language / position-dependent keywords
        val CloseQuote get(): Listable = ListableKeyword("close-quote")
        val NoCloseQuote get(): Listable = ListableKeyword("no-close-quote")
        val NoOpenQuote get(): Listable = ListableKeyword("no-open-quote")
        val OpenQuote get(): Listable = ListableKeyword("open-quote")
    }
}

fun StyleScope.content(content: Content) {
    property("content", content)
}

// Needed temporarily until we can remove the deprecated `vararg` version
fun StyleScope.content(content: Content.Listable) {
    content(content as Content)
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
