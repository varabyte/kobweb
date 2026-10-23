package com.varabyte.kobweb.api

/**
 * An annotation which targets a String that represents a path for accessing a backend API endpoint.
 *
 * An API path is relative to "/api/", e.g. "hello" corresponds to the full path "/api/hello". These are generated for
 * functions on the backend under the `api` package annotated with `@Api`.
 *
 * API paths are allowed to contain query parameters, e.g. "users?id=5"
 *
 * This has no use for the code directly, but the Kobweb IntelliJ Plugin can detect it and use it to drive extra
 * functionality like code completion, navigation, find usages, and missing endpoint warnings. Therefore, if you are
 * creating your own code that takes in an API path, you are encouraged to annotate it.
 *
 * @property mustExist Whether the IDE will enforce that this path already exists (i.e. declared by a `@Api`) and flag it
 *   otherwise.
 */
@Target(
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.PROPERTY_GETTER,
)
@Retention(AnnotationRetention.BINARY)
annotation class ApiPath(val mustExist: Boolean = true)
