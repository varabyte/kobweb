package com.varabyte.kobweb.core

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.data.Data
import com.varabyte.kobweb.navigation.Route
import com.varabyte.kobweb.navigation.Router
import kotlinx.browser.window

/**
 * @param dynamicParams Values captured from the URL's dynamic route parts, already decoded.
 */
class RouteInfo internal constructor(
    private val route: Route,
    dynamicParams: Map<String, String>
) {
    /**
     * The origin of the current page.
     *
     * In the URL: "https://example.com/a/b/c/slug?x=1&y=2#id", the origin is "https://example.com"
     *
     * This property is equivalent to `window.location.origin` but provided here as a convenience property.
     */
    val origin: String get() = window.location.origin

    /**
     * The slug for the current page.
     *
     * In the URL: "https://example.com/a/b/c/slug?x=1&y=2#id", the slug is "slug"
     */
    val slug: String = route.path.substringAfterLast('/')

    /**
     * The current route path.
     *
     * In the URL: "https://example.com/a/b/c/slug?x=1&y=2#id", the path is "/a/b/c/slug"
     *
     * This property is equivalent to `window.location.pathname` but provided here as a convenience property.
     */
    @RoutePath(RoutePathType.PATH_ONLY, mustExist = false)
    val path: String = route.path

    /**
     * The query parameters of a URL, if any.
     *
     * For example:
     *
     * ```
     * /users/posts?user=123&post=11
     * ```
     *
     * will generate a mapping of "user" to 123 and "post" to 11.
     *
     * Use this over [params] when you want to make sure you get the query param value, even if an identically named
     * dynamic route param exists.
     *
     * @see dynamicParams
     * @see params
     */
    val queryParams: Map<String, String> = route.queryParams

    /**
     * Params extracted from the URL's dynamic route.
     *
     * For example:
     *
     * ```
     * /users/123/posts/11
     *
     * # for a URL registered as "/users/{user}/posts/{post}"
     * ```
     *
     * will generate a mapping of "user" to 123 and "post" to 11.
     *
     * If any captured part of the original URL was encoded, e.g. "/users/john%20doe", it will be decoded when queried
     * here.
     *
     * Checking if this value is not empty is a quick way to determine if the current route is, indeed, a dynamic one.
     *
     * NOTE: Unlike query params, which can contain any key, the keys of this map are fixed by the page's route. This
     * means the Kobweb IntelliJ plugin can check your lookups, for example flagging a key that isn't part of the route
     * (likely a typo) or confirming one that is (meaning it knows it won't ever be null).
     *
     * @see queryParams
     * @see params
     */
    @Suppress("CanBePrimaryConstructorProperty") // Leave property here as it is a better place for our giant KDoc
    val dynamicParams: Map<String, String> = dynamicParams

    /**
     * Params extracted either from the URL's query parameters OR from a dynamic route.
     *
     * For example:
     *
     * ```
     * /users/posts?user=123&post=11
     * ```
     *
     * and/or
     *
     * ```
     * /users/123/posts/11
     *
     * # for a URL registered as "/users/{user}/posts/{post}"
     * ```
     *
     * will generate a mapping of "user" to 123 and "post" to 11.
     *
     * If the same key is specified in both the query parameters and the dynamic route, the dynamic route will take
     * precedence.
     *
     * This property is fine to use over [dynamicParams] or [queryParams], especially if you don't care where the input
     * comes from. Prefer [dynamicParams] or [queryParams] when you want to make explicit where a value comes from.
     * [dynamicParams] has the added advantage that the Kobweb IntelliJ plugin can check its keys against the page's
     * route.
     *
     * @see dynamicParams
     * @see queryParams
     */
    val params: Map<String, String> = this.queryParams + this.dynamicParams

    /**
     * The post-hash fragment of a URL, if specified.
     *
     * For example, `/a/b/c/#fragment-id` will be the value `fragment-id`
     */
    val fragment: String? = route.fragment

    /**
     * The full path of the current route, including any query parameters and fragment.
     *
     * In the URL: "https://example.com/a/b/c/slug?x=1&y=2#id", this will return
     * "/a/b/c/slug?x=1&y=2#id"
     */
    @RoutePath(RoutePathType.PATH_QUERY_AND_FRAGMENT, mustExist = false)
    val pathQueryAndFragment get() = route.toString()

    override fun toString() = pathQueryAndFragment

    override fun equals(other: Any?): Boolean {
        return (other is RouteInfo
            && other.path == path
            && other.params == params
            && other.fragment == fragment
            )
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + params.hashCode()
        result = 31 * result + fragment.hashCode()
        return result
    }

    fun copy(
        @RoutePath(RoutePathType.PATH_ONLY, mustExist = false) path: String = route.path,
        queryParams: Map<String, String> = route.queryParams,
        fragment: String? = route.fragment,
        dynamicParams: Map<String, String> = this.dynamicParams
    ) =
        RouteInfo(Route(path, queryParams, fragment), dynamicParams)
}

/**
 * Various contextual information useful for a page.
 *
 * Access it using [rememberPageContext] either in the page itself or within any composable nested inside of it.
 *
 * ```
 * @Page
 * @Composable
 * fun SettingsPage() {
 *    val ctx = rememberPageContext()
 *    val userName = ctx.route.params["username"] ?: "Unknown user"
 *    ...
 * }
 */
class PageContext internal constructor(val router: Router, val data: Data) {
    internal val routeState: MutableState<RouteInfo?> = mutableStateOf(null)
    var route
        get() = routeState.value ?: error("PageContext route info is only valid within a @Page composable")
        internal set(value) {
            routeState.value = value
        }

    companion object {
        internal lateinit var instance: PageContext
        internal fun init(router: Router, data: Data) {
            instance = PageContext(router, data)
        }
    }
}

// Note: PageContext is technically a global, but we wrap it in a `PageContextLocal` as a way to ensure it is only
// accessible when under a `@Page` composable.
internal val PageContextLocal = staticCompositionLocalOf<PageContext?> { null }

/**
 * Returns the active page's context.
 *
 * This will throw an exception if not called within the scope of a `@Page` annotated composable.
 */
@Composable
// Note: Technically this isn't a real "remember", as the page context is really just a composition local, but we leave
// the API like this because user's mental model should think of it like a normal remember call. After all, they
// shouldn't wrap the return value in a remember themselves. It's possible we may revisit this approach in the future,
// as well.
fun rememberPageContext() = PageContextLocal.current ?: error("PageContext is only valid within a @Page composable")
