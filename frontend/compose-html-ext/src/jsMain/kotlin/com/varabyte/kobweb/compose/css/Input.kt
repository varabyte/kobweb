package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// region Caret Color, see https://developer.mozilla.org/en-US/docs/Web/CSS/caret-color

sealed interface CaretColor : StylePropertyValue {
    companion object : CssGlobalValues<CaretColor> {
        fun of(color: CSSColorValue) = color.unsafeCast<CaretColor>()

        // Keyword
        val Auto get() = "auto".unsafeCast<CaretColor>()
    }
}

fun StyleScope.caretColor(caretColor: CaretColor) {
    property("caret-color", caretColor)
}

// endregion

// region Touch Action, see https://developer.mozilla.org/en-US/docs/Web/CSS/touch-action
sealed interface TouchAction : StylePropertyValue {
    sealed interface PanHorizontal : TouchAction
    sealed interface PanVertical : TouchAction

    companion object : CssGlobalValues<TouchAction> {
        @Suppress("FunctionName")
        private fun _of(vararg touchAction: TouchAction, pinchZoom: Boolean): TouchAction =
            (touchAction.toList() + if (pinchZoom) listOf("pinch-zoom") else emptyList())
                .joinToString(" ").unsafeCast<TouchAction>()

        fun of(horizontal: PanHorizontal, vertical: PanVertical, pinchZoom: Boolean = false) =
            _of(horizontal, vertical, pinchZoom = pinchZoom)
        fun of(horizontal: PanHorizontal, pinchZoom: Boolean = false) = _of(horizontal, pinchZoom = pinchZoom)
        fun of(vertical: PanVertical, pinchZoom: Boolean = false) = _of(vertical, pinchZoom = pinchZoom)

        // Keyword
        val Auto get() = "auto".unsafeCast<TouchAction>()
        val None get() = "none".unsafeCast<TouchAction>()
        val PanX get() = "pan-x".unsafeCast<PanHorizontal>()
        val PanY get() = "pan-y".unsafeCast<PanVertical>()
        val PinchZoom get() = "pinch-zoom".unsafeCast<TouchAction>()
// Still experimental: https://caniuse.com/mdn-css_properties_touch-action_unidirectional-pan
//        val PanLeft get() = "pan-left".unsafeCast<PanHorizontal>()
//        val PanRight get() = "pan-right".unsafeCast<PanHorizontal>()
//        val PanUp get() = "pan-up".unsafeCast<PanVertical>()
//        val PanDown get() = "pan-down".unsafeCast<PanVertical>()
        val Manipulation get() = "manipulation".unsafeCast<TouchAction>()
    }
}

fun StyleScope.touchAction(touchAction: TouchAction) {
    property("touch-action", touchAction)
}
