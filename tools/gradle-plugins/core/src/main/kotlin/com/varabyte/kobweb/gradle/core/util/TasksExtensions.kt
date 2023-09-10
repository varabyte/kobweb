package com.varabyte.kobweb.gradle.core.util

import org.gradle.api.Task
import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.utils.named

/**
 * Replacement for `findByName` that supports task configuration avoidance.
 */
inline fun <reified V : Task> TaskCollection<in V>.namedOrNull(name: String): TaskProvider<V>? =
    if (name in names) named<V>(name) else null
