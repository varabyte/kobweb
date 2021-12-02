package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.CSSPercentageValue
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.lineHeight
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.maxHeight
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.minHeight
import org.jetbrains.compose.web.css.minWidth
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width

fun Modifier.lineHeight(value: CSSNumeric): Modifier = styleModifier {
    lineHeight(value)
}

fun Modifier.lineHeight(value: Number): Modifier = styleModifier {
    lineHeight(value.toString())
}

fun Modifier.fillMaxWidth(percent: CSSPercentageValue = 100.percent) = styleModifier {
    width(percent)
}

fun Modifier.fillMaxHeight(percent: CSSPercentageValue = 100.percent) = styleModifier {
    height(percent)
}

fun Modifier.fillMaxSize(percent: CSSPercentageValue = 100.percent): Modifier = styleModifier {
    width(percent)
    height(percent)
}

fun Modifier.size(size: CSSNumeric): Modifier = styleModifier {
    width(size)
    height(size)
}

fun Modifier.width(size: CSSNumeric): Modifier = styleModifier {
    width(size)
}

fun Modifier.height(size: CSSNumeric): Modifier = styleModifier {
    height(size)
}

fun Modifier.minWidth(size: CSSNumeric): Modifier = styleModifier {
    minWidth(size)
}

fun Modifier.maxWidth(size: CSSNumeric): Modifier = styleModifier {
    maxWidth(size)
}

fun Modifier.minHeight(size: CSSNumeric): Modifier = styleModifier {
    minHeight(size)
}

fun Modifier.maxHeight(size: CSSNumeric): Modifier = styleModifier {
    maxHeight(size)
}

fun Modifier.margin(all: CSSNumeric): Modifier = styleModifier {
    margin(all)
}

fun Modifier.margin(topBottom: CSSNumeric, leftRight: CSSNumeric): Modifier = styleModifier {
    margin(topBottom, leftRight)
}

fun Modifier.margin(
    top: CSSNumeric = 0.px,
    right: CSSNumeric = 0.px,
    bottom: CSSNumeric = 0.px,
    left: CSSNumeric = 0.px
): Modifier = styleModifier {
    margin(top, right, bottom, left)
}
fun Modifier.padding(all: CSSNumeric): Modifier = styleModifier {
    padding(all)
}

fun Modifier.padding(topBottom: CSSNumeric, leftRight: CSSNumeric): Modifier = styleModifier {
    padding(topBottom, leftRight)
}

fun Modifier.padding(
    top: CSSNumeric = 0.px,
    right: CSSNumeric = 0.px,
    bottom: CSSNumeric = 0.px,
    left: CSSNumeric = 0.px
): Modifier = styleModifier {
    padding(top, right, bottom, left)
}