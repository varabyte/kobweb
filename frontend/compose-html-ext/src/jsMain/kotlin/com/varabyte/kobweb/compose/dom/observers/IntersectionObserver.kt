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

/**
 * Provides a way to asynchronously observe changes in the intersection of a target element with an ancestor element.
 *
 * See https://developer.mozilla.org/en-US/docs/Web/API/Intersection_Observer_API
 */
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.dom.observers.IntersectionObserver` instead (that is, `compose` → `browser`).")
typealias IntersectionObserver = com.varabyte.kobweb.browser.dom.observers.IntersectionObserver
