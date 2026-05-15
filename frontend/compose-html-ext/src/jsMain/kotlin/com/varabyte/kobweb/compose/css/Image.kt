package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/image-orientation
sealed interface ImageOrientation : StylePropertyValue {
    companion object : CssGlobalValues<ImageOrientation> {
        // Keyword
        val None get() = "none".unsafeCast<ImageOrientation>()
        val FromImage get() = "from-image".unsafeCast<ImageOrientation>()
    }
}

fun StyleScope.imageOrientation(imageOrientation: ImageOrientation) {
    property("image-orientation", imageOrientation)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/image-rendering
sealed interface ImageRendering : StylePropertyValue {
    companion object : CssGlobalValues<ImageRendering> {
        // Keyword
        val Auto get() = "auto".unsafeCast<ImageRendering>()
        val CrispEdges get() = "crisp-edges".unsafeCast<ImageRendering>()
        val Pixelated get() = "pixelated".unsafeCast<ImageRendering>()
    }
}

fun StyleScope.imageRendering(imageRendering: ImageRendering) {
    property("image-rendering", imageRendering)
}
