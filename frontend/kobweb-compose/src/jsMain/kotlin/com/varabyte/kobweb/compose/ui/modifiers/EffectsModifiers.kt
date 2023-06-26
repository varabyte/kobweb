package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.CSSFilter
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

// TODO(#168): Remove before v1.0
@Suppress("DeprecatedCallableAddReplaceWith") // Not a trivial replace
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
)
fun Modifier.backdropFilter(value: String) = styleModifier {
    property("backdrop-filter", value)
}

fun Modifier.backdropFilter(backdropFilter: BackdropFilter) = styleModifier {
    backdropFilter(backdropFilter)
}

fun Modifier.backdropFilter(vararg filters: CSSFilter) = styleModifier {
    if (filters.isNotEmpty()) {
        property("backdrop-filter", filters.joinToString(" "))
    }
}

fun Modifier.filter(filter: Filter) = styleModifier {
    filter(filter)
}

fun Modifier.filter(vararg filters: CSSFilter) = styleModifier {
    if (filters.isNotEmpty()) {
        property("filter", filters.joinToString(" "))
    }
}
