package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrModifier

fun Modifier.classNames(vararg classes: String) = attrModifier {
    classes(*classes)
}