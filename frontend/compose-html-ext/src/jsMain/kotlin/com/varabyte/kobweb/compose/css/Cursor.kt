package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/cursor
sealed interface Cursor : StylePropertyValue {
    companion object : CssGlobalValues<Cursor> {
        // General
        val Auto get() = "auto".unsafeCast<Cursor>()
        val Default get() = "default".unsafeCast<Cursor>()
        val None get() = "none".unsafeCast<Cursor>()

        // Links and status
        val ContextMenu get() = "context-menu".unsafeCast<Cursor>()
        val Help get() = "help".unsafeCast<Cursor>()
        val Pointer get() = "pointer".unsafeCast<Cursor>()
        val Progress get() = "progress".unsafeCast<Cursor>()
        val Wait get() = "wait".unsafeCast<Cursor>()

        // Selection
        val Cell get() = "cell".unsafeCast<Cursor>()
        val Crosshair get() = "crosshair".unsafeCast<Cursor>()
        val Text get() = "text".unsafeCast<Cursor>()
        val VerticalText get() = "vertical-text".unsafeCast<Cursor>()

        // Drag and drop
        val Alias get() = "alias".unsafeCast<Cursor>()
        val Copy get() = "copy".unsafeCast<Cursor>()
        val Move get() = "move".unsafeCast<Cursor>()
        val NoDrop get() = "no-drop".unsafeCast<Cursor>()
        val NotAllowed get() = "not-allowed".unsafeCast<Cursor>()
        val Grab get() = "grab".unsafeCast<Cursor>()
        val Grabbing get() = "grabbing".unsafeCast<Cursor>()

        // Resizing and scrolling
        val AllScroll get() = "all-scroll".unsafeCast<Cursor>()
        val ColumnResize get() = "col-resize".unsafeCast<Cursor>()
        val RowResize get() = "row-resize".unsafeCast<Cursor>()
        val NResize get() = "n-resize".unsafeCast<Cursor>()
        val NeResize get() = "ne-resize".unsafeCast<Cursor>()
        val EResize get() = "e-resize".unsafeCast<Cursor>()
        val SeResize get() = "se-resize".unsafeCast<Cursor>()
        val SResize get() = "s-resize".unsafeCast<Cursor>()
        val SwResize get() = "sw-resize".unsafeCast<Cursor>()
        val WResize get() = "w-resize".unsafeCast<Cursor>()
        val NwResize get() = "nw-resize".unsafeCast<Cursor>()
        val EwResize get() = "ew-resize".unsafeCast<Cursor>()
        val NsResize get() = "ns-resize".unsafeCast<Cursor>()
        val NeswResize get() = "nesw-resize".unsafeCast<Cursor>()
        val NwseResize get() = "nwse-resize".unsafeCast<Cursor>()

        // Zoom
        val ZoomIn get() = "zoom-in".unsafeCast<Cursor>()
        val ZoomOut get() = "zoom-out".unsafeCast<Cursor>()
    }
}

fun StyleScope.cursor(cursor: Cursor) {
    property("cursor", cursor)
}
