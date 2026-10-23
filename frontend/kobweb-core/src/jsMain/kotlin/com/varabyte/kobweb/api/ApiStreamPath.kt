package com.varabyte.kobweb.api

/**
 * An annotation which targets a String that represents a path for accessing a backend API stream endpoint.
 *
 * This is similar to [ApiPath] but applies specifically to API streams only. API streams have their own names
 * independent of `@Api` methods, and they are not allowed to have query parameters.
 *
 * @property mustExist Whether the IDE will enforce that this path already exists (i.e. refers to an `ApiStream`
 *   instance) and flag it otherwise.
 */
@Target(
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.PROPERTY_GETTER,
)
@Retention(AnnotationRetention.BINARY)
annotation class ApiStreamPath(val mustExist: Boolean = true)
