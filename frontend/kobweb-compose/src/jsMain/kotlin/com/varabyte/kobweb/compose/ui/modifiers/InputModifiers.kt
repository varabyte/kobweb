package com.varabyte.kobweb.compose.ui.modifiers

import androidx.compose.web.events.SyntheticDragEvent
import androidx.compose.web.events.SyntheticMouseEvent
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.attributes.Draggable
import org.jetbrains.compose.web.events.SyntheticKeyboardEvent

fun Modifier.contentEditable(editable: Boolean) = attrsModifier {
    contentEditable(editable)
}

fun Modifier.cursor(cursor: Cursor) = styleModifier {
    cursor(cursor)
}

fun Modifier.draggable(draggable: Draggable) = attrsModifier {
    draggable(draggable)
}

fun Modifier.onClick(onClick: (SyntheticMouseEvent) -> Unit): Modifier = attrsModifier {
    onClick { evt -> onClick(evt) }
}

fun Modifier.onDrag(onDrag: (SyntheticDragEvent) -> Unit): Modifier = attrsModifier {
    onDrag { evt -> onDrag(evt) }
}

fun Modifier.onDragOver(onDragOver: (SyntheticDragEvent) -> Unit): Modifier = attrsModifier {
    onDragOver { evt -> onDragOver(evt) }
}

fun Modifier.onDrop(onDrop: (SyntheticDragEvent) -> Unit): Modifier = attrsModifier {
    onDrop { evt -> onDrop(evt) }
}

fun Modifier.onKeyDown(onKeyDown: (SyntheticKeyboardEvent) -> Unit) = attrsModifier {
    onKeyDown { evt -> onKeyDown(evt) }
}

fun Modifier.onKeyUp(onKeyUp: (SyntheticKeyboardEvent) -> Unit) = attrsModifier {
    onKeyUp { evt -> onKeyUp(evt) }
}

fun Modifier.onMouseDown(onMouseDown: (SyntheticMouseEvent) -> Unit) = attrsModifier {
    onMouseDown { evt -> onMouseDown(evt) }
}

fun Modifier.onMouseEnter(onMouseEnter: (SyntheticMouseEvent) -> Unit) = attrsModifier {
    onMouseEnter { evt -> onMouseEnter(evt) }
}

fun Modifier.onMouseLeave(onMouseLeave: (SyntheticMouseEvent) -> Unit) = attrsModifier {
    onMouseLeave { evt -> onMouseLeave(evt) }
}

fun Modifier.onMouseMove(onMouseMove: (SyntheticMouseEvent) -> Unit) = attrsModifier {
    onMouseMove { evt -> onMouseMove(evt) }
}

fun Modifier.onMouseUp(onMouseUp: (SyntheticMouseEvent) -> Unit) = attrsModifier {
    onMouseUp { evt -> onMouseUp(evt) }
}

fun Modifier.pointerEvents(pointerEvents: PointerEvents) = styleModifier {
    pointerEvents(pointerEvents)
}

fun Modifier.userSelect(userSelect: UserSelect): Modifier = styleModifier {
    userSelect(userSelect)
}

fun Modifier.tabIndex(value: Int) = attrsModifier {
    tabIndex(value)
}