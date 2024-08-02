package com.varabyte.kobweb.browser.dom.observers

import com.varabyte.kobweb.browser.dom.observers.externals.ResizeObserverEntry
import com.varabyte.kobweb.browser.dom.observers.externals.ResizeObserverSize
import com.varabyte.kobweb.browser.util.titleCamelCaseToKebabCase
import org.w3c.dom.DOMRectReadOnly
import org.w3c.dom.Element
import kotlin.js.json
import com.varabyte.kobweb.browser.dom.observers.externals.ResizeObserver as ActualResizeObserver

/**
 * A performant mechanism by which code can monitor an element for changes to its size.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Resize_Observer_API">Resize Observer API</a>
 */
class ResizeObserver(resized: (List<Entry>, ResizeObserver) -> Unit) {
    constructor(resized: (List<Entry>) -> Unit) : this({ entries, _ -> resized.invoke(entries) })

    private val _actualObserver = ActualResizeObserver { actualEntries, _ ->
        resized.invoke(actualEntries.map { Entry.from(it) }, this)
    }

    class Size(
        val blockSize: Double,
        val inlineSize: Double,
    ) {
        companion object {
            internal fun from(actualSize: ResizeObserverSize) = Size(
                actualSize.blockSize,
                actualSize.inlineSize
            )
        }
    }

    class Entry(
        val target: Element,
        val borderBoxSize: List<Size>,
        val contentBoxSize: List<Size>,
        val contentRect: DOMRectReadOnly,
        val devicePixelContentBoxSize: List<Size>,
    ) {
        companion object {
            internal fun from(actualEntry: ResizeObserverEntry) = Entry(
                actualEntry.target,
                actualEntry.borderBoxSize.map { Size.from(it) },
                actualEntry.contentBoxSize.map { Size.from(it) },
                actualEntry.contentRect,
                actualEntry.devicePixelContentBoxSize.map { Size.from(it) },
            )
        }
    }

    enum class BoxType {
        ContentBox,
        BorderBox,
        DevicePixelContentBox
    }

    class ObserveOptions(val boxType: BoxType? = null) {
        internal fun toJson() = json().apply {
            boxType?.let { this["box"] = it.name.titleCamelCaseToKebabCase() }
        }
    }

    fun observe(element: Element): Unit = _actualObserver.observe(element)
    fun unobserve(element: Element): Unit = _actualObserver.unobserve(element)
    fun disconnect(): Unit = _actualObserver.disconnect()
}
