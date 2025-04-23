package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/word-spacing
class WordSpacing private constructor(private val value: String) : StylePropertyValue {

    companion object {

        /* Keyword value */
        val Normal get() = WordSpacing("normal")

        /* <length> values */
        fun of(length: CSSLengthNumericValue) = WordSpacing("$length")

        /* Global values */
        val Inherit get() = WordSpacing("inherit")
        val Initial get() = WordSpacing("initial")
        val Revert get() = WordSpacing("revert")
        val RevertLayer get() = WordSpacing("revert-layer")
        val Unset get() = WordSpacing("unset")
    }
}

fun StyleScope.wordSpacing(wordSpacing: WordSpacing) {
    property("word-spacing", wordSpacing)
}