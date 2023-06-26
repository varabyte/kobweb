package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/flex-basis
class FlexBasis private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Width
        val Auto get() = FlexBasis("auto")

        // Intrinsic sizing
        val MaxContent get() = FlexBasis("max-content")
        val MinContent get() = FlexBasis("min-content")
        val FitContent get() = FlexBasis("fit-content")

        // Content sizing
        val Content get() = FlexBasis("content")

        // Global
        val Inherit get() = FlexBasis("inherit")
        val Initial get() = FlexBasis("initial")
        val Revert get() = FlexBasis("revert")
        val Unset get() = FlexBasis("unset")
    }
}

fun StyleScope.flexBasis(flexBasis: FlexBasis) {
    property("flex-basis", flexBasis)
}

fun StyleScope.flexBasis(value: CSSNumeric) {
    property("flex-basis", value)
}
