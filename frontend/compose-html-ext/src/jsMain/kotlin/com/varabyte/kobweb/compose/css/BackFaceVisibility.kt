package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

class BackFaceVisibility private constructor(private val value: String) : StylePropertyValue {

    override fun toString() = value

    companion object {

        /* Keyword values */
        val Visible get() = BackFaceVisibility("visible")
        val Hidden get() = BackFaceVisibility("hidden")

        /* Global values */
        val Inherit get() = BackFaceVisibility("inherit")
        val Initial get() = BackFaceVisibility("initial")
        val Revert get() = BackFaceVisibility("revert")
        val RevertLayer get() = BackFaceVisibility("revert-layer")
        val Unset get() = BackFaceVisibility("unset")
    }
}

fun StyleScope.backFaceVisibility(backFaceVisibility: BackFaceVisibility) {
    property("backface-visibility", backFaceVisibility)
}