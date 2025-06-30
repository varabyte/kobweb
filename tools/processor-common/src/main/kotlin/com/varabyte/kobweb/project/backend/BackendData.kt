package com.varabyte.kobweb.project.backend

import kotlinx.serialization.Serializable

/**
 * Useful Kobweb-related information discovered while parsing a backend module.
 *
 * @param initMethods A collection of methods annotated with `@InitApi` and relevant metadata.
 * @param apiMethods A collection of methods annotated with `@Api` and relevant metadata.
 * @param apiStreamMethods A collection of ApiStream properties and relevant metadata.
 */
@Serializable
class BackendData(
    val initMethods: List<InitApiEntry> = emptyList(),
    val apiMethods: List<ApiEntry> = emptyList(),
    val apiStreamMethods: List<ApiStreamEntry> = emptyList(),
) {
    fun isEmpty() = initMethods.isEmpty() && apiMethods.isEmpty() && apiStreamMethods.isEmpty()
}

@Serializable
class AppBackendData(
    val apiInterceptorMethod: ApiInterceptorEntry? = null,
    val backendData: BackendData = BackendData(),
) {
    fun isEmpty() = apiInterceptorMethod == null && backendData.isEmpty()
}

/**
 * Merge multiple [BackendData] objects together.
 *
 * An error will be thrown if the merged result generates any invalid conditions, e.g. duplicate routes.
 */
fun Iterable<BackendData>.merge(throwError: (String) -> Unit): BackendData {
    return BackendData(
        this.flatMap { it.initMethods },
        this.flatMap { it.apiMethods },
        this.flatMap { it.apiStreamMethods },
    ).also { it.assertValid(throwError) }
}

fun BackendData.assertValid(throwError: (String) -> Unit) {
    apiMethods.assertValidApis(throwError)
}

private fun Iterable<ApiEntry>.assertValidApis(throwError: (String) -> Unit) {
    val entriesByRoute = this.groupBy { it.route }
    entriesByRoute.forEach { (route, apis) ->
        if (apis.size > 1) {
            throwError("The API route \"$route\" was defined more than once. Used by:\n${apis.joinToString("\n") { entry -> " - ${entry.fqn}" }}")
        }
    }
}

@Serializable
class InitApiEntry(val fqn: String)

@Serializable
class ApiInterceptorEntry(val fqn: String)

@Serializable
class ApiStreamEntry(val fqn: String, val route: String)

@Serializable
class ApiEntry(val fqn: String, val route: String)
