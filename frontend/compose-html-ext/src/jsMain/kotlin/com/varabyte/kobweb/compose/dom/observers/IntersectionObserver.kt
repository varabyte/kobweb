package com.varabyte.kobweb.compose.dom.observers

import com.varabyte.kobweb.browser.dom.observers.IntersectionObserver
import com.varabyte.kobweb.compose.css.*
import org.w3c.dom.Element

/**
 * A helper method for generating [IntersectionObserver.Options] being able to use a [CSSMargin] object.
 */
operator fun IntersectionObserver.Options.Companion.invoke(
    root: Element? = null,
    rootMargin: CSSMargin? = null,
    thresholds: List<Double>? = null,
) = IntersectionObserver.Options(
    root,
    rootMargin?.toString(),
    thresholds
)
