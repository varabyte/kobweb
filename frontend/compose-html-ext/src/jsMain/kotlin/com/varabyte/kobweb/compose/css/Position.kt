package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.global.CssGlobalValues
import org.jetbrains.compose.web.css.*

// region horizontal and vertical positions

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/bottom
class Bottom private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<Bottom>
}

fun StyleScope.bottom(bottom: Bottom) {
    property("bottom", bottom)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/top
class Top private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<Top>
}

fun StyleScope.top(top: Top) {
    property("top", top)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/left
class Left private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<Left>
}

fun StyleScope.left(left: Left) {
    property("left", left)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/right
class Right private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<Right>
}

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

    companion object: CssGlobalValues<CSSFloat> {
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
