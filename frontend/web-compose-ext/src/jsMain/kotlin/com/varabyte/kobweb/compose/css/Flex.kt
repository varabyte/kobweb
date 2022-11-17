package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/flex-basis
class FlexBasis private constructor(val value: StylePropertyValue) {
    private constructor(value: String) : this(StylePropertyValue(value))
    constructor(value: CSSNumeric) : this(value.unsafeCast<StylePropertyValue>())

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
        val RevertLayer get() = FlexBasis("revert-layer")
        val Unset get() = FlexBasis("unset")
    }
}

@Deprecated("This factory method is no longer required. Construct a FlexBasis directly instead.",
    ReplaceWith("FlexBasis(value)", "com.varabyte.kobweb.compose.css.FlexBasis"),
)
fun NumericFlexBasis(value: CSSNumeric) = FlexBasis(value)

fun StyleScope.flexBasis(flexBasis: FlexBasis) {
    property("flex-basis", flexBasis.value)
}