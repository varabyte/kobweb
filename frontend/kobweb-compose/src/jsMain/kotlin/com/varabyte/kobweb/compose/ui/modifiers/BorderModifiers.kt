package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.CSSLengthValue
import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.borderWidth

fun Modifier.border(value: String) = styleModifier {
    property("border", value)
}

fun Modifier.border(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
     border(width, style, color)
}

fun Modifier.borderTop(value: String) = styleModifier {
    property("border-top", value)
}

fun Modifier.borderTop(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
     borderTop(width, style, color)
}

fun Modifier.borderBottom(value: String) = styleModifier {
    property("border-bottom", value)
}

fun Modifier.borderBottom(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
    borderBottom(width, style, color)
}

fun Modifier.borderLeft(value: String) = styleModifier {
    property("border-left", value)
}

fun Modifier.borderLeft(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
     borderLeft(width, style, color)
}

fun Modifier.borderRight(value: String) = styleModifier {
    property("border-right", value)
}

fun Modifier.borderRight(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
     borderRight(width, style, color)
}

fun Modifier.borderRadius(r: CSSNumeric) = styleModifier {
    borderRadius(r)
}

fun Modifier.borderRadius(topLeft: CSSNumeric, bottomRight: CSSNumeric) = styleModifier {
    borderRadius(topLeft, bottomRight)
}

fun Modifier.borderRadius(
    topLeft: CSSNumeric,
    topRightAndBottomLeft: CSSNumeric,
    bottomRight: CSSNumeric
) = styleModifier {
    borderRadius(topLeft, topRightAndBottomLeft, bottomRight)
}

fun Modifier.borderRadius(
    topLeft: CSSNumeric,
    topRight: CSSNumeric,
    bottomRight: CSSNumeric,
    bottomLeft: CSSNumeric
) = styleModifier {
    borderRadius(topLeft, topRight, bottomRight, bottomLeft)
}

fun Modifier.borderWidth(width: CSSNumeric) = styleModifier {
    borderWidth(width)
}

fun Modifier.borderWidth(topLeft: CSSNumeric, bottomRight: CSSNumeric) = styleModifier {
    borderWidth(topLeft, bottomRight)
}

fun Modifier.borderWidth(
    topLeft: CSSNumeric,
    topRightAndBottomLeft: CSSNumeric,
    bottomRight: CSSNumeric
) = styleModifier {
    borderWidth(topLeft, topRightAndBottomLeft, bottomRight)
}

fun Modifier.borderWidth(
    topLeft: CSSNumeric,
    topRight: CSSNumeric,
    bottomRight: CSSNumeric,
    bottomLeft: CSSNumeric
) = styleModifier {
    borderWidth(topLeft, topRight, bottomRight, bottomLeft)
}

fun Modifier.borderColor(value: String) = styleModifier {
    property("border-color", value)
}

fun Modifier.borderColor(color: CSSColorValue) = styleModifier {
    property("border-color", color)
}

fun Modifier.borderColor(color: Color) = borderColor(color.toCssColor())