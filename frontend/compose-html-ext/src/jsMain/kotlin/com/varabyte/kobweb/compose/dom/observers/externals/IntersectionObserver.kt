package com.varabyte.kobweb.compose.dom.observers.externals

import org.w3c.dom.DOMRectReadOnly
import org.w3c.dom.Element

internal external class IntersectionObserverEntry {
    val boundingClientRect: DOMRectReadOnly
    val intersectionRatio: Double
    val intersectionRect: DOMRectReadOnly
    val isIntersecting: Boolean
    val rootBounds: DOMRectReadOnly
    val target: Element
}

internal external class IntersectionObserver(callback: (Array<IntersectionObserverEntry>, ResizeObserver) -> Unit) {
    fun observe(element: Element): Unit
    fun unobserve(element: Element): Unit
    fun disconnect(): Unit
    fun takeRecords(): Array<IntersectionObserverEntry>
}
