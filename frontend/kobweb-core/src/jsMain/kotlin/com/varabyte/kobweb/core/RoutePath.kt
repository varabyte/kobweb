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
 * What a route path is allowed to point at: Kobweb pages, resources, or both.
 *
 * Here, a resource is anything the server responds to directly that isn't a page, e.g. a file in the site's public
 * resources folder or an API endpoint (like "/api/hello"). Since every path is either a page or not, these values
 * cover all cases.
 */
enum class RoutePathDestination {
    /**
     * This path can point at either a page or a resource, e.g. a `Link`.
     */
    PAGES_AND_RESOURCES,

    /**
     * This path can only point at a page, e.g. `Router.tryRoutingTo`, since Kobweb's client router only knows about
     * pages.
     */
    PAGES_ONLY,

    /**
     * This path can only point at a resource, e.g. an `Image`'s `src`, since the browser fetches it directly instead
     * of going through Kobweb's router, so pointing it at a page makes no sense.
     */
    RESOURCES_ONLY,
}

/**
 * An annotation which targets a String that represents a route path.
 *
 * This has no use for the code directly, but the Kobweb IntelliJ Plugin can detect it and use it to generate useful
 * code completion suggestions when interacting with such a String.
 *
 * If you are creating your own widget that takes in a route path, you are therefore encouraged to annotate it.
 *
 * @property type Instructs the IDE plugin with what is allowed in this particular argument, vs. what should be flagged
 *   as an error. See [RoutePathType] for more details.
 *
 * @property mustExist Whether the IDE will enforce that this path refers to something that already exists and flag it
 *   otherwise. A path exists if it matches a page (i.e. declared by a `@Page`) or a resource (e.g. a file under the
 *   site's public resources folder or an API endpoint; see [RoutePathDestination]). For code that wants to navigate to
 *   a route or load a resource, this should be true! But there may also be code which works with hypothetical routes
 *   that shouldn't get flagged (like the functions that register new routes for example). Note that this check only
 *   consistently applies to paths that start with a slash. Routes with an authority are considered external and
 *   unknowable, and we can't always reason about relative routes because it depends on what they are appended to.
 *   Finally, if this annotation is applied to a getter and this value set to true, the IDE will enforce that to mean
 *   any route value checked against it must exist, e.g. `route.path == "/must-be-valid"`.
 *
 * @property destination What this path is allowed to lead to: a page, a resource (e.g. a public file or an API
 *   endpoint), or either. For example, an `Image`'s `src` should lead to a resource such as a png of a cat, but never a
 *   page, while `Router.tryRoutingTo` can only ever lead to a page, since Kobweb's client router doesn't know about
 *   anything else. See [RoutePathDestination] for more details.
 */
@Target(
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.PROPERTY_GETTER,
)
@Retention(AnnotationRetention.BINARY)
annotation class RoutePath(
    val type: RoutePathType = RoutePathType.URL,
    val mustExist: Boolean = true,
    val destination: RoutePathDestination = RoutePathDestination.PAGES_AND_RESOURCES,
)
