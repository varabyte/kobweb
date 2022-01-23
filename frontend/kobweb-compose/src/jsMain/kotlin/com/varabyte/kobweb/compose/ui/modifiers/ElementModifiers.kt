package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.content
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrModifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.classNames(vararg classes: String) = attrModifier {
    classes(*classes)
}

fun Modifier.content(value: String) = styleModifier {
    content(value)
}