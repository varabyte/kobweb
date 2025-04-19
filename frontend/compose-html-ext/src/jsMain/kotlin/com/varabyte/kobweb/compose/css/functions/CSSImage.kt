package com.varabyte.kobweb.compose.css.functions

import com.varabyte.kobweb.compose.css.global.CssGlobalValues
import org.jetbrains.compose.web.css.*

sealed class CSSImage private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String) : CSSImage(value)
    private class Url(url: CSSUrl) : CSSImage(url.toString())
    private class Gradient(gradient: com.varabyte.kobweb.compose.css.functions.Gradient) :
        CSSImage(gradient.toString())


    companion object: CssGlobalValues<Keyword> {
        fun of(url: CSSUrl): CSSImage = Url(url)

        /**
         * @see toImage
         */
        fun of(gradient: com.varabyte.kobweb.compose.css.functions.Gradient): CSSImage = Gradient(gradient)

        // Keyword
        val None get(): CSSImage = Keyword("none")
    }
}

fun Gradient.toImage() = CSSImage.of(this)
