package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

// region grid container

// TODO(#168): Remove before v1.0
@Suppress("DeprecatedCallableAddReplaceWith") // Not a trivial replace
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use `grid` instead or use styleModifier as a fallback.",
)
fun Modifier.gridTemplateColumns(value: String) = styleModifier {
    gridTemplateColumns(value)
}

// TODO(#168): Remove before v1.0
@Suppress("DeprecatedCallableAddReplaceWith") // Not a trivial replace
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use `grid` instead or use styleModifier as a fallback.",
)
fun Modifier.gridAutoColumns(value: String) = styleModifier {
    gridAutoColumns(value)
}

fun Modifier.gridAutoFlow(value: GridAutoFlow) = styleModifier {
    gridAutoFlow(value)
}

// TODO(#168): Remove before v1.0
@Suppress("DeprecatedCallableAddReplaceWith") // Not a trivial replace
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use `grid` instead or use styleModifier as a fallback.",
)
fun Modifier.gridTemplateRows(value: String) = styleModifier {
    gridTemplateRows(value)
}

// TODO(#168): Remove before v1.0
@Suppress("DeprecatedCallableAddReplaceWith") // Not a trivial replace
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use `grid` instead or use styleModifier as a fallback.",
)
fun Modifier.gridAutoRows(value: String) = styleModifier {
    gridAutoRows(value)
}

fun Modifier.gridTemplateAreas(vararg rows: String) = styleModifier {
    gridTemplateAreas(*rows)
}

fun Modifier.grid(block: GridBuilder.() -> Unit) = styleModifier {
    grid(block)
}

// endregion grid container

// region grid items

fun Modifier.gridColumn(value: String) = styleModifier {
    gridColumn(value)
}

fun Modifier.gridColumn(start: String, end: String) = styleModifier {
    gridColumn(start, end)
}

fun Modifier.gridColumn(start: String, end: Int) = styleModifier {
    gridColumn(start, end)
}

fun Modifier.gridColumn(start: Int, end: String) = styleModifier {
    gridColumn(start, end)
}

fun Modifier.gridColumn(start: Int, end: Int) = styleModifier {
    gridColumn(start, end)
}

fun Modifier.gridColumnStart(value: String) = styleModifier {
    gridColumnStart(value)
}

fun Modifier.gridColumnStart(value: Int) = styleModifier {
    gridColumnStart(value)
}

fun Modifier.gridColumnEnd(value: String) = styleModifier {
    gridColumnEnd(value)
}

fun Modifier.gridColumnEnd(value: Int) = styleModifier {
    gridColumnEnd(value)
}

fun Modifier.gridRow(value: String) = styleModifier {
    gridRow(value)
}

fun Modifier.gridRow(start: String, end: String) = styleModifier {
    gridRow(start, end)
}

fun Modifier.gridRow(start: String, end: Int) = styleModifier {
    gridRow(start, end)
}

fun Modifier.gridRow(start: Int, end: String) = styleModifier {
    gridRow(start, end)
}

fun Modifier.gridRow(start: Int, end: Int) = styleModifier {
    gridRow(start, end)
}

fun Modifier.gridRowStart(value: String) = styleModifier {
    gridRowStart(value)
}

fun Modifier.gridRowStart(value: Int) = styleModifier {
    gridRowStart(value)
}

fun Modifier.gridRowEnd(value: String) = styleModifier {
    gridRowEnd(value)
}

fun Modifier.gridRowEnd(value: Int) = styleModifier {
    gridRowEnd(value)
}

fun Modifier.gridArea(rowStart: String) = styleModifier {
    gridArea(rowStart)
}

fun Modifier.gridArea(rowStart: String, columnStart: String) = styleModifier {
    gridArea(rowStart, columnStart)
}

fun Modifier.gridArea(rowStart: String, columnStart: String, rowEnd: String) = styleModifier {
    gridArea(rowStart, columnStart, rowEnd)
}

fun Modifier.gridArea(rowStart: String, columnStart: String, rowEnd: String, columnEnd: String) = styleModifier {
    gridArea(rowStart, columnStart, rowEnd, columnEnd)
}

// endregion grid items
