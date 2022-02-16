package com.varabyte.kobweb.api

/**
 * An annotation which identifies a suspend function as one which will be used to serve the result of an API call.
 * The method should take an [ApiContext] as its only parameter.
 *
 * The method's filename will be used to generate its slug, e.g. "api/user/Fetch.kt" ->
 * "/api/user/fetch".
 *
 * ## routeOverride
 *
 * Note that the route generated for this page is quite customizable by setting the [routeOverride] parameter. In
 * general, you should NOT set it, as this will make it harder for people to navigate your project and find where a
 * URL is coming from.
 *
 * However, if you do set it, in most cases, it is expected to just be a single, lowercase word, which changes the slug
 * used for this route (instead of the file name).
 *
 * But wait, there's more!
 *
 * If the value starts with a slash, it will be treated as a full path starting at the site's root. If the value ends
 * with a slash, it means the override represents a change in the URL path but the slug will still be derived from
 * the filename.
 *
 * Some examples should clear up the various cases. Let's say the site is `com.example` and this `@Api` is defined in
 * `package api.user` in file `Fetch.kt`:
 *
 * ```
 * @Api -> example.com/api/user/fetch
 * @Api("retrieve") -> example.com/api/user/retrieve
 * @Api("current/") -> example.com/api/user/current/fetch
 * @Api("current/retrieve") -> example.com/api/user/current/retrieve
 * @Api("/users/") -> example.com/api/users/fetch
 * @Api("/users/retrieve") -> example.com/api/users/retrieve
 * @Api("/") -> example.com/api/fetch
 * ```
 *
 * @param routeOverride If specified, override the logic for generating a route for this API as documented in this
 *   header doc.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Api(val routeOverride: String = "")