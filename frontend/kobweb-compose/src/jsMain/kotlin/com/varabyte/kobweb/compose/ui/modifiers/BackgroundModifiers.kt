package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.background(value: String) = styleModifier {
    background(value)
}

fun Modifier.backgroundAttachment(value: String) = styleModifier {
    backgroundAttachment(value)
}

fun Modifier.backgroundClip(value: String) = styleModifier {
    backgroundClip(value)
}

fun Modifier.backgroundColor(value: String) = styleModifier {
    property("background-color", value)
}

fun Modifier.backgroundColor(color: CSSColorValue) = styleModifier {
    backgroundColor(color)
}

fun Modifier.backgroundImage(value: String) = styleModifier {
    backgroundImage(value)
}

fun Modifier.backgroundOrigin(value: String) = styleModifier {
    backgroundOrigin(value)
}

fun Modifier.backgroundPosition(value: String) = styleModifier {
    backgroundPosition(value)
}

fun Modifier.backgroundRepeat(value: String) = styleModifier {
    backgroundRepeat(value)
}

fun Modifier.backgroundSize(value: String) = styleModifier {
    backgroundSize(value)
}
