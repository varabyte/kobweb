package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.fontFamily(vararg values: String): Modifier = styleModifier {
    fontFamily(*values)
}

fun Modifier.fontSize(value: CSSNumeric): Modifier = styleModifier {
    fontSize(value)
}

fun Modifier.fontSize(fontSize: FontSize): Modifier = styleModifier {
    fontSize(fontSize)
}

fun Modifier.fontStyle(value: FontStyle): Modifier = styleModifier {
    fontStyle(value)
}

fun Modifier.fontWeight(value: FontWeight): Modifier = styleModifier {
    fontWeight(value)
}

fun Modifier.fontWeight(value: Int): Modifier = styleModifier {
    fontWeight(value)
}
