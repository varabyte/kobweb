package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.FlexBasis
import com.varabyte.kobweb.compose.css.flexBasis
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.flexWrap

@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { flexBasis(value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier",
        "org.jetbrains.compose.web.css.flexBasis"
    ),
)
fun Modifier.flexBasis(value: String): Modifier = styleModifier {
    flexBasis(value)
}

fun Modifier.flexBasis(flexBasis: FlexBasis): Modifier = styleModifier {
    flexBasis(flexBasis)
}

fun Modifier.flexBasis(value: CSSNumeric): Modifier = styleModifier {
    flexBasis(value)
}

fun Modifier.flexDirection(flexDirection: FlexDirection): Modifier = styleModifier {
    flexDirection(flexDirection)
}

fun Modifier.flexWrap(flexWrap: FlexWrap): Modifier = styleModifier {
    flexWrap(flexWrap)
}

fun Modifier.flexFlow(flexDirection: FlexDirection, flexWrap: FlexWrap): Modifier = styleModifier {
    flexFlow(flexDirection, flexWrap)
}

fun Modifier.order(value: Int): Modifier = styleModifier {
    order(value)
}

fun Modifier.flexGrow(value: Number): Modifier = styleModifier {
    flexGrow(value)
}

fun Modifier.flexShrink(value: Number): Modifier = styleModifier {
    flexShrink(value)
}

fun Modifier.flex(value: String): Modifier = styleModifier {
    flex(value)
}

fun Modifier.flex(value: Int): Modifier = styleModifier {
    flex(value)
}

fun Modifier.flex(value: CSSNumeric): Modifier = styleModifier {
    flex(value)
}

fun Modifier.flex(flexGrow: Int, flexBasis: CSSNumeric): Modifier = styleModifier {
    flex(flexGrow, flexBasis)
}

fun Modifier.flex(flexGrow: Int, flexShrink: Int): Modifier = styleModifier {
    flex(flexGrow, flexShrink)
}

fun Modifier.flex(flexGrow: Int, flexShrink: Int, flexBasis: CSSNumeric): Modifier = styleModifier {
    flex(flexGrow, flexShrink, flexBasis)
}

fun Modifier.rowGap(value: CSSNumeric) = styleModifier {
    rowGap(value)
}

fun Modifier.columnGap(value: CSSNumeric) = styleModifier {
    columnGap(value)
}

fun Modifier.gap(value: CSSNumeric) = styleModifier {
    gap(value)
}

fun Modifier.gap(rowGap: CSSNumeric, columnGap: CSSNumeric) = styleModifier {
    gap(rowGap, columnGap)
}