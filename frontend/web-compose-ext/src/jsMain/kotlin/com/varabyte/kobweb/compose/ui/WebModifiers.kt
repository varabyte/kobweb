package com.varabyte.kobweb.compose.ui

import androidx.compose.web.events.SyntheticMouseEvent
import com.varabyte.kobweb.compose.css.UserSelect
import com.varabyte.kobweb.compose.css.cursor
import com.varabyte.kobweb.compose.css.userSelect
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.unit.Dp
import com.varabyte.kobweb.compose.ui.unit.dp
import org.jetbrains.compose.web.css.*
import com.varabyte.kobweb.compose.css.Cursor as KobwebCursor
import com.varabyte.kobweb.compose.ui.graphics.Color as KobwebColor

fun Modifier.borderRadius(size: Dp) = webModifier {
    style {
        borderRadius(size.value.px)
    }
}

fun Modifier.background(color: KobwebColor) = webModifier {
    style {
        backgroundColor(color.toCssColor())
    }
}

fun Modifier.cursor(cursor: KobwebCursor) = webModifier {
    style {
        cursor(cursor)
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

fun Modifier.size(size: CSSPercentageValue): Modifier = webModifier {
    style {
        width(size)
        height(size)
    }
}

fun Modifier.size(size: Dp): Modifier = webModifier {
    style {
        width(size.value.px)
        height(size.value.px)
    }
}

fun Modifier.width(size: Dp): Modifier = webModifier {
    style {
        width(size.value.px)
    }
}

fun Modifier.width(size: CSSPercentageValue): Modifier = webModifier {
    style {
        width(size)
    }
}

fun Modifier.height(size: Dp): Modifier = webModifier {
    style {
        height(size.value.px)
    }
}

fun Modifier.height(size: CSSPercentageValue): Modifier = webModifier {
    style {
        height(size)
    }
}

fun Modifier.minWidth(size: Dp): Modifier = webModifier {
    style {
        minWidth(size.value.px)
    }
}

fun Modifier.minWidth(size: CSSPercentageValue): Modifier = webModifier {
    style {
        minWidth(size)
    }
}

fun Modifier.minHeight(size: Dp): Modifier = webModifier {
    style {
        minHeight(size.value.px)
    }
}

fun Modifier.minHeight(size: CSSPercentageValue): Modifier = webModifier {
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

fun Modifier.padding(all: Dp): Modifier = webModifier {
    style {
        // Compose padding is the same thing as CSS margin, confusingly... (it puts space around the current composable,
        // as opposed to doing anything with its children)
        margin(all.value.px)
    }
}

fun Modifier.padding(topBottom: Dp, leftRight: Dp): Modifier = webModifier {
    style {
        // See: Modifier.padding(all) comment
        margin(topBottom.value.px, leftRight.value.px)
    }
}

fun Modifier.padding(top: Dp = 0.dp, right: Dp = 0.dp, bottom: Dp = 0.dp, left: Dp = 0.dp): Modifier = webModifier {
    style {
        // See: Modifier.padding(all) comment
        margin(top.value.px, right.value.px, bottom.value.px, left.value.px)
    }
}

fun Modifier.userSelect(userSelect: UserSelect): Modifier = webModifier {
    style {
        userSelect(userSelect)
    }
}