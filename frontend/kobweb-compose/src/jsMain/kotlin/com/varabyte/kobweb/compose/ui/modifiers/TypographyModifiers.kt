package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.widows(widows: Widows) = styleModifier {
    widows(widows)
}

fun Modifier.widows(numLines: Int) = styleModifier {
    widows(Widows.of(numLines))
}

fun Modifier.orphans(orphans: Orphans) = styleModifier {
    orphans(orphans)
}

fun Modifier.orphans(value: Int) = styleModifier {
    orphans(Orphans.of(value))
}