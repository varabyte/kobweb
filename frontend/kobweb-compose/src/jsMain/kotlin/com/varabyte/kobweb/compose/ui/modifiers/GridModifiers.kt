package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

// region grid container

fun Modifier.gridTemplateColumns(block: GridTrackBuilder.() -> Unit) = styleModifier {
    gridTemplateColumns(block)
}

fun Modifier.gridTemplateColumns(value: GridTemplate) = styleModifier {
    gridTemplateColumns(value)
}

fun Modifier.gridAutoColumns(block: GridTrackBuilder.() -> Unit) = styleModifier {
    gridAutoColumns(block)
}

fun Modifier.gridAutoColumns(value: GridAuto) = styleModifier {
    gridAutoColumns(value)
}

fun Modifier.gridAutoFlow(value: GridAutoFlow) = styleModifier {
    gridAutoFlow(value)
}

fun Modifier.gridTemplateRows(block: GridTrackBuilder.() -> Unit) = styleModifier {
    gridTemplateRows(block)
}

fun Modifier.gridTemplateRows(value: GridTemplate) = styleModifier {
    gridTemplateRows(value)
}

fun Modifier.gridAutoRows(block: GridTrackBuilder.() -> Unit) = styleModifier {
    gridAutoRows(block)
}

fun Modifier.gridAutoRows(value: GridAuto) = styleModifier {
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

fun Modifier.gridColumn(start: Int, end: Int = start + 1) = styleModifier {
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

fun Modifier.gridRow(start: Int, end: Int = start + 1) = styleModifier {
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

/**
 * A convenience modifier for specifying both row and column indices at the same time.
 *
 * Note that the indices are 1-based, not 0-based.
 */
fun Modifier.gridItem(row: Int, column: Int, width: Int? = null, height: Int? = null) = styleModifier {
    if (height != null) {
        check(height > 0) { "Grid item height must be > 0, got $height" }
        gridRow(row, row + height)
    } else {
        gridRowStart(row)
    }

    if (width != null) {
        check(width > 0) { "Grid item width must be > 0, got $width" }
        gridColumn(column, column + width)
    } else {
        gridColumnStart(column)
    }
}

// endregion grid items
