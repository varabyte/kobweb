package com.varabyte.kobweb.gradle.core.project.backend

import kotlinx.serialization.Serializable
import org.gradle.api.GradleException

/**
 * Useful Kobweb-related information discovered while parsing a backend module.
 *
 * @param initMethods A collection of methods annotated with `@InitApi` and relevant metadata.
 * @param apiMethods A collection of methods annotated with `@Api` and relevant metadata.
 */
@Serializable
class BackendData(val initMethods: List<InitApiEntry>, val apiMethods: List<ApiEntry>)

fun Iterable<BackendData>.merge(): BackendData {
    return BackendData(
        this.flatMap { it.initMethods },
        this.flatMap { it.apiMethods },
    ).also { it.assertValid() }
}

fun BackendData.assertValid() {
    apiMethods.assertValidApis()
}

private fun Iterable<ApiEntry>.assertValidApis() {
    val entriesByRoute = this.groupBy { it.route }
    entriesByRoute.forEach { (route, apis) ->
        if (apis.size > 1) {
            throw GradleException("The API route \"$route\" was defined more than once. Used by:\n${apis.joinToString("\n") { entry -> " - ${entry.fqn}" }}")
        }
    }
}
