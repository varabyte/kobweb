package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.captionSide(captionSide: CaptionSide) : Modifier = styleModifier {
    captionSide(captionSide)
}