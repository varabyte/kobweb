package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.kobweb.compose.css.functions.Gradient
import com.varabyte.kobweb.compose.util.wrapQuotesIfNecessary
import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/appearance
class Appearance private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // CSS Basic User Interface Module Level 4 values
        val None get() = Appearance("none")
        val Auto get() = Appearance("auto")
        val MenuListButton get() = Appearance("menulist-button")
        val TextField get() = Appearance("textfield")

        // Global values
        val Inherit get() = Appearance("inherit")
        val Initial get() = Appearance("initial")
        val Revert get() = Appearance("revert")
        val Unset get() = Appearance("unset")
    }
}

fun StyleScope.appearance(appearance: Appearance) {
    property("appearance", appearance)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/content
sealed class Content(private val value: String) : StylePropertyValue {
    override fun toString() = value

    /** Content keywords that cannot be used in combination with any others. */
    sealed class Restricted(value: String) : Content(value)

    /** Content keywords that can be used in combination with others. */
    sealed class Unrestricted(value: String) : Content(value)

    private class Keyword(value: String) : Unrestricted(value)
    private class RestrictedKeyword(value: String) : Restricted(value)
    private class Text(value: String) : Unrestricted(value.wrapQuotesIfNecessary())

    private class Url(url: CSSUrl) : Unrestricted(url.toString())
    private class Gradient(gradient: com.varabyte.kobweb.compose.css.functions.Gradient) :
        Unrestricted(gradient.toString())

    companion object {
        fun of(url: CSSUrl): Unrestricted = Url(url)
        fun of(gradient: com.varabyte.kobweb.compose.css.functions.Gradient): Unrestricted = Gradient(gradient)
        fun of(text: String): Unrestricted = Text(text)

        // Non-combinable keywords
        val None get(): Restricted = RestrictedKeyword("none")
        val Normal get(): Restricted = RestrictedKeyword("normal")

        // Language / position-dependent keywords
        val CloseQuote get(): Content = Keyword("close-quote")
        val NoCloseQuote get(): Content = Keyword("no-close-quote")
        val NoOpenQuote get(): Content = Keyword("no-open-quote")
        val OpenQuote get(): Content = Keyword("open-quote")

        // Global
        val Inherit get(): Content = Keyword("inherit")
        val Initial get(): Content = Keyword("initial")
        val Revert get(): Content = Keyword("revert")
        val Unset get(): Content = Keyword("unset")
    }
}

fun StyleScope.content(content: Content.Restricted) {
    property("content", content)
}

fun StyleScope.content(vararg contents: Content.Unrestricted) {
    property("content", contents.joinToString(" "))
}

fun StyleScope.content(altText: String, vararg contents: Content.Unrestricted) {
    property("content", "${contents.joinToString(" ")} / ${altText.wrapQuotesIfNecessary()}")
}

/** Convenience function for an extremely common case, setting content to text. */
fun StyleScope.content(value: String) {
    content(Content.of(value))
}

fun CSSUrl.toContent() = Content.of(this)
fun Gradient.toContent() = Content.of(this)
