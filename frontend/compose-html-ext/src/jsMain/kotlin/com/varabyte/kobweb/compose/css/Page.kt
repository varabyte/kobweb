package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/page
class Page private constructor(private val value: String) : StylePropertyValue {

    override fun toString() = value

    companion object {

        /* set a named page */
        fun of(name: String) = Page(name)

        /* Use ancestors named page */
        val Auto get() = Page("auto") /* default value */

        /* Global values */
        val Inherit get() = Page("inherit")
        val Initial get() = Page("initial")
        val Revert get() = Page("revert")
        val RevertLayer get() = Page("revert-layer")
        val Unset get() = Page("unset")
    }
}

fun StyleScope.page(page: Page = Page.Auto) {
    property("page", page)
}