package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSFilter
import com.varabyte.kobweb.compose.css.global.CssGlobalValues
import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/filter
// See also: CSSFilter
class Filter private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<Filter> {
        // Keyword
        val None get() = Filter("none")
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/backdrop-filter
typealias BackdropFilter = Filter

fun StyleScope.backdropFilter(backdropFilter: BackdropFilter) {
    property("backdrop-filter", backdropFilter)
    property("-webkit-backdrop-filter", backdropFilter) // For safari
}

fun StyleScope.backdropFilter(vararg filters: CSSFilter) {
    if (filters.isNotEmpty()) {
        val backdropFilter = filters.joinToString(" ")
        property("backdrop-filter", backdropFilter)
        property("-webkit-backdrop-filter", backdropFilter) // For safari
    }
}

fun StyleScope.filter(filter: Filter) {
    property("filter", filter)
}

fun StyleScope.filter(vararg filters: CSSFilter) {
    if (filters.isNotEmpty()) {
        property("filter", filters.joinToString(" "))
    }
}
