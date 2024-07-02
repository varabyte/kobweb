package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.CSSFilter
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.backdropFilter(backdropFilter: BackdropFilter) = styleModifier {
    backdropFilter(backdropFilter)
}

fun Modifier.backdropFilter(vararg filters: CSSFilter) = styleModifier {
    backdropFilter(*filters)
}

fun Modifier.filter(filter: Filter) = styleModifier {
    filter(filter)
}

fun Modifier.filter(vararg filters: CSSFilter) = styleModifier {
    filter(*filters)
}
