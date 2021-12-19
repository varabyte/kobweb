package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.FontStyle
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.fontStyle
import com.varabyte.kobweb.compose.css.fontWeight
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.fontFamily
import org.jetbrains.compose.web.css.fontSize

fun Modifier.fontFamily(vararg values: String): Modifier = styleModifier {
    fontFamily(*values)
}

fun Modifier.fontSize(value: CSSNumeric): Modifier = styleModifier {
    fontSize(value)
}

fun Modifier.fontStyle(value: FontStyle): Modifier = styleModifier {
    fontStyle(value)
}

fun Modifier.fontWeight(value: FontWeight): Modifier = styleModifier {
    fontWeight(value)
}