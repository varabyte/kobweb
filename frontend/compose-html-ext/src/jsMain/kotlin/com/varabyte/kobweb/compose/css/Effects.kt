// Sealed class private constructors are useful, actually!
@file:Suppress("RedundantVisibilityModifier")

package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSFilter
import com.varabyte.kobweb.compose.css.functions.blur
import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/backdrop-filter
// See also: CSSFilter
sealed class BackdropFilter private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String): BackdropFilter(value)
    class Listable internal constructor(filter: CSSFilter) :
        BackdropFilter(filter.toString())
    private class ValueList(values: List<Listable>) : BackdropFilter(values.joinToString(" "))

    companion object {
        // Keyword
        val None: BackdropFilter get() = Keyword("none")

        fun of(filter: CSSFilter) = Listable(filter)
        fun list(vararg filters: Listable): BackdropFilter = ValueList(filters.toList())

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

@Deprecated("Use `backdropFilter(BackdropFilter.list(...))` instead.", ReplaceWith("BackdropFilter.list(*filters.map { BackdropFilter.of(it) }.toTypedArray())"))
fun StyleScope.backdropFilter(vararg filters: CSSFilter) {
    backdropFilter(BackdropFilter.list(*filters.map { BackdropFilter.of(it) }.toTypedArray()))
}

@Deprecated("Use `backdropFilter(BackdropFilter.list(...))` instead.", ReplaceWith("backdropFilter(BackdropFilter.list(*filters))"))
fun StyleScope.backdropFilter(vararg filters: BackdropFilter.Listable) {
    backdropFilter(BackdropFilter.list(*filters))
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/filter
// See also: CSSFilter
sealed class Filter private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String): Filter(value)
    class Listable internal constructor(filter: CSSFilter) :
        Filter(filter.toString())
    private class ValueList(values: List<Listable>) : Filter(values.joinToString(" "))

    companion object {
        // Keyword
        val None: Filter get() = Keyword("none")

        fun of(filter: CSSFilter) = Listable(filter)
        fun list(vararg filters: CSSFilter): Filter = ValueList(filters.map { of(it) }.toList())
        fun list(vararg filters: Listable): Filter = ValueList(filters.toList())

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

@Deprecated("Use filter(Filter.list(...)) instead.", ReplaceWith("filter(Filter.list(*filters))"))
fun StyleScope.filter(vararg filters: Filter.Listable) {
    filter(Filter.list(*filters))
}

@Deprecated("Use filter(Filter.list(...)) instead.", ReplaceWith("filter(Filter.list(*filters))"))
fun StyleScope.filter(vararg filters: CSSFilter) {
    filter(Filter.list(*filters))
}
