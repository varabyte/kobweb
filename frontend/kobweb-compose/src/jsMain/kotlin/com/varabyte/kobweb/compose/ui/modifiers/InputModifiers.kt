package com.varabyte.kobweb.compose.ui.modifiers

import androidx.compose.web.events.SyntheticMouseEvent
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.UserSelect
import com.varabyte.kobweb.compose.css.cursor
import com.varabyte.kobweb.compose.css.userSelect
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrModifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.attributes.Draggable
import org.jetbrains.compose.web.events.SyntheticKeyboardEvent

fun Modifier.contentEditable(editable: Boolean) = attrModifier {
    contentEditable(editable)
}

fun Modifier.cursor(cursor: Cursor) = styleModifier {
    cursor(cursor)
}

fun Modifier.draggable(draggable: Draggable) = attrModifier {
    draggable(draggable)
}

fun Modifier.onClick(onClick: (SyntheticMouseEvent) -> Unit): Modifier = attrModifier {
    onClick { evt -> onClick(evt) }
}

fun Modifier.onKeyDown(onKeyDown: (SyntheticKeyboardEvent) -> Unit) = attrModifier {
    onKeyDown { evt -> onKeyDown(evt) }
}

fun Modifier.onKeyUp(onKeyUp: (SyntheticKeyboardEvent) -> Unit) = attrModifier {
    onKeyUp { evt -> onKeyUp(evt) }
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

fun Modifier.userSelect(userSelect: UserSelect): Modifier = styleModifier {
    userSelect(userSelect)
}

fun Modifier.tabIndex(value: Int) = attrModifier {
    tabIndex(value)
}