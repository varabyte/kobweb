// Sealed class private constructors are useful, actually!
@file:Suppress("RedundantVisibilityModifier")

package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*
import kotlin.collections.emptyList

// region Caret Color, see https://developer.mozilla.org/en-US/docs/Web/CSS/caret-color

class CaretColor private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val Auto get() = CaretColor("auto")
        val Transparent get() = CaretColor("transparent")
        val CurrentColor get() = CaretColor("currentcolor")

        // Global
        val Inherit get() = CaretColor("inherit")
        val Initial get() = CaretColor("initial")
        val Revert get() = CaretColor("revert")
        val Unset get() = CaretColor("unset")
    }
}

fun StyleScope.caretColor(caretColor: CaretColor) {
    property("caret-color", caretColor)
}

fun StyleScope.caretColor(color: CSSColorValue) {
    property("caret-color", color)
}

// endregion

// region Touch Action, see https://developer.mozilla.org/en-US/docs/Web/CSS/touch-action

sealed class TouchAction private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String) : TouchAction(value)
    class PanHorizontal internal constructor(value: String) : TouchAction(value)
    class PanVertical internal constructor(value: String) : TouchAction(value)

    companion object {
        @Suppress("FunctionName")
        private fun _of(vararg touchAction: TouchAction, withPinchZoom: Boolean): TouchAction =
            Keyword(
                (touchAction.toList() + if (withPinchZoom) listOf("pinch-zoom") else emptyList())
                    .joinToString(" ")
            )
        fun of(horiz: PanHorizontal, vert: PanVertical, withPinchZoom: Boolean = false) =
            _of(horiz, vert, withPinchZoom = withPinchZoom)
        fun of(horiz: PanHorizontal, withPinchZoom: Boolean = false) = _of(horiz, withPinchZoom = withPinchZoom)
        fun of(vert: PanVertical, withPinchZoom: Boolean = false) = _of(vert, withPinchZoom = withPinchZoom)

        // Keyword
        val Auto get(): TouchAction = Keyword("auto")
        val None get(): TouchAction = Keyword("none")
        val PanX get() = PanHorizontal("pan-x")
        val PanY get() = PanVertical("pan-y")
        val PinchZoom get(): TouchAction = Keyword("pinch-zoom")
// Still experimental: https://caniuse.com/mdn-css_properties_touch-action_unidirectional-pan
//        val PanLeft get(): TouchAction = PanHorizontal("pan-left")
//        val PanRight get(): TouchAction = PanHorizontal("pan-right")
//        val PanUp get(): TouchAction = PanVertical("pan-up")
//        val PanDown get(): TouchAction = PanVertical("pan-down")

        val Manipulation get(): TouchAction = Keyword("manipulation")

        // Global
        val Inherit get(): TouchAction = Keyword("inherit")
        val Initial get(): TouchAction = Keyword("initial")
        val Revert get(): TouchAction = Keyword("revert")
        val Unset get(): TouchAction = Keyword("unset")
    }
}

fun StyleScope.touchAction(touchAction: TouchAction) {
    property("touch-action", touchAction)
}
