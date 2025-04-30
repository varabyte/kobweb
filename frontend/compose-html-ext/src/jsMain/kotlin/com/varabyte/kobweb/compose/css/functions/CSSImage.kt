// Sealed class private constructors are useful, actually!
@file:Suppress("RedundantVisibilityModifier")

package com.varabyte.kobweb.compose.css.functions

import com.varabyte.kobweb.compose.css.*
import org.jetbrains.compose.web.css.*

sealed interface CSSImage : StylePropertyValue {
    companion object : CssGlobalValues<CSSImage> {
        fun of(url: CSSUrl) = url.toString().unsafeCast<CSSImage>()

        /**
         * @see toImage
         */
        fun of(gradient: Gradient) = gradient.toString().unsafeCast<CSSImage>()

        // Keyword
        val None get() = "none".unsafeCast<CSSImage>()
    }
}

fun Gradient.toImage() = CSSImage.of(this)
