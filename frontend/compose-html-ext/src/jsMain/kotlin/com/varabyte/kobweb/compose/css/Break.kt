package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

internal interface CssBreakValues<T: StylePropertyValue> {
    // Generic break values
    val Auto get() = "auto".unsafeCast<T>()
    val Avoid get() = "avoid".unsafeCast<T>()
    val Always get() = "always".unsafeCast<T>()
    val All get() = "all".unsafeCast<T>()

    // Page break values
    val AvoidPage get() = "avoid-page".unsafeCast<T>()
    val Page get() = "page".unsafeCast<T>()
    val Left get() = "left".unsafeCast<T>()
    val Right get() = "right".unsafeCast<T>()
    val Recto get() = "recto".unsafeCast<T>()
    val Verso get() = "verso".unsafeCast<T>()

    // Column break values
    val Column get() = "column".unsafeCast<T>()
    val AvoidColumn get() = "avoid-column".unsafeCast<T>()

    // Region break values
    val Region get() = "region".unsafeCast<T>()
    val AvoidRegion get() = "avoid-region".unsafeCast<T>()
}

// See https://developer.mozilla.org/en-US/docs/Web/CSS/break-after
class BreakAfter private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssBreakValues<BreakAfter>, CssGlobalValues<BreakAfter>
}

fun StyleScope.breakAfter(breakAfter: BreakAfter) {
    property("break-after", breakAfter)
}

// See https://developer.mozilla.org/en-US/docs/Web/CSS/break-before
sealed interface BreakBefore : StylePropertyValue {
    companion object : CssBreakValues<BreakBefore>, CssGlobalValues<BreakBefore>
}

fun StyleScope.breakBefore(breakBefore: BreakBefore) {
    property("break-before", breakBefore)
}

//See https://developer.mozilla.org/en-US/docs/Web/CSS/break-inside
sealed interface BreakInside : StylePropertyValue {
    companion object : CssGlobalValues<BreakInside> {
        // Keyword values
        val Auto get() = "auto".unsafeCast<BreakInside>()
        val Avoid get() = "avoid".unsafeCast<BreakInside>()
        val AvoidPage get() = "avoid-page".unsafeCast<BreakInside>()
        val AvoidColumn get() = "avoid-column".unsafeCast<BreakInside>()
        val AvoidRegion get() = "avoid-region".unsafeCast<BreakInside>()
    }
}

fun StyleScope.breakInside(breakInside: BreakInside) {
    property("break-inside", breakInside)
}