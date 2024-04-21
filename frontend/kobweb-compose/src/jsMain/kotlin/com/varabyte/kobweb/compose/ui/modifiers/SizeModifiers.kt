package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword

fun Modifier.fillMaxWidth(percent: CSSLengthOrPercentageNumericValue = 100.percent) = styleModifier {
    width(percent)
}

fun Modifier.fillMaxHeight(percent: CSSPercentageNumericValue = 100.percent) = styleModifier {
    height(percent)
}

fun Modifier.fillMaxSize(percent: CSSPercentageNumericValue = 100.percent): Modifier = styleModifier {
    width(percent)
    height(percent)
}

fun Modifier.size(size: CSSLengthOrPercentageNumericValue): Modifier = size(width = size, height = size)

fun Modifier.size(width: CSSLengthOrPercentageNumericValue, height: CSSLengthOrPercentageNumericValue): Modifier =
    styleModifier {
        width(width)
        height(height)
    }

fun Modifier.minSize(size: CSSLengthOrPercentageNumericValue): Modifier = minSize(width = size, height = size)

fun Modifier.minSize(width: CSSLengthOrPercentageNumericValue, height: CSSLengthOrPercentageNumericValue): Modifier =
    styleModifier {
        minWidth(width)
        minHeight(height)
    }

fun Modifier.maxSize(size: CSSLengthOrPercentageNumericValue): Modifier = maxSize(width = size, height = size)

fun Modifier.maxSize(width: CSSLengthOrPercentageNumericValue, height: CSSLengthOrPercentageNumericValue): Modifier =
    styleModifier {
        maxWidth(width)
        maxHeight(height)
    }

fun Modifier.width(size: CSSLengthOrPercentageNumericValue): Modifier = styleModifier {
    width(size)
}

fun Modifier.height(size: CSSLengthOrPercentageNumericValue): Modifier = styleModifier {
    height(size)
}

fun Modifier.width(width: Width): Modifier = styleModifier {
    width(width)
}

fun Modifier.width(auto: CSSAutoKeyword): Modifier = styleModifier {
    width(auto)
}

fun Modifier.height(height: Height): Modifier = styleModifier {
    height(height)
}

fun Modifier.height(auto: CSSAutoKeyword): Modifier = styleModifier {
    height(auto)
}

fun Modifier.minWidth(size: CSSLengthOrPercentageNumericValue): Modifier = styleModifier {
    minWidth(size)
}

fun Modifier.minWidth(minWidth: MinWidth): Modifier = styleModifier {
    minWidth(minWidth)
}

fun Modifier.maxWidth(size: CSSLengthOrPercentageNumericValue): Modifier = styleModifier {
    maxWidth(size)
}

fun Modifier.maxWidth(maxWidth: MaxWidth): Modifier = styleModifier {
    maxWidth(maxWidth)
}

fun Modifier.minHeight(size: CSSLengthOrPercentageNumericValue): Modifier = styleModifier {
    minHeight(size)
}

fun Modifier.minHeight(minHeight: MinHeight): Modifier = styleModifier {
    minHeight(minHeight)
}

fun Modifier.maxHeight(size: CSSLengthOrPercentageNumericValue): Modifier = styleModifier {
    maxHeight(size)
}

fun Modifier.maxHeight(maxHeight: MaxHeight): Modifier = styleModifier {
    maxHeight(maxHeight)
}

fun Modifier.widthIn(min: CSSLengthOrPercentageNumericValue, max: CSSLengthOrPercentageNumericValue): Modifier =
    styleModifier {
        minWidth(min)
        maxWidth(max)
    }

fun Modifier.widthIn(min: MinWidth, max: MaxWidth): Modifier = styleModifier {
    minWidth(min)
    maxWidth(max)
}

fun Modifier.heightIn(min: CSSLengthOrPercentageNumericValue, max: CSSLengthOrPercentageNumericValue): Modifier =
    styleModifier {
        minHeight(min)
        maxHeight(max)
    }

fun Modifier.heightIn(min: MinHeight, max: MaxHeight): Modifier = styleModifier {
    minHeight(min)
    maxHeight(max)
}

fun Modifier.sizeIn(min: CSSLengthOrPercentageNumericValue, max: CSSLengthOrPercentageNumericValue): Modifier =
    minSize(min).maxSize(max)
