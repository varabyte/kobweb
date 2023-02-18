package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSFilter
import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.css.StyleScope

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/filter
// See also: CSSFilter
class Filter private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val None = Filter("none")

        // Global
        val Inherit = Filter("inherit")
        val Initial = Filter("initial")
        val Revert = Filter("revert")
        val RevertLayer = Filter("revert-layer")
        val Unset = Filter("unset")
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/backdrop-filter
typealias BackdropFilter = Filter

fun StyleScope.backdropFilter(backdropFilter: BackdropFilter) {
    property("backdrop-filter", backdropFilter)
}

fun StyleScope.backdropFilter(vararg filters: CSSFilter) {
    if (filters.isNotEmpty()) {
        property("backdrop-filter", filters.joinToString(" "))
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
