package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.listStyle
import org.jetbrains.compose.web.css.listStyleImage
import org.jetbrains.compose.web.css.listStylePosition
import org.jetbrains.compose.web.css.listStyleType

fun Modifier.listStyle(value: String) = styleModifier {
    listStyle(value)
}

fun Modifier.listStyleType(value: String) = styleModifier {
    listStyleType(value)
}

fun Modifier.listStyleImage(value: String) = styleModifier {
    listStyleImage(value)
}

fun Modifier.listStylePosition(value: String) = styleModifier {
    listStylePosition(value)
}
