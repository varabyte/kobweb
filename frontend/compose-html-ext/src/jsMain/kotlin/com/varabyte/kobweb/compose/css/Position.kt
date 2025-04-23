package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// region horizontal and vertical positions

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/bottom
class Bottom private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Global
        val Inherit get() = Bottom("inherit")
        val Initial get() = Bottom("initial")
        val Revert get() = Bottom("revert")
        val Unset get() = Bottom("unset")
    }
}

fun StyleScope.bottom(bottom: Bottom) {
    property("bottom", bottom)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/top
class Top private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Global
        val Inherit get() = Top("inherit")
        val Initial get() = Top("initial")
        val Revert get() = Top("revert")
        val Unset get() = Top("unset")
    }
}

fun StyleScope.top(top: Top) {
    property("top", top)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/left
class Left private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Global
        val Inherit get() = Left("inherit")
        val Initial get() = Left("initial")
        val Revert get() = Left("revert")
        val Unset get() = Left("unset")
    }
}

fun StyleScope.left(left: Left) {
    property("left", left)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/right
class Right private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Global
        val Inherit get() = Right("inherit")
        val Initial get() = Right("initial")
        val Revert get() = Right("revert")
        val Unset get() = Right("unset")
    }
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

//https://developer.mozilla.org/en-US/docs/Web/CSS/ruby-position
class RubyPosition private constructor(private val value: String) : StylePropertyValue {

    override fun toString() = value

    companion object {

        /* Keyword values */
        val Over get() = RubyPosition("over")
        val Under get() = RubyPosition("under")
        val Alternate get() = RubyPosition("alternate")
        val AlternateOver get() = RubyPosition("alternate over")
        val AlternateUnder get() = RubyPosition("alternate under")
        val InterCharacter get() = RubyPosition("inter-character")


        /* Global values */
        val Inherit get() = RubyPosition("inherit")
        val Initial get() = RubyPosition("initial")
        val Revert get() = RubyPosition("revert")
        val RevertLayer get() = RubyPosition("revert-layer")
        val Unset get() = RubyPosition("unset")
    }
}

fun StyleScope.rubyPosition(rubyPosition: RubyPosition) {
    property("ruby-position", rubyPosition)
}
