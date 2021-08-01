package kobweb.compose.css

import org.jetbrains.compose.web.css.StyleBuilder

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/cursor
enum class Cursor(val value: String) {
    // General
    AUTO("auto"),
    DEFAULT("default"),
    NONE("none"),

    // Links and status
    CONTEXT_MENU("context-menu"),
    HELP("help"),
    POINTER("pointer"),
    PROGRESS("progress"),
    WAIT("wait"),

    // Selection
    CELL("cell"),
    CROSSHAIR("crosshair"),
    TEXT("text"),
    VERTICAL_TEXT("vertical-text"),

    // Drag and drop
    ALIAS("alias"),
    COPY("copy"),
    MOVE("move"),
    NO_DROP("no-drop"),
    NOT_ALLOWED("not-allowed"),
    GRAB("grab"),
    GRABBING("grabbing"),

    // Resizing and scrolling
    ALL_SCROLL("all-scroll"),
    COLUMN_RESIZE("col-resize"),
    ROW_RESIZE("row-resize"),
    N_RESIZE("n-resize"),
    NE_RESIZE("ne-resize"),
    E_RESIZE("e-resize"),
    SE_RESIZE("se-resize"),
    S_RESIZE("s-resize"),
    SW_RESIZE("sw-resize"),
    W_RESIZE("w-resize"),
    NW_RESIZE("nw-resize"),
    EW_RESIZE("ew-resize"),
    NS_RESIZE("ns-resize"),
    NESW_RESIZE("nesw-resize"),
    NWSE_RESIZE("nwse-resize"),

    // Zoom
    ZOOM_IN("zoom-in"),
    ZOOM_OUT("zoom-out"),
}

fun StyleBuilder.cursor(cursor: Cursor) {
    property("cursor", cursor.value)
}