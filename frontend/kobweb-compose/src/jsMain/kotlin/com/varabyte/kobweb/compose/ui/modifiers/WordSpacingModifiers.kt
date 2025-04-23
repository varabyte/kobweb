package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.wordSpacing(wordSpacing: WordSpacing) = styleModifier {
    wordSpacing(wordSpacing)
}

fun Modifier.wordSpacing(length: CSSLengthNumericValue) = styleModifier {
    wordSpacing(WordSpacing.of(length))
}

