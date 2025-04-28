// Sealed class private constructors are useful, actually!
@file:Suppress("RedundantVisibilityModifier")

package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSFilter
import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/backdrop-filter
// See also: CSSFilter
sealed class BackdropFilter private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String): BackdropFilter(value)
    class Repeatable internal constructor(filter: CSSFilter) :
        BackdropFilter(filter.toString())

    companion object {
        // Keyword
        val None: BackdropFilter get() = Keyword("none")

        fun of(filter: CSSFilter) = Repeatable(filter)

        // Global
        val Inherit: BackdropFilter get() = Keyword("inherit")
        val Initial: BackdropFilter get() = Keyword("initial")
        val Revert: BackdropFilter get() = Keyword("revert")
        val Unset: BackdropFilter get() = Keyword("unset")
    }
}

fun StyleScope.backdropFilter(backdropFilter: BackdropFilter) {
    property("backdrop-filter", backdropFilter)
    property("-webkit-backdrop-filter", backdropFilter) // For safari
}

fun StyleScope.backdropFilter(vararg filters: CSSFilter) {
    backdropFilter(*filters.map { BackdropFilter.of(it) }.toTypedArray())
}

fun StyleScope.backdropFilter(vararg filters: BackdropFilter.Repeatable) {
    if (filters.isNotEmpty()) {
        val backdropFilter = filters.joinToString(" ")
        property("backdrop-filter", backdropFilter)
        property("-webkit-backdrop-filter", backdropFilter) // For safari
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/filter
// See also: CSSFilter
sealed class Filter private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String): Filter(value)
    class Repeatable internal constructor(filter: CSSFilter) :
        Filter(filter.toString())

    companion object {
        // Keyword
        val None: Filter get() = Keyword("none")

        fun of(filter: CSSFilter) = Repeatable(filter)

        // Global
        val Inherit: Filter get() = Keyword("inherit")
        val Initial: Filter get() = Keyword("initial")
        val Revert: Filter get() = Keyword("revert")
        val Unset: Filter get() = Keyword("unset")
    }
}

fun StyleScope.filter(filter: Filter) {
    property("filter", filter)
}

fun StyleScope.filter(vararg filters: Filter.Repeatable) {
    if (filters.isNotEmpty()) {
        property("filter", filters.joinToString(" "))
    }
}

fun StyleScope.filter(vararg filters: CSSFilter) {
    filter(*filters.map { Filter.of(it) }.toTypedArray())
}
