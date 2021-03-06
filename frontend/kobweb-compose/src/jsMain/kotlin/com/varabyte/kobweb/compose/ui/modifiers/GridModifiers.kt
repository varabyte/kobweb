package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

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

fun Modifier.gridTemplateColumns(value: String) = styleModifier {
    gridTemplateColumns(value)
}

fun Modifier.gridAutoColumns(value: String) = styleModifier {
    gridAutoColumns(value)
}

fun Modifier.gridAutoFlow(value: GridAutoFlow) = styleModifier {
    gridAutoFlow(value)
}

fun Modifier.gridTemplateRows(value: String) = styleModifier {
    gridTemplateRows(value)
}

fun Modifier.gridAutoRows(value: String) = styleModifier {
    gridAutoRows(value)
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

fun Modifier.gridTemplateAreas(vararg rows: String) = styleModifier {
    gridTemplateAreas(*rows)
}
