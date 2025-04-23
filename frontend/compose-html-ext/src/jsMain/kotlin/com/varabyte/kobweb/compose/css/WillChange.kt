package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/will-change
class WillChange private constructor(private val value: String) : StylePropertyValue {

    companion object {

        /* Keyword values */
        val Auto get() = WillChange("auto")
        val ScrollPosition get() = WillChange("scroll-position")
        val Contents get() = WillChange("contents")

        /* <custom-ident> */
        fun of(customIndent: String) = WillChange(customIndent)

        /* two <animatable-feature> */
        fun of(firstPosition: String, secondPosition: String) = WillChange("$firstPosition, $secondPosition")

        /* Global values */
        val Inherit get() = WillChange("inherit")
        val Initial get() = WillChange("initial")
        val Revert get() = WillChange("revert")
        val RevertLayer get() = WillChange("revert-layer")
        val Unset get() = WillChange("unset")
    }
}

fun StyleScope.willChange(willChange: WillChange) {
    property("will-change", willChange)
}