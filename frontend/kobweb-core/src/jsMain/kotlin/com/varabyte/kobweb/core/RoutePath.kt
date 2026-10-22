package com.varabyte.kobweb.core

/**
 * The type determines the strictness of this route path argument.
 */
enum class RoutePathType {
    /**
     * This argument can accept literally any URL.
     *
     * Route completion suggestions will not be shown for URLs that start with an authority
     * (like "https://example.com"), but it is not an error to include them.
     */
    URL,

    /**
     * This argument can accept a path string and, optionally, query parameters and fragments.
     *
     * If this route path contains a starting authority, that will be flagged as an error.
     */
    PATH_QUERY_AND_FRAGMENT,

    /**
     * This argument can accept a path string only.
     *
     * Authorities, query parameters, and fragments will be flagged as errors.
     */
    PATH_ONLY
}

/**
 * An annotation which targets a String that represents a route path.
 *
 * This has no use for the code directly, but the Kobweb IntelliJ Plugin can detect it and use it to generate useful
 * code completion suggestions when interacting with such a String.
 *
 * If you are creating your own widget that takes in a route path, you are therefore encouraged to annotate it.
 * 
 * @param type Instructs the IDE plugin with what is allowed in this particular argument, vs. what should be flagged as
 *   an error. See [RoutePathType] for more details.
 *
 * @param mustExist Whether the IDE will enforce that this path already exists (i.e. declared by a `@Page`) and flag it
 *   otherwise. For code that wants to navigate to a route, this should be true! But there may also be code which works
 *   with hypothetical routes that shouldn't get flagged (like functions that register routes for example). Note that
 *   this check only consistently applies to paths that start with a slash. Routes with an authority are considered
 *   external and unknowable, and we can't always reason about relative routes because it depends on what they are
 *   appended to, outside of contexts where we know for sure what page we are on. Finally, if this annotation is applied
 *   to a getter and this value set to true, the IDE will enforce that to mean any route value checked against it must
 *   exist, e.g. `route.path == "/must-be-valid"`.
 */
@Target(
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.PROPERTY_GETTER,
)
@Retention(AnnotationRetention.BINARY)
annotation class RoutePath(val type: RoutePathType = RoutePathType.URL, val mustExist: Boolean = true)
