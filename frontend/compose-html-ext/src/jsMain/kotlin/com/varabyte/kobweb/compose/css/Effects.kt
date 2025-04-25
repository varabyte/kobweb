// Sealed class private constructors are useful, actually!
@file:Suppress("RedundantVisibilityModifier")

package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSFilter
import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/filter
// See also: CSSFilter
sealed class Filter private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String): Filter(value)
    class Repeatable internal constructor(first: CSSFilter, vararg rest: CSSFilter) :
        Filter((listOf(first) + rest).joinToString(" "))

    companion object {
        // Keyword
        val None: Filter get() = Keyword("none")

        fun of(first: CSSFilter, vararg rest: CSSFilter): Filter = Repeatable(first, *rest)

        // Global
        val Inherit: Filter get() = Keyword("inherit")
        val Initial: Filter get() = Keyword("initial")
        val Revert: Filter get() = Keyword("revert")
        val Unset: Filter get() = Keyword("unset")
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
