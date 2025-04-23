package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.willChange(willChange: WillChange) = styleModifier {
    willChange(willChange)
}

fun Modifier.willChange(customIndent: String) = styleModifier {
    willChange(WillChange.of(customIndent))
}

fun Modifier.willChange(firstPosition: String, secondPosition: String) = styleModifier {
    willChange(WillChange.Companion.of(firstPosition, secondPosition))
}