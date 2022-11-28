package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.display(value: DisplayStyle) = styleModifier {
    display(value)
}

fun Modifier.visibility(visibility: Visibility) = styleModifier {
    visibility(visibility)
}
