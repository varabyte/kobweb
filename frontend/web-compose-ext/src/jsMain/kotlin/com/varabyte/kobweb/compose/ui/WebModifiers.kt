package com.varabyte.kobweb.compose.ui

import androidx.compose.web.events.SyntheticMouseEvent
import com.varabyte.kobweb.compose.css.UserSelect
import com.varabyte.kobweb.compose.css.cursor
import com.varabyte.kobweb.compose.css.userSelect
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.CSSPercentageValue
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.lineHeight
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.minHeight
import org.jetbrains.compose.web.css.minWidth
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import com.varabyte.kobweb.compose.css.Cursor as KobwebCursor
import com.varabyte.kobweb.compose.ui.graphics.Color as KobwebColor

fun Modifier.background(color: KobwebColor) = webModifier {
    style {
        backgroundColor(color.toCssColor())
    }
}

fun Modifier.classNames(vararg classes: String) = webModifier {
    style {
        classes(*classes)
    }
}

fun Modifier.cursor(cursor: KobwebCursor) = webModifier {
    style {
        cursor(cursor)
    }
}

fun Modifier.fontSize(value: CSSNumeric): Modifier = webModifier {
    style {
        fontSize(value)
    }
}

fun Modifier.lineHeight(value: CSSNumeric): Modifier = webModifier {
    style {
        lineHeight(value)
    }
}

fun Modifier.lineHeight(value: Double): Modifier = webModifier {
    style {
        lineHeight(value.toString())
    }
}

fun Modifier.fillMaxWidth(percent: CSSPercentageValue = 100.percent) = webModifier {
    style {
        width(percent)
    }
}

fun Modifier.fillMaxHeight(percent: CSSPercentageValue = 100.percent) = webModifier {
    style {
        height(percent)
    }
}

fun Modifier.fillMaxSize(percent: CSSPercentageValue = 100.percent): Modifier = webModifier {
    style {
        width(percent)
        height(percent)
    }
}

fun Modifier.size(size: CSSNumeric): Modifier = webModifier {
    style {
        width(size)
        height(size)
    }
}

fun Modifier.width(size: CSSNumeric): Modifier = webModifier {
    style {
        width(size)
    }
}

fun Modifier.height(size: CSSNumeric): Modifier = webModifier {
    style {
        height(size)
    }
}

fun Modifier.minWidth(size: CSSNumeric): Modifier = webModifier {
    style {
        minWidth(size)
    }
}

fun Modifier.minHeight(size: CSSNumeric): Modifier = webModifier {
    style {
        minHeight(size)
    }
}

fun Modifier.clickable(onClick: () -> Unit): Modifier = webModifier {
    onClick { onClick() }
}


fun Modifier.onMouseDown(onMouseDown: (SyntheticMouseEvent) -> Unit) = webModifier {
    onMouseDown { evt -> onMouseDown(evt) }
}

fun Modifier.onMouseEnter(onMouseEnter: (SyntheticMouseEvent) -> Unit) = webModifier {
    onMouseEnter { evt -> onMouseEnter(evt) }
}

fun Modifier.onMouseLeave(onMouseLeave: (SyntheticMouseEvent) -> Unit) = webModifier {
    onMouseLeave { evt -> onMouseLeave(evt) }
}

fun Modifier.onMouseMove(onMouseMove: (SyntheticMouseEvent) -> Unit) = webModifier {
    onMouseMove { evt -> onMouseMove(evt) }
}

fun Modifier.onMouseUp(onMouseUp: (SyntheticMouseEvent) -> Unit) = webModifier {
    onMouseUp { evt -> onMouseUp(evt) }
}

fun Modifier.padding(all: CSSNumeric): Modifier = webModifier {
    style {
        // Compose padding is the same thing as CSS margin, confusingly... (it puts space around the current composable,
        // as opposed to doing anything with its children)
        margin(all)
    }
}

fun Modifier.padding(topBottom: CSSNumeric, leftRight: CSSNumeric): Modifier = webModifier {
    style {
        // See: Modifier.padding(all) comment
        margin(topBottom, leftRight)
    }
}

fun Modifier.padding(top: CSSNumeric = 0.px, right: CSSNumeric = 0.px, bottom: CSSNumeric = 0.px, left: CSSNumeric = 0.px): Modifier = webModifier {
    style {
        // See: Modifier.padding(all) comment
        margin(top, right, bottom, left)
    }
}

fun Modifier.userSelect(userSelect: UserSelect): Modifier = webModifier {
    style {
        userSelect(userSelect)
    }
}