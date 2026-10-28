package com.varabyte.kobweb.project.backend

import com.varabyte.kobweb.project.common.RouteEntry
import com.varabyte.kobweb.project.common.assertValidRoutes
import kotlinx.serialization.Serializable

/**
 * Useful Kobweb-related information discovered while parsing a backend module.
 *
 * @param initMethods A collection of methods annotated with `@InitApi` and relevant metadata.
 * @param apiMethods A collection of methods annotated with `@Api` and relevant metadata.
 * @param apiStreamProperties A collection of ApiStream properties and relevant metadata.
 */
@Serializable
class BackendData(
    val initMethods: List<InitApiEntry> = emptyList(),
    val apiMethods: List<ApiEntry> = emptyList(),
    val apiStreamProperties: List<ApiStreamEntry> = emptyList(),
) {
    fun isEmpty() = initMethods.isEmpty() && apiMethods.isEmpty() && apiStreamProperties.isEmpty()
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
fun Iterable<BackendData>.merge(handleError: (String) -> Unit): BackendData {
    return BackendData(
        this.flatMap { it.initMethods },
        this.flatMap { it.apiMethods },
        this.flatMap { it.apiStreamProperties },
    ).also { it.assertValid(handleError) }
}

fun BackendData.assertValid(handleError: (String) -> Unit) {
    apiMethods.assertValidApis(handleError)
    apiStreamProperties.assertValidApiStreams(handleError)
}

private fun Iterable<ApiEntry>.assertValidApis(handleError: (String) -> Unit) {
    assertValidRoutes(routeLabel = "API", handleError)
}

private fun Iterable<ApiStreamEntry>.assertValidApiStreams(handleError: (String) -> Unit) {
    // The server registers streams with any leading slash removed, so "/echo" and "echo" collide
    val entriesByRoute = this.groupBy { it.route.removePrefix("/") }
    entriesByRoute.forEach { (route, streams) ->
        if (streams.size > 1) {
            handleError("The API stream route \"/$route\" was defined more than once. Used by:\n${streams.joinToString("\n") { entry -> " - ${entry.fqn}" }}")
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
class ApiEntry(override val fqn: String, override val route: String) : RouteEntry
