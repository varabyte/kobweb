package com.varabyte.kobweb.compose.dom.observers

import com.varabyte.kobweb.compose.dom.observers.externals.IntersectionObserverEntry
import org.w3c.dom.DOMRectReadOnly
import org.w3c.dom.Element
import com.varabyte.kobweb.compose.dom.observers.externals.IntersectionObserver as ActualIntersectionObserver

/**
 * Provides a way to asynchronously observe changes in the intersection of a target element with an ancestor element.
 *
 * See https://developer.mozilla.org/en-US/docs/Web/API/Intersection_Observer_API
 */
class IntersectionObserver(resized: (List<Entry>, IntersectionObserver) -> Unit) {
    constructor(resized: (List<Entry>) -> Unit) : this({ entries, _ -> resized(entries) })

    private val _actualObserver = ActualIntersectionObserver { actualEntries, _ ->
        resized.invoke(actualEntries.map { Entry.from(it) }, this)
    }

    class Entry(
        val target: Element,
        val boundingClientRect: DOMRectReadOnly,
        val intersectionRatio: Double,
        val intersectionRect: DOMRectReadOnly,
        val isIntersecting: Boolean,
        val rootBounds: DOMRectReadOnly,
    ) {
        companion object {
            internal fun from(actualEntry: IntersectionObserverEntry) = Entry(
                actualEntry.target,
                actualEntry.boundingClientRect,
                actualEntry.intersectionRatio,
                actualEntry.intersectionRect,
                actualEntry.isIntersecting,
                actualEntry.rootBounds
            )
        }
    }

    fun observe(element: Element): Unit = _actualObserver.observe(element)
    fun unobserve(element: Element): Unit = _actualObserver.unobserve(element)
    fun disconnect(): Unit = _actualObserver.disconnect()
    fun takeRecords(): List<Entry> = _actualObserver.takeRecords().map { Entry.from(it) }
}