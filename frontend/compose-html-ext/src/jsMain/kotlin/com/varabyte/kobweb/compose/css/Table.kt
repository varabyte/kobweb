package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/caption-side
class CaptionSide private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {

        // Directional values
        val Top get() = CaptionSide("top")
        val Bottom get() = CaptionSide("bottom")

        // Logical values
        val BlockStart get() = CaptionSide("block-start")
        val BlockEnd get() = CaptionSide("block-end")
        val InlineStart get() = CaptionSide("inline-start")
        val InlineEnd get() = CaptionSide("inline-end")

        // Global values
        val Inherit get() = CaptionSide("inherit")
        val Initial get() = CaptionSide("initial")
        val Revert get() = CaptionSide("revert")
        val RevertLayer get() = CaptionSide("revert-layer")
        val Unset get() = CaptionSide("unset")
    }
}

fun StyleScope.captionSide(captionSide: CaptionSide) {
    property("caption-side", captionSide)
}