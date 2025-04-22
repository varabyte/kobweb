package com.varabyte.kobweb.browser.dom.observers.externals

import org.w3c.dom.DOMRectReadOnly
import org.w3c.dom.Element
import kotlin.js.Json

internal external class IntersectionObserverEntry {
    val boundingClientRect: DOMRectReadOnly
    val intersectionRatio: Double
    val intersectionRect: DOMRectReadOnly
    val isIntersecting: Boolean
    val rootBounds: DOMRectReadOnly
    val target: Element
}

internal external class IntersectionObserver(
    callback: (Array<IntersectionObserverEntry>, IntersectionObserver) -> Unit,
    options: Json?
) {
    val root: dynamic
    val rootMargin: String
    val thresholds: Array<Double>

    fun observe(element: Element)
    fun unobserve(element: Element)
    fun disconnect()
    fun takeRecords(): Array<IntersectionObserverEntry>
}
