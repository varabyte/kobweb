package com.varabyte.kobweb.compose.css.functions

import org.jetbrains.compose.web.css.*

sealed class CSSImage private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String) : CSSImage(value)
    private class Url(url: CSSUrl) : CSSImage(url.toString())
    private class Gradient(gradient: com.varabyte.kobweb.compose.css.functions.Gradient) :
        CSSImage(gradient.toString())


    companion object {
        fun of(url: CSSUrl): CSSImage = Url(url)

        /**
         * @see [com.varabyte.kobweb.compose.css.functions.Gradient.toImage]
         */
        fun of(gradient: com.varabyte.kobweb.compose.css.functions.Gradient): CSSImage = Gradient(gradient)

        // Keyword
        val None get(): CSSImage = Keyword("none")

        // Global values
        val Inherit get(): CSSImage = Keyword("inherit")
        val Initial get(): CSSImage = Keyword("initial")
        val Revert get(): CSSImage = Keyword("revert")
        val Unset get(): CSSImage = Keyword("unset")
    }
}

fun Gradient.toImage() = CSSImage.of(this)
