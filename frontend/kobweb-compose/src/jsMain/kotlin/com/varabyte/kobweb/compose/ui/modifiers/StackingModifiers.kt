package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.Isolation
import com.varabyte.kobweb.compose.css.isolation
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.isolation(isolation: Isolation) = styleModifier {
    isolation(isolation)
}
