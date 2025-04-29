package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// region horizontal and vertical positions

// NOTE: This class is used as a typealias and should not be referenced directly by the end user.
// TODO: In a future refactoring, this will likely change into an interface instead using unsafe casts; we want to wait until
//  all tests are done before doing this.
class CSSElementPosition private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<CSSElementPosition> {
        fun of(value: CSSLengthOrPercentageNumericValue) = CSSElementPosition("$value")
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/bottom
typealias Bottom = CSSElementPosition

fun StyleScope.bottom(bottom: Bottom) {
    property("bottom", bottom)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/top
typealias Top = CSSElementPosition

fun StyleScope.top(top: Top) {
    property("top", top)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/left
typealias Left = CSSElementPosition

fun StyleScope.left(left: Left) {
    property("left", left)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/right
typealias Right = CSSElementPosition

fun StyleScope.right(right: Right) {
    property("right", right)
}

// endregion

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/float
/**
 * A list of enumerated CSS float values.
 *
 * Note: This class is named `CSSFloat` to avoid collision with the Kotlin `Float` class.
 *
 * See: https://developer.mozilla.org/en-US/docs/Web/CSS/float
 */
class CSSFloat private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<CSSFloat> {
        // Keyword
        val Left get() = CSSFloat("left")
        val Right get() = CSSFloat("right")
        val None get() = CSSFloat("none")
        val InlineStart get() = CSSFloat("inline-start")
        val InlineEnd get() = CSSFloat("inline-end")
    }
}

fun StyleScope.float(float: CSSFloat) {
    property("float", float)
}
