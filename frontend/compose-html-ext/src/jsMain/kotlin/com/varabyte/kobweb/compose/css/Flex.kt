package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/flex-basis
sealed interface FlexBasis : StylePropertyValue {
    companion object : CssGlobalValues<FlexBasis> {
        // Width
        val Auto get() = "auto".unsafeCast<FlexBasis>()

        // Intrinsic sizing
        val MaxContent get() = "max-content".unsafeCast<FlexBasis>()
        val MinContent get() = "min-content".unsafeCast<FlexBasis>()
        val FitContent get() = "fit-content".unsafeCast<FlexBasis>()

        // Content sizing
        val Content get() = "content".unsafeCast<FlexBasis>()
    }
}

fun StyleScope.flexBasis(flexBasis: FlexBasis) {
    property("flex-basis", flexBasis)
}

fun StyleScope.flexBasis(value: CSSLengthOrPercentageNumericValue) {
    property("flex-basis", value)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/flex-direction
val FlexDirection.Companion.Inherit get() = "inherit".unsafeCast<FlexDirection>()
val FlexDirection.Companion.Initial get() = "initial".unsafeCast<FlexDirection>()
val FlexDirection.Companion.Revert get() = "revert".unsafeCast<FlexDirection>()
val FlexDirection.Companion.Unset get() = "unset".unsafeCast<FlexDirection>()
