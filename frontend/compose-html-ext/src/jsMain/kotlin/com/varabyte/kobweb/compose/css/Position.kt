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
class Float private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Global
        val Inherit get() = Float("inherit")
        val Initial get() = Float("initial")
        val Revert get() = Float("revert")
        val Unset get() = Float("unset")
    }
}

fun StyleScope.float(float: Float) {
    property("float", float)
}
