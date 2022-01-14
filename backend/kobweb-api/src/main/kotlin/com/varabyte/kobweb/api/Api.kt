package com.varabyte.kobweb.api

/**
 * An annotation which identifies a suspend function as one which will be used to serve the result of an API call.
 * The method should take an [ApiContext] as its only parameter.
 *
 * The method's filename will be used to generate its slug, e.g. "api/user/Fetch.kt" ->
 * "/api/user/fetch".
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Api