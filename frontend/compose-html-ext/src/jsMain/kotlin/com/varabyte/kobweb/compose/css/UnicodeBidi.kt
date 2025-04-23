package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/unicode-bidi
class UnicodeBidi private constructor(private val value: String) : StylePropertyValue {

    companion object {

        /* Keyword values */
        val Normal get() = UnicodeBidi("normal")
        val Embed get() = UnicodeBidi("embed")
        val Isolate get() = UnicodeBidi("isolate")
        val BidiOverride get() = UnicodeBidi("bidi-override")
        val IsolateOverride get() = UnicodeBidi("isolate-override")
        val PlainText get() = UnicodeBidi("plaintext")

        /* Global values */
        val Inherit get() = UnicodeBidi("inherit")
        val Initial get() = UnicodeBidi("initial")
        val Revert get() = UnicodeBidi("revert")
        val RevertLayer get() = UnicodeBidi("revert-layer")
        val Unset get() = UnicodeBidi("unset")
    }
}

fun StyleScope.unicodeBidi(unicodeBidi: UnicodeBidi) {
    property("unicode-bidi", unicodeBidi)
}