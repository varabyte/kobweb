package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/column-count

class ColumnCount private constructor(private val value: String) : StylePropertyValue {

    companion object {

        /* Keyword value */
        val Auto get() = ColumnCount("auto")

        /* <integer> value */
        fun of(count: Int) = ColumnCount("$count")

        /* Global values */
        val Inherit get() = ColumnCount("inherit")
        val Initial get() = ColumnCount("initial")
        val Revert get() = ColumnCount("revert")
        val RevertLayer get() = ColumnCount("revert-layer")
        val Unset get() = ColumnCount("unset")
    }
}

fun StyleScope.columnCount(columnCount: ColumnCount) {
    property("column-count", columnCount)
}

fun StyleScope.columnCount(count: Int) {
    property("column-count", count)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/column-fill
class ColumnFill private constructor(private val value: String) : StylePropertyValue {

    companion object {

        /* Keyword value */
        val Auto get() = ColumnFill("auto")
        val Balance get() = ColumnFill("balance")

        /* Global values */
        val Inherit get() = ColumnFill("inherit")
        val Initial get() = ColumnFill("initial")
        val Revert get() = ColumnFill("revert")
        val RevertLayer get() = ColumnFill("revert-layer")
        val Unset get() = ColumnFill("unset")
    }
}

fun StyleScope.columnFill(columnFill: ColumnFill) {
    property("column-fill", columnFill)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/column-span
class ColumnSpan private constructor(private val value: String) : StylePropertyValue {

    companion object {

        /* Keyword value */
        val None get() = ColumnSpan("none")
        val All get() = ColumnSpan("all")

        /* Global values */
        val Inherit get() = ColumnSpan("inherit")
        val Initial get() = ColumnSpan("initial")
        val Revert get() = ColumnSpan("revert")
        val RevertLayer get() = ColumnSpan("revert-layer")
        val Unset get() = ColumnSpan("unset")
    }
}

fun StyleScope.columnSpan(columnSpan: ColumnSpan) {
    property("column-span", columnSpan)
}
