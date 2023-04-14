package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/cursor
class Cursor private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // General
        val Auto get() = Cursor("auto")
        val Default get() = Cursor("default")
        val None get() = Cursor("none")

        // Links and status
        val ContextMenu get() = Cursor("context-menu")
        val Help get() = Cursor("help")
        val Pointer get() = Cursor("pointer")
        val Progress get() = Cursor("progress")
        val Wait get() = Cursor("wait")

        // Selection
        val Cell get() = Cursor("cell")
        val Crosshair get() = Cursor("crosshair")
        val Text get() = Cursor("text")
        val VerticalText get() = Cursor("vertical-text")

        // Drag and drop
        val Alias get() = Cursor("alias")
        val Copy get() = Cursor("copy")
        val Move get() = Cursor("move")
        val NoDrop get() = Cursor("no-drop")
        val NotAllowed get() = Cursor("not-allowed")
        val Grab get() = Cursor("grab")
        val Grabbing get() = Cursor("grabbing")

        // Resizing and scrolling
        val AllScroll get() = Cursor("all-scroll")
        val ColumnResize get() = Cursor("col-resize")
        val RowResize get() = Cursor("row-resize")
        val NResize get() = Cursor("n-resize")
        val NeResize get() = Cursor("ne-resize")
        val EResize get() = Cursor("e-resize")
        val SeResize get() = Cursor("se-resize")
        val SResize get() = Cursor("s-resize")
        val SwResize get() = Cursor("sw-resize")
        val WResize get() = Cursor("w-resize")
        val NwResize get() = Cursor("nw-resize")
        val EwResize get() = Cursor("ew-resize")
        val NsResize get() = Cursor("ns-resize")
        val NeswResize get() = Cursor("nesw-resize")
        val NwseResize get() = Cursor("nwse-resize")

        // Zoom
        val ZoomIn get() = Cursor("zoom-in")
        val ZoomOut get() = Cursor("zoom-out")
    }
}

fun StyleScope.cursor(cursor: Cursor) {
    property("cursor", cursor)
}