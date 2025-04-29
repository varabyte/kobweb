package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/flex-basis
class FlexBasis private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<FlexBasis> {
        // Width
        val Auto get() = FlexBasis("auto")

        // Intrinsic sizing
        val MaxContent get() = FlexBasis("max-content")
        val MinContent get() = FlexBasis("min-content")
        val FitContent get() = FlexBasis("fit-content")

        // Content sizing
        val Content get() = FlexBasis("content")
    }
}

fun StyleScope.flexBasis(flexBasis: FlexBasis) {
    property("flex-basis", flexBasis)
}

fun StyleScope.flexBasis(value: CSSLengthOrPercentageNumericValue) {
    property("flex-basis", value)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/flex-direction
val FlexDirection.Companion.Inherit get() = FlexDirection("inherit")
val FlexDirection.Companion.Initial get() = FlexDirection("initial")
val FlexDirection.Companion.Revert get() = FlexDirection("revert")
val FlexDirection.Companion.Unset get() = FlexDirection("unset")
