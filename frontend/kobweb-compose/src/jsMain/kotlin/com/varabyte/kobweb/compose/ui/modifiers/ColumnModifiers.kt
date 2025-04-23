package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.columnCount(columnCount: ColumnCount) = styleModifier {
    columnCount(columnCount)
}

fun Modifier.columnCount(count: Int) = styleModifier {
    columnCount(count)
}

fun Modifier.columnFill(columnFill: ColumnFill) = styleModifier {
    columnFill(columnFill)
}

fun Modifier.columnSpan(columnSpan: ColumnSpan) = styleModifier {
    columnSpan(columnSpan)
}
