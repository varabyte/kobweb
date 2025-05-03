package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/caption-side
sealed interface CaptionSide : StylePropertyValue {
    companion object : CssGlobalValues<CaptionSide> {
        // Directional values
        val Top get() = "top".unsafeCast<CaptionSide>()
        val Bottom get() = "bottom".unsafeCast<CaptionSide>()

        // Logical values
        val BlockStart get() = "block-start".unsafeCast<CaptionSide>()
        val BlockEnd get() = "block-end".unsafeCast<CaptionSide>()
        val InlineStart get() = "inline-start".unsafeCast<CaptionSide>()
        val InlineEnd get() = "inline-end".unsafeCast<CaptionSide>()
    }
}

fun StyleScope.captionSide(captionSide: CaptionSide) {
    property("caption-side", captionSide)
}
