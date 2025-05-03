package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.CSSFilter
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.backdropFilter(backdropFilter: BackdropFilter) = styleModifier {
    backdropFilter(backdropFilter)
}

fun Modifier.backdropFilter(vararg filters: CSSFilter) = styleModifier {
    backdropFilter(BackdropFilter.list(*filters.map { BackdropFilter.of(it) }.toTypedArray()))
}

fun Modifier.backdropFilter(filters: List<CSSFilter>) = backdropFilter(*filters.toTypedArray())

fun Modifier.backdropFilter(vararg filters: BackdropFilter.Listable) = styleModifier {
    backdropFilter(BackdropFilter.list(*filters))
}

fun Modifier.backdropFilter(filters: List<BackdropFilter.Listable>) = backdropFilter(*filters.toTypedArray())

fun Modifier.filter(filter: Filter) = styleModifier {
    filter(filter)
}

fun Modifier.filter(vararg filters: CSSFilter) = styleModifier {
    filter(Filter.list(*filters.map { Filter.of(it) }.toTypedArray()))
}

fun Modifier.filter(filters: List<CSSFilter>) = filter(*filters.toTypedArray())

fun Modifier.filter(vararg filters: Filter.Listable) = styleModifier {
    filter(Filter.list(*filters))
}

fun Modifier.filter(filters: List<Filter.Listable>) = filter(*filters.toTypedArray())
