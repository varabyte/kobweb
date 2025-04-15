package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.breakAfter(breakAfter: BreakAfter) = styleModifier {
    breakAfter(breakAfter)
}

fun Modifier.breakBefore(breakBefore: BreakBefore) = styleModifier {
    breakBefore(breakBefore)
}

fun Modifier.breakInside(breakInside: BreakInside) = styleModifier {
    breakInside(breakInside)
}