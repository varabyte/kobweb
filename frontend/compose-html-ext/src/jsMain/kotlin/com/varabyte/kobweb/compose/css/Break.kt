package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See https://developer.mozilla.org/en-US/docs/Web/CSS/break-after
class BreakAfter private constructor(private val value: String) : StylePropertyValue {

    override fun toString() = value

    companion object {

        /* Generic break values */
        val Auto = BreakAfter("auto")
        val Avoid = BreakAfter("avoid")
        val Always = BreakAfter("always")
        val All = BreakAfter("all")

        /* Page break values */
        val AvoidPage get() = BreakAfter("avoid-page")
        val Page get() = BreakAfter("page")
        val Left get() = BreakAfter("left")
        val Right get() = BreakAfter("right")
        val Recto get() = BreakAfter("recto")
        val Verso get() = BreakAfter("verso")

        /* Column break values */
        val Column = BreakAfter("column")
        val AvoidColumn = BreakAfter("avoid-column")

        /* Region break values */
        val Region = BreakAfter("region")
        val AvoidRegion = BreakAfter("avoid-region")

        /* Global values */
        val Inherit = BreakAfter("inherit")
        val Initial = BreakAfter("initial")
        val Revert = BreakAfter("revert")
        val RevertLayer = BreakAfter("revert-layer")
        val Unset = BreakAfter("unset")
    }
}

fun StyleScope.breakAfter(breakAfter: BreakAfter) {
    property("break-after", breakAfter)
}

// See https://developer.mozilla.org/en-US/docs/Web/CSS/break-before
class BreakBefore private constructor(private val value: String) : StylePropertyValue {

    override fun toString() = value

    companion object {

        /* Generic break values */
        val Auto get() = BreakBefore("auto")
        val Avoid get() = BreakBefore("avoid")
        val Always get() = BreakBefore("always")
        val All get() = BreakBefore("all")

        /* Page break values */
        val AvoidPage get() = BreakBefore("avoid-page")
        val Page get() = BreakBefore("page")
        val Left get() = BreakBefore("left")
        val Right get() = BreakBefore("right")
        val Recto get() = BreakBefore("recto")
        val Verso get() = BreakBefore("verso")

        /* Column break values */
        val Column = BreakBefore("column")
        val AvoidColumn = BreakBefore("avoid-column")

        /* Region break values */
        val Region = BreakBefore("region")
        val AvoidRegion = BreakBefore("avoid-region")

        /* Global values */
        val Inherit get() = BreakBefore("inherit")
        val Initial get() = BreakBefore("initial")
        val Revert get() = BreakBefore("revert")
        val RevertLayer get() = BreakBefore("revert-layer")
        val Unset get() = BreakBefore("unset")
    }
}

fun StyleScope.breakBefore(breakBefore: BreakBefore) {
    property("break-before", breakBefore)
}

//See https://developer.mozilla.org/en-US/docs/Web/CSS/break-inside
class BreakInside private constructor(private val value: String) : StylePropertyValue {

    override fun toString() = value

    companion object {

        /* Keyword values */
        val Auto get() = BreakInside("auto")
        val Avoid get() = BreakInside("avoid")
        val AvoidPage get() = BreakInside("avoid-page")
        val AvoidColumn = BreakInside("avoid-column")
        val AvoidRegion = BreakInside("avoid-region")

        /* Global values */
        val Inherit get() = BreakInside("inherit")
        val Initial get() = BreakInside("initial")
        val Revert get() = BreakInside("revert")
        val RevertLayer get() = BreakInside("revert-layer")
        val Unset get() = BreakInside("unset")
    }
}

fun StyleScope.breakInside(breakInside: BreakInside) {
    property("break-inside", breakInside)
}