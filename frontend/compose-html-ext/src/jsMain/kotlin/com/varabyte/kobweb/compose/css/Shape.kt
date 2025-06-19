package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/shape-margin
sealed interface ShapeMargin : StylePropertyValue {
    companion object : CssGlobalValues<ShapeMargin> {
        fun of(value: CSSLengthOrPercentageNumericValue) = value.unsafeCast<ShapeMargin>()
    }
}

fun StyleScope.shapeMargin(shapeMargin: ShapeMargin) {
    property("shape-margin", shapeMargin)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/shape-rendering
sealed interface ShapeRendering : StylePropertyValue {
    companion object : CssGlobalValues<ShapeRendering> {
        val Auto get() = "auto".unsafeCast<ShapeRendering>()
        val CrispEdges get() = "crispEdges".unsafeCast<ShapeRendering>()
        val GeometricPrecision get() = "geometricPrecision".unsafeCast<ShapeRendering>()
        val OptimizeSpeed get() = "optimizeSpeed".unsafeCast<ShapeRendering>()
    }
}

fun StyleScope.shapeRendering(shapeRendering: ShapeRendering) {
    property("shape-rendering", shapeRendering)
}