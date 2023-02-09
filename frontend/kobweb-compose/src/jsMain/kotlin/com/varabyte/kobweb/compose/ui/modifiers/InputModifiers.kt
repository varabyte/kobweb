package com.varabyte.kobweb.compose.ui.modifiers

import androidx.compose.web.events.SyntheticDragEvent
import androidx.compose.web.events.SyntheticEvent
import androidx.compose.web.events.SyntheticMouseEvent
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.attributes.Draggable
import org.jetbrains.compose.web.attributes.EventsListenerScope
import org.jetbrains.compose.web.attributes.SyntheticEventListener
import org.jetbrains.compose.web.events.SyntheticFocusEvent
import org.jetbrains.compose.web.events.SyntheticKeyboardEvent
import org.jetbrains.compose.web.events.SyntheticTouchEvent
import org.w3c.dom.events.EventTarget

fun Modifier.contentEditable(editable: Boolean) = attrsModifier {
    contentEditable(editable)
}

fun Modifier.cursor(cursor: Cursor) = styleModifier {
    cursor(cursor)
}

fun Modifier.draggable(draggable: Draggable) = attrsModifier {
    draggable(draggable)
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

// region mouse events

fun Modifier.onClick(onClick: (SyntheticMouseEvent) -> Unit): Modifier = attrsModifier {
    onClick { evt -> onClick(evt) }
}

fun Modifier.onDoubleClick(onDoubleClick: (SyntheticMouseEvent) -> Unit): Modifier = attrsModifier {
    onDoubleClick { evt -> onDoubleClick(evt) }
}

fun Modifier.onContextMenu(onContextMenu: (SyntheticMouseEvent) -> Unit): Modifier = attrsModifier {
    onContextMenu { evt -> onContextMenu(evt) }
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

fun Modifier.onMouseOut(onMouseOut: (SyntheticMouseEvent) -> Unit) = attrsModifier {
    onMouseOut { evt -> onMouseOut(evt) }
}

fun Modifier.onMouseOver(onMouseOver: (SyntheticMouseEvent) -> Unit) = attrsModifier {
    onMouseOver { evt -> onMouseOver(evt) }
}

fun Modifier.onMouseUp(onMouseUp: (SyntheticMouseEvent) -> Unit) = attrsModifier {
    onMouseUp { evt -> onMouseUp(evt) }
}

fun Modifier.onWheel(onWheel: (SyntheticMouseEvent) -> Unit) = attrsModifier {
    onWheel { evt -> onWheel(evt) }
}

// endregion

// region drag events

fun Modifier.onDrag(onDrag: (SyntheticDragEvent) -> Unit): Modifier = attrsModifier {
    onDrag { evt -> onDrag(evt) }
}

fun Modifier.onDrop(onDrop: (SyntheticDragEvent) -> Unit): Modifier = attrsModifier {
    onDrop { evt -> onDrop(evt) }
}

fun Modifier.onDragStart(listener: (SyntheticDragEvent) -> Unit): Modifier = attrsModifier {
    onDragStart(listener)
}

fun Modifier.onDragEnd(listener: (SyntheticDragEvent) -> Unit): Modifier = attrsModifier {
    onDragEnd(listener)
}

fun Modifier.onDragOver(listener: (SyntheticDragEvent) -> Unit): Modifier = attrsModifier {
    onDragOver(listener)
}

fun Modifier.onDragEnter(listener: (SyntheticDragEvent) -> Unit): Modifier = attrsModifier {
    onDragEnter(listener)
}

fun Modifier.onDragLeave(listener: (SyntheticDragEvent) -> Unit): Modifier = attrsModifier {
    onDragLeave(listener)
}

// endregion

// region keyboard events

fun Modifier.onKeyDown(onKeyDown: (SyntheticKeyboardEvent) -> Unit) = attrsModifier {
    onKeyDown { evt -> onKeyDown(evt) }
}

fun Modifier.onKeyUp(onKeyUp: (SyntheticKeyboardEvent) -> Unit) = attrsModifier {
    onKeyUp { evt -> onKeyUp(evt) }
}

// endregion

// region focus events

fun Modifier.onFocus(listener: (SyntheticFocusEvent) -> Unit): Modifier = attrsModifier {
    onFocus(listener)
}

fun Modifier.onBlur(listener: (SyntheticFocusEvent) -> Unit): Modifier = attrsModifier {
    onBlur(listener)
}

fun Modifier.onFocusIn(listener: (SyntheticFocusEvent) -> Unit): Modifier = attrsModifier {
    onFocusIn(listener)
}

fun Modifier.onFocusOut(listener: (SyntheticFocusEvent) -> Unit): Modifier = attrsModifier {
    onFocusOut(listener)
}

// endregion

// region touch events

fun Modifier.onTouchCancel(listener: (SyntheticTouchEvent) -> Unit): Modifier = attrsModifier {
    onTouchCancel(listener)
}

fun Modifier.onTouchMove(listener: (SyntheticTouchEvent) -> Unit): Modifier = attrsModifier {
    onTouchMove(listener)
}

fun Modifier.onTouchEnd(listener: (SyntheticTouchEvent) -> Unit): Modifier = attrsModifier {
    onTouchEnd(listener)
}

fun Modifier.onTouchStart(listener: (SyntheticTouchEvent) -> Unit): Modifier = attrsModifier {
    onTouchStart(listener)
}

// endregion

// region scroll events

fun Modifier.onScroll(listener: (SyntheticEvent<EventTarget>) -> Unit): Modifier = attrsModifier {
    onScroll(listener)
}

// endregion