package com.varabyte.kobweb.gradle.core.util

import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.provider.Provider

/**
 * Lazily evaluates whether the provided dependency is included in the list of resolved dependencies.
 *
 * @param name The dependency to be searched for in the form `$group:$name`, e.g. "com.varabyte.kobweb:kobweb-silk"
 */
fun Provider<List<ResolvedDependencyResult>>.hasDependencyNamed(name: String): Provider<Boolean> {
    return map { dependencies ->
        dependencies.any {
            val dep = it.selected.moduleVersion ?: return@any false
            name == "${dep.group}:${dep.name}"
        }
    }
}
