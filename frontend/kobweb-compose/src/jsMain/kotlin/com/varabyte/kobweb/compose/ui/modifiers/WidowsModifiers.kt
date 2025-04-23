package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.windows(windows: Widows) = styleModifier {
    windows(windows)
}

fun Modifier.windows(noOfWidows: Int) = styleModifier {
    windows(Widows.of(noOfWidows))
}