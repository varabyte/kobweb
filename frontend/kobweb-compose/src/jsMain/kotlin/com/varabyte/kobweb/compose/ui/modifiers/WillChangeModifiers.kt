package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.willChange(willChange: WillChange) = styleModifier {
    willChange(willChange)
}

fun Modifier.willChange(value: String) = styleModifier {
    willChange(WillChange.of(value))
}

fun Modifier.willChange(vararg values: String) = styleModifier {
    willChange(WillChange.of(*values))
}