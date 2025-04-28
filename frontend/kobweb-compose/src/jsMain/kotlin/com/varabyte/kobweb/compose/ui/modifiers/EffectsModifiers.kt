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

fun Modifier.backdropFilter(filters: List<CSSFilter>) = backdropFilter(*filters.toTypedArray())

fun Modifier.backdropFilter(vararg filters: BackdropFilter.Repeatable) = styleModifier {
    backdropFilter(*filters)
}

fun Modifier.backdropFilter(filters: List<BackdropFilter.Repeatable>) = backdropFilter(*filters.toTypedArray())

fun Modifier.filter(filter: Filter) = styleModifier {
    filter(filter)
}

fun Modifier.filter(vararg filters: CSSFilter) = styleModifier {
    filter(*filters)
}

fun Modifier.filter(filters: List<CSSFilter>) = filter(*filters.toTypedArray())

fun Modifier.filter(vararg filters: Filter.Repeatable) = styleModifier {
    filter(*filters)
}

fun Modifier.filter(filters: List<Filter.Repeatable>) = filter(*filters.toTypedArray())
