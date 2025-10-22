package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSFilter
import org.jetbrains.compose.web.css.*

internal sealed interface CssFilterValues<T: StylePropertyValue, L: T> {
    // Keyword
    val None get() = "none".unsafeCast<T>()

    fun of(filter: CSSFilter) = filter.toString().unsafeCast<L>()
    fun list(vararg filters: L) = filters.joinToString(" ").unsafeCast<T>()
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

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/filter
// See also: CSSFilter
sealed interface Filter : StylePropertyValue {
    sealed interface Listable : Filter

    companion object : CssFilterValues<Filter, Listable>, CssGlobalValues<Filter>
}

fun StyleScope.filter(filter: Filter) {
    property("filter", filter)
}
