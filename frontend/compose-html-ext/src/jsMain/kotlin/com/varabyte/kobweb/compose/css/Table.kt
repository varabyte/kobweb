package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.global.CssGlobalValues
import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/caption-side
class CaptionSide private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<CaptionSide> {

        // Directional values
        val Top get() = CaptionSide("top")
        val Bottom get() = CaptionSide("bottom")

        // Logical values
        val BlockStart get() = CaptionSide("block-start")
        val BlockEnd get() = CaptionSide("block-end")
        val InlineStart get() = CaptionSide("inline-start")
        val InlineEnd get() = CaptionSide("inline-end")
    }
}

fun StyleScope.captionSide(captionSide: CaptionSide) {
    property("caption-side", captionSide)
}
