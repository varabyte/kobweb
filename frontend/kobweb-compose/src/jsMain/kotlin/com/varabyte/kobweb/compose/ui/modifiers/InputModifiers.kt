package com.varabyte.kobweb.compose.ui.modifiers

import androidx.compose.web.events.SyntheticDragEvent
import androidx.compose.web.events.SyntheticEvent
import androidx.compose.web.events.SyntheticMouseEvent
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.CaretColor
import com.varabyte.kobweb.compose.css.caretColor
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.attributes.Draggable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.events.SyntheticFocusEvent
import org.jetbrains.compose.web.events.SyntheticKeyboardEvent
import org.jetbrains.compose.web.events.SyntheticTouchEvent
import org.w3c.dom.events.EventTarget

fun Modifier.caretColor(caretColor: CaretColor) = styleModifier {
    caretColor(caretColor)
}

fun Modifier.caretColor(color: CSSColorValue) = styleModifier {
    caretColor(CaretColor.of(color))
}

fun Modifier.contentEditable(editable: Boolean) = attrsModifier {
    contentEditable(editable)
}

fun Modifier.cursor(cursor: Cursor) = styleModifier {
    cursor(cursor)
}

fun Modifier.draggable(draggable: Draggable) = attrsModifier {
    draggable(draggable)
}

fun Modifier.draggable(draggable: Boolean) = draggable(if (draggable) Draggable.True else Draggable.False)

fun Modifier.pointerEvents(pointerEvents: PointerEvents) = styleModifier {
    pointerEvents(pointerEvents)
}

fun Modifier.userSelect(userSelect: UserSelect): Modifier = styleModifier {
    userSelect(userSelect)
}

fun Modifier.tabIndex(value: Int) = attrsModifier {
    tabIndex(value)
}

fun Modifier.touchAction(touchAction: TouchAction) = styleModifier {
    touchAction(touchAction)
}

// region mouse events

fun Modifier.onClick(listener: (SyntheticMouseEvent) -> Unit): Modifier = attrsModifier {
    onClick(listener)
}

fun Modifier.onDoubleClick(listener: (SyntheticMouseEvent) -> Unit): Modifier = attrsModifier {
    onDoubleClick(listener)
}

fun Modifier.onContextMenu(listener: (SyntheticMouseEvent) -> Unit): Modifier = attrsModifier {
    onContextMenu(listener)
}

fun Modifier.onMouseDown(listener: (SyntheticMouseEvent) -> Unit) = attrsModifier {
    onMouseDown(listener)
}

fun Modifier.onMouseEnter(listener: (SyntheticMouseEvent) -> Unit) = attrsModifier {
    onMouseEnter(listener)
}

fun Modifier.onMouseLeave(listener: (SyntheticMouseEvent) -> Unit) = attrsModifier {
    onMouseLeave(listener)
}

fun Modifier.onMouseMove(listener: (SyntheticMouseEvent) -> Unit) = attrsModifier {
    onMouseMove(listener)
}

fun Modifier.onMouseOut(listener: (SyntheticMouseEvent) -> Unit) = attrsModifier {
    onMouseOut(listener)
}

fun Modifier.onMouseOver(listener: (SyntheticMouseEvent) -> Unit) = attrsModifier {
    onMouseOver(listener)
}

fun Modifier.onMouseUp(listener: (SyntheticMouseEvent) -> Unit) = attrsModifier {
    onMouseUp(listener)
}

fun Modifier.onWheel(listener: (SyntheticMouseEvent) -> Unit) = attrsModifier {
    onWheel(listener)
}

// endregion

// region drag events

fun Modifier.onDrag(listener: (SyntheticDragEvent) -> Unit): Modifier = attrsModifier {
    onDrag(listener)
}

fun Modifier.onDrop(listener: (SyntheticDragEvent) -> Unit): Modifier = attrsModifier {
    onDrop(listener)
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

fun Modifier.onKeyDown(listener: (SyntheticKeyboardEvent) -> Unit) = attrsModifier {
    onKeyDown(listener)
}

fun Modifier.onKeyUp(listener: (SyntheticKeyboardEvent) -> Unit) = attrsModifier {
    onKeyUp(listener)
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
