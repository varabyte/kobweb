package com.varabyte.kobweb.compose.dom.observers.externals

import org.w3c.dom.DOMRectReadOnly
import org.w3c.dom.Element
import kotlin.js.Json

internal external class ResizeObserverSize {
    val blockSize: Double
    val inlineSize: Double
}

internal external class ResizeObserverEntry {
    val borderBoxSize: Array<ResizeObserverSize>
    val contentBoxSize: Array<ResizeObserverSize>
    val contentRect: DOMRectReadOnly
    val devicePixelContentBoxSize: Array<ResizeObserverSize>
    val target: Element
}

internal external class ResizeObserver(callback: (Array<ResizeObserverEntry>, ResizeObserver) -> Unit) {
    fun observe(element: Element, options: Json = definedExternally)
    fun unobserve(element: Element)
    fun disconnect()
}
