package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSFilter
import org.jetbrains.compose.web.css.*

internal sealed interface CssFilterValues<T: StylePropertyValue, L: T> {
    // Keyword
    val None get() = "none".unsafeCast<T>()

    fun of(filter: CSSFilter) = filter.toString().unsafeCast<L>()
    fun list(vararg filters: L) = filters.toList().joinToString(" ").unsafeCast<T>()
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/backdrop-filter
// See also: CSSFilter
sealed interface BackdropFilter : StylePropertyValue {
    sealed interface Listable : BackdropFilter

    companion object : CssFilterValues<BackdropFilter, Listable>, CssGlobalValues<BackdropFilter>
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
sealed interface Filter : StylePropertyValue {
    sealed interface Listable : Filter

    companion object : CssFilterValues<Filter, Listable>, CssGlobalValues<Filter>
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
    filter(Filter.list(*filters.map { Filter.of(it) }.toTypedArray()))
}
