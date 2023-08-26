package com.varabyte.kobweb.gradle.core.util

import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.NamedDomainObjectProvider

/**
 * Replacement for `findByName` that supports task configuration avoidance.
 */
fun <T> NamedDomainObjectCollection<T>.namedOrNull(name: String): NamedDomainObjectProvider<T>? =
    if (name in names) named(name) else null
