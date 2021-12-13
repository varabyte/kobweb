package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.backdropFilter(value: String) = styleModifier {
    property("backdrop-filter", value)
}

fun Modifier.boxShadow(value: String) = styleModifier {
    property("box-shadow", value)
}