package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.content
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrModifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.jetbrains.compose.web.attributes.DirType
import org.jetbrains.compose.web.attributes.Draggable

fun Modifier.classNames(vararg classes: String) = attrModifier {
    classes(*classes)
}

fun Modifier.content(value: String) = styleModifier {
    content(value)
}

fun Modifier.hidden() = attrModifier {
    hidden()
}

fun Modifier.id(value: String) = attrModifier {
    id(value)
}

fun Modifier.title(value: String) = attrModifier {
    title(value)
}
