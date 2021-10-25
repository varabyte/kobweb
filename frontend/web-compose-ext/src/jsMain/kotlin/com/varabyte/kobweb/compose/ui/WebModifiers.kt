package com.varabyte.kobweb.compose.ui

import androidx.compose.web.events.SyntheticMouseEvent
import com.varabyte.kobweb.compose.css.FontStyle
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.UserSelect
import com.varabyte.kobweb.compose.css.cursor
import com.varabyte.kobweb.compose.css.fontStyle
import com.varabyte.kobweb.compose.css.fontWeight
import com.varabyte.kobweb.compose.css.userSelect
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.CSSPercentageValue
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.lineHeight
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.maxHeight
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.minHeight
import org.jetbrains.compose.web.css.minWidth
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import com.varabyte.kobweb.compose.css.Cursor as KobwebCursor
import com.varabyte.kobweb.compose.ui.graphics.Color as KobwebColor

fun Modifier.color(color: KobwebColor) = styleModifier {
    color(color.toCssColor())
}

fun Modifier.color(color: CSSColorValue) = styleModifier {
    color(color)
}

fun Modifier.background(color: KobwebColor) = styleModifier {
    backgroundColor(color.toCssColor())
}

fun Modifier.background(color: CSSColorValue) = styleModifier {
    backgroundColor(color)
}

fun Modifier.classNames(vararg classes: String) = attrModifier {
    classes(*classes)
}

fun Modifier.cursor(cursor: KobwebCursor) = styleModifier {
    cursor(cursor)
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

fun Modifier.clickable(onClick: (SyntheticMouseEvent) -> Unit): Modifier = attrModifier {
    onClick { evt -> onClick(evt) }
}

fun Modifier.onMouseDown(onMouseDown: (SyntheticMouseEvent) -> Unit) = attrModifier {
    onMouseDown { evt -> onMouseDown(evt) }
}

fun Modifier.onMouseEnter(onMouseEnter: (SyntheticMouseEvent) -> Unit) = attrModifier {
    onMouseEnter { evt -> onMouseEnter(evt) }
}

fun Modifier.onMouseLeave(onMouseLeave: (SyntheticMouseEvent) -> Unit) = attrModifier {
    onMouseLeave { evt -> onMouseLeave(evt) }
}

fun Modifier.onMouseMove(onMouseMove: (SyntheticMouseEvent) -> Unit) = attrModifier {
    onMouseMove { evt -> onMouseMove(evt) }
}

fun Modifier.onMouseUp(onMouseUp: (SyntheticMouseEvent) -> Unit) = attrModifier {
    onMouseUp { evt -> onMouseUp(evt) }
}

fun Modifier.padding(all: CSSNumeric): Modifier = styleModifier {
    // Compose padding is the same thing as CSS margin, confusingly... (it puts space around the current composable,
    // as opposed to doing anything with its children)
    margin(all)
}

fun Modifier.padding(topBottom: CSSNumeric, leftRight: CSSNumeric): Modifier = styleModifier {
    // See: Modifier.padding(all) comment
    margin(topBottom, leftRight)
}

fun Modifier.padding(top: CSSNumeric = 0.px, right: CSSNumeric = 0.px, bottom: CSSNumeric = 0.px, left: CSSNumeric = 0.px): Modifier = styleModifier {
    // See: Modifier.padding(all) comment
    margin(top, right, bottom, left)
}

fun Modifier.userSelect(userSelect: UserSelect): Modifier = styleModifier {
    userSelect(userSelect)
}
