package com.varabyte.kobweb.compose.dom.observers

import com.varabyte.kobweb.compose.css.*
import org.w3c.dom.Element
import com.varabyte.kobweb.browser.dom.observers.IntersectionObserver as BrowserIntersectionObserver

// We provide our own wrapper around IntersectionObserver.Options for rich-type CSSMargin support
class IntersectionObserverOptions(
    val root: Element? = null,
    val rootMargin: CSSMargin? = null,
    val thresholds: List<Double>? = null,
)

@Suppress("FunctionName") // Factory method that mimics a constructor
fun IntersectionObserver(options: IntersectionObserverOptions, resized: (List<BrowserIntersectionObserver.Entry>, BrowserIntersectionObserver) -> Unit) {
    @Suppress("NAME_SHADOWING") val options = options.let {
        BrowserIntersectionObserver.Options(
            it.root,
            it.rootMargin.toString(),
            it.thresholds
        )
    }
    BrowserIntersectionObserver(options, resized)
}

/**
 * Provides a way to asynchronously observe changes in the intersection of a target element with an ancestor element.
 *
 * See https://developer.mozilla.org/en-US/docs/Web/API/Intersection_Observer_API
 */
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.dom.observers.IntersectionObserver` instead (that is, `compose` → `browser`).")
typealias IntersectionObserver = com.varabyte.kobweb.browser.dom.observers.IntersectionObserver
