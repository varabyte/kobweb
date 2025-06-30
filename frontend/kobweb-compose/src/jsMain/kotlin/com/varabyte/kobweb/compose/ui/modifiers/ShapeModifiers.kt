package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.shapeMargin(shapeMargin: ShapeMargin) = styleModifier {
    shapeMargin(shapeMargin)
}

fun Modifier.shapeMargin(value: CSSLengthOrPercentageNumericValue) = styleModifier {
    shapeMargin(ShapeMargin.of(value))
}

fun Modifier.shapeRendering(shapeRendering: ShapeRendering) = styleModifier {
    shapeRendering(shapeRendering)
}