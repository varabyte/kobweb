package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

/**
 * A list of enumerated CSS float values.
 *
 * Note: This class is named `CSSFloat` to avoid collision with the Kotlin `Float` class.
 *
 * See: https://developer.mozilla.org/en-US/docs/Web/CSS/float
 */
class CSSFloat private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val Left get() = CSSFloat("left")
        val Right get() = CSSFloat("right")
        val None get() = CSSFloat("none")
        val InlineStart get() = CSSFloat("inline-start")
        val InlineEnd get() = CSSFloat("inline-end")

        // Global
        val Inherit get() = CSSFloat("inherit")
        val Initial get() = CSSFloat("initial")
        val Revert get() = CSSFloat("revert")
        val Unset get() = CSSFloat("unset")
    }
}

fun StyleScope.float(float: CSSFloat) {
    property("float", float)
}
