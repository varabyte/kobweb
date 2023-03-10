package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.backgroundAttachment

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-attachment
class BackgroundAttachment private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keywords
        val Scroll get() = BackgroundAttachment("scroll")
        val Fixed get() = BackgroundAttachment("fixed")
        val Local get() = BackgroundAttachment("local")

        // Global values
        val Inherit get() = BackgroundAttachment("inherit")
        val Initial get() = BackgroundAttachment("initial")
        val Revert get() = BackgroundAttachment("revert")
        val RevertLayer get() = BackgroundAttachment("revert")
        val Unset get() = BackgroundAttachment("unset")
    }
}

fun StyleScope.backgroundAttachment(backgroundAttachment: BackgroundAttachment) {
    backgroundAttachment(backgroundAttachment.toString())
}

fun StyleScope.backgroundBlendMode(vararg blendModes: MixBlendMode) {
    property("background-blend-mode", blendModes.joinToString())
}