package com.varabyte.kobweb.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.util.CancellableActionHandle
import com.varabyte.kobweb.browser.util.setInterval
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.PageContextLocal
import com.varabyte.kobweb.navigation.Router.LegacyRouteRedirectStrategy.DISALLOW
import com.varabyte.kobweb.navigation.Router.LegacyRouteRedirectStrategy.WARN
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.url.URL
import org.w3c.xhr.XMLHttpRequest
import kotlin.time.Duration.Companion.milliseconds

/** How to affect the current history when navigating to a new location */
enum class UpdateHistoryMode {
    /**
     * Push the new URL onto the stack, meaning if the user presses back, they return to the current URL.
     *
     * This is the most common and expected behavior when navigating within this site.
     */
    PUSH,

    /**
     * Overwrite the current URL, meaning if the user presses back, they'll go back to the URL before that one.
     *
     * This is most often useful if you aren't really navigating but are instead just changing query parameters on some
     * current page that represent transient state changes.
     */
    REPLACE
}

/**
 * Scope provided backing the handler for [Router.addRouteInterceptor].
 *
 * Through this scope, various parts of a route are exposed and can be set.
 */
class RouteInterceptorScope(pathQueryAndFragment: String) {
    private val route = Route(pathQueryAndFragment)

    /**
     * The path part of a route.
     *
     * For example, "/a/b/c" in the route "/a/b/c?key=value#fragment"
     *
     * This path should always be absolute; if you set it without a leading slash, one will
     * be added for you.
     */
    var path = route.path
        set(value) {
            field = if (value.startsWith('/')) value else "/$value"
        }

    /**
     * A map of query parameters.
     *
     * For example, { "key" = "value" } in the route "/a/b/c?key=value"
     */
    var queryParams = route.queryParams.toMutableMap()

    /**
     * The fragment part of a route, if present, or null otherwise.
     *
     * For example, "fragment" in the route "/a/b/c#fragment"
     */
    var fragment = route.fragment

    /**
     * The full route, built up of all the other parts of this scope.
     *
     * This value is read-only. To affect it, set the various parts.
     */
    val pathQueryAndFragment get() = Route(path, queryParams, fragment).toString()
}


/**
 * The class responsible for navigating to different pages in a user's app.
 */
class Router {
    /**
     * Strategy for allowing flexibility when trying to resolve legacy, non-hyphenated routes.
     *
     * When enabled, multi-word pages will be visitable both by typing in their hyphenated slug and their non-hyphenated
     * slug as well.
     *
     * Newer sites that aren't concerned with legacy may want to set this to [DISALLOW]. However, this defaults to
     * [WARN] for now to avoid breaking old sites.
     *
     * Note that this strategy will also affect the key name for dynamic routes associated with multi-word pages.
     * If allowed, then a dynamic route associated with a page like "UserPost.kt" can access the dynamic value either
     * using "userpost" or "user-post". If disallowed, then only "user-post" will work.
     */
    enum class LegacyRouteRedirectStrategy {
        /** Allow redirecting permissively. */
        ALLOW,

        /**
         * Redirect, but warn the user that it happened.
         *
         * This could be a good way to discover / clean up old links and fix them up before changing the strategy to
         * disallow them.
         */
        WARN,

        /**
         * Do not allow redirects.
         */
        DISALLOW,
    }

    private var activePageMethod by mutableStateOf<PageMethod?>(null)
    private val routeTree = RouteTree()
    private val interceptors = mutableListOf<RouteInterceptorScope.() -> Unit>()

    init {
        PageContext.init(this)
        window.onpopstate = {
            PageContext.instance.updatePageContext(document.location!!.let { it.href.removePrefix(it.origin) })
        }
    }

    /**
     * See docs for [tryRoutingTo].
     *
     * Returns true if we were able to navigate to a route that's internal to this site; false otherwise, e.g. if the
     * path targets some external website.
     */
    private fun PageContext.updatePageContext(pathQueryAndFragment: String): Boolean {
        // Special case - sometimes the value passed in here is simply a fragment, which means the browser
        // should scroll to an element on the same page
        if (pathQueryAndFragment.startsWith("#")) {
            routeState.value?.let { routeInfo ->
                routeState.value = routeInfo.copy(fragment = pathQueryAndFragment.removePrefix("#"))
                return true
            } ?: run {
                return false
            }
        }

        val route = Route.tryCreate(pathQueryAndFragment)
        return if (route != null) {
            val data = routeTree.createPageData(route)
            activePageMethod = data.pageMethod
            this.route = data.routeInfo
            true
        } else {
            false
        }
    }

    /**
     * Render the active page composable.
     *
     * This is the composable from the user's code tagged with `@Page` that is associated with the browser's currently
     * active url.
     *
     * @param pageWrapper A wrapper composable which, if provided, should wrap the page composable. The page composable
     *   will be passed to the wrapper as an `it` parameter, and the wrapper is expected to call it. The reason
     *   `pageWrapper` is passed in as a parameter rather than simply being called outside of this method is that the
     *   wrapper, along with the page itself, will get scoped underneath some composition local values that are tied to
     *   the lifetime of the page.
     */
    @Suppress("unused") // Called by generated code
    @Composable
    fun renderActivePage(pageWrapper: @Composable (@Composable () -> Unit) -> Unit = { it() }) {
        val pageMethod = activePageMethod
            ?: error("Call 'navigateTo' at least once before calling 'renderActivePage'")

        CompositionLocalProvider(
            PageContextLocal provides PageContext.instance
        ) {
            pageWrapper {
                pageMethod.invoke()
            }
        }
    }

    /**
     * Convert the incoming navigation request into a full route (but without the origin).
     *
     * Note that this can handle an isolated fragment, e.g. `#test`, which will, after normalization, be prepended with
     * the full route. Routes without a leading slash will have one prepended as well.
     */
    private fun String.normalize(): String {
        check(Route.isRoute(this))

        // The following line handles cases where the user passed in just query params / a fragment without a path
        // e.g. "#test" -> "/currpage#test" if the current page is "https://yoursite.com/currpage"
        // as well as relative routes, ensuring the final version is a full path that begins with a leading slash.
        val hrefResolved = Route.fromUrl(URL(this, window.location.href)).toString()

        // By design, whether a site has a route prefix or not should be invisible to the user. So here, we remove a
        // prefix if it is present only to put it back again (in the case that we removed it) after the interceptors
        // all have their pass.
        val withoutPrefix = RoutePrefix.remove(hrefResolved).takeIf { it.isNotEmpty() } ?: "/"
        val hadPrefix = withoutPrefix != hrefResolved

        return RoutePrefix.prependIf(hadPrefix, interceptors.fold(Route(withoutPrefix).toString()) { acc, intercept ->
            val interceptor = RouteInterceptorScope(acc)
            interceptor.intercept()
            interceptor.pathQueryAndFragment
        })
    }

    /**
     * Split a URL into its path part and the rest of the URL.
     *
     * For example, `/path?query#fragment` would return `/path` and `?query#fragment`.
     */
    private fun String.partitionPath(): Pair<String, String> {
        val pathPart = this.substringBefore('?').substringBefore('#')
        return pathPart to this.removePrefix(pathPart)
    }

    /**
     * Register a route, mapping it to some target composable method that will get called when that path is requested by
     * the browser.
     *
     * Routes should be internal, rooted paths, so:
     *
     * * Good: `/path`
     * * Good: `/path/with/subparts`
     * * Bad: `path`
     * * Bad: `http://othersite.com/path`
     *
     * Paths can also be dynamic routes, i.e. with parts that will consume values typed into the URL and exposed as
     * variables to the page. To accomplish this, use curly braces for that part of the path.
     *
     * For example: `/users/{user}/posts/{post}`
     *
     * In that case, if the user visited `/users/123456/posts/321`, then that composable method will be visited, with
     * `user = 123456` and `post = 321` passed down in the `PageContext`.
     *
     * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
     *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
     *   staying in the same domain.
     */
    @Suppress("unused") // Called by generated code
    fun register(route: String, autoPrefix: Boolean = true, pageMethod: PageMethod) {
        require(Route.isRoute(route) && route.startsWith('/')) { "Registration only allowed for internal, rooted routes, e.g. /example/path. Got: $route" }
        require(
            routeTree.register(
                RoutePrefix.prependIf(autoPrefix, route),
                pageMethod
            )
        ) { "Registration failure. Path is already registered: $route" }
    }

    @Suppress("unused") // Called by generated code
    fun registerRedirect(fromRoute: String, toRoute: String, autoPrefix: Boolean = true) {
        listOf(fromRoute, toRoute).forEach {
            require(Route.isRoute(it) && it.startsWith('/')) { "Registration only allowed for rooted routes, e.g. `/example/path`. Got: $it" }
        }

        // Don't use RoutePrefix.prepend here because `fromRoute` and `toRoute` are not strictly routes (the first is a
        // regex and the latter is a route that might have variables in it).
        val prefix = if (autoPrefix) RoutePrefix.value.removeSuffix("/") else ""
        routeTree.registerRedirect(prefix + fromRoute, prefix + toRoute)
    }

    /**
     * Set a handler to call to render a page when the route is not found.
     *
     * Using this in your project would look like this:
     *
     * ```
     * @InitKobweb
     * fun initKobweb(ctx: InitKobwebContext) {
     *   ctx.router.setErrorHandler { errorCode ->
     *     // NOTE: This callback is a @Composable function
     *     if (errorCode == 404) {
     *       // Render a 404 page
     *     }
     *   }
     * }
     * ```
     */
    fun setErrorHandler(errorHandler: ErrorPageMethod) {
        routeTree.errorHandler = errorHandler
    }

    fun setLegacyRouteRedirectStrategy(strategy: LegacyRouteRedirectStrategy) {
        routeTree.legacyRouteRedirectStrategy = strategy
    }

    /**
     * If set, get a chance to modify the page's route before Kobweb navigates to it.
     *
     * This could be a cheap way to redirect to a new URL if an old one was removed due to
     * a refactor.
     *
     * A simple way to use this might look like:
     *
     * ```
     * @InitKobweb
     * fun initKobweb(ctx: InitKobwebContext) {
     *   ctx.router.addRouteInterceptor {
     *     if (path == "/admin") {
     *       // The old admin has grown and is being split up into multiple pages.
     *       // Send people to the dashboard by default.
     *       path = "/admin/dashboard"
     *     }
     *   }
     * }
     * ```
     *
     * In the above case, if a user navigates to `https://yoursite.com/admin` the URL will automatically change to
     * `https://yoursite.com/admin/dashboard`.
     *
     * See [RouteInterceptorScope] for more options.
     */
    fun addRouteInterceptor(interceptor: RouteInterceptorScope.() -> Unit) {
        interceptors.add(interceptor)
    }

    /**
     * Attempt to navigate **internally** within this site, or return false if that's not possible (i.e. because the
     * path is external).
     *
     * You will generally call this method like so:
     *
     * ```
     * onClick { evt ->
     *   if (ctx.router.tryRoutingTo(...)) {
     *     evt.preventDefault()
     *   }
     * ```
     *
     * That way, either the router handles the navigation or the browser does.
     *
     * See also: [navigateTo], which, if called, handles the external navigation for you.
     *
     * @param pathQueryAndFragment The path to a page, including (optional) search params and hash,
     *   e.g. "/example/path?arg=1234#fragment". See also the
     *   [standards](https://www.rfc-editor.org/rfc/rfc3986#section-3.3) documentation.
     * @param updateHistoryMode How this new path should affect the history. See [UpdateHistoryMode] docs for more
     *   details. Note that this value will be ignored if [pathQueryAndFragment] refers to an external link.
     */
    fun tryRoutingTo(
        pathQueryAndFragment: String,
        updateHistoryMode: UpdateHistoryMode = UpdateHistoryMode.PUSH,
        openLinkStrategy: OpenLinkStrategy = OpenLinkStrategy.IN_PLACE
    ): Boolean {
        @Suppress("NAME_SHADOWING") // Intentionally transformed
        var pathQueryAndFragment = pathQueryAndFragment
        if (Route.isRoute(pathQueryAndFragment)) {
            pathQueryAndFragment = pathQueryAndFragment.normalize()

            // Next, we check a common edge case where the site has registered "slug" and the user typed "slug/"
            // OR vice versa ("slug/" and user typed "slug"). Let's help the user find the right place.

            // Note: We don't touch the path if it has queries or fragments. Because in that case, if the original route
            // isn't found, we don't want to waste time adding a slash to the end of it (since slashes shouldn't ever
            // come after queries / fragments).
            if (pathQueryAndFragment.all { it != '#' && it != '?' }) {
                // Unlikely but if user never defines a root page, `isRegistered("/")` will return false. We don't want
                // to add or remove slashes in that case!
                if (pathQueryAndFragment != "/") {
                    val originalRoute = pathQueryAndFragment
                    if (!routeTree.isRegistered(originalRoute)) {
                        if (originalRoute.endsWith('/')) {
                            val withoutSlash = originalRoute.removeSuffix("/")
                            if (routeTree.isRegistered(withoutSlash)) {
                                pathQueryAndFragment = withoutSlash
                            }
                        } else {
                            val withSlash = "$originalRoute/"
                            if (routeTree.isRegistered(withSlash)) {
                                pathQueryAndFragment = withSlash
                            }
                        }
                    }

                    // If the next check passes, we can't find the path locally, but it's possible that it refers to a
                    // file that lives on the server e.g. "documents/external.md". So we ask the server if it's there.
                    // If so, we treat this navigation as "handled" and kick off a request to the server to download
                    // the file.
                    if (!routeTree.isRegistered(pathQueryAndFragment)) {
                        val xhr = XMLHttpRequest()
                        var fileExistsOnServer = false
                        xhr.open("HEAD", pathQueryAndFragment, async = false)
                        xhr.onload = {
                            fileExistsOnServer = xhr.status == 200.toShort()
                            Unit
                        }
                        xhr.onerror = {}
                        xhr.onabort = {}
                        xhr.send(null)

                        if (fileExistsOnServer) {
                            window.open(pathQueryAndFragment)
                            return true
                        }
                    }
                }
            }
        }

        if (openLinkStrategy != OpenLinkStrategy.IN_PLACE) {
            window.open(pathQueryAndFragment, openLinkStrategy)
            return true
        }

        return if (PageContext.instance.updatePageContext(pathQueryAndFragment)) {
            // Although 99.9% of the time, the URL we request to visit will be the same as the one visited, if any parts
            // of the URL are set up to use redirects, then it will be different. Here, we update just the path part
            // *just in case* it changed, but keep the query parameters / fragment parts the same as before.
            // So if we visit "blah.com/old-page#hash?a=b" and get redirected, the final URL will be
            // "blah.com/new-page#hash?a=b"
            pathQueryAndFragment = PageContext.instance.route.path + pathQueryAndFragment.partitionPath().second

            // Update URL to match page we navigated to
            "${window.location.origin}$pathQueryAndFragment".let { url ->
                if (window.location.href != url) {
                    // It's possible only the search params or hash changed, in which case we don't want to reset the
                    // current page scroll
                    val newPathname = window.location.pathname != Route.fromUrl(URL(url)).path
                    when (updateHistoryMode) {
                        UpdateHistoryMode.PUSH -> window.history.pushState(window.history.state, "", url)
                        UpdateHistoryMode.REPLACE -> window.history.replaceState(window.history.state, "", url)
                    }

                    if (newPathname) {
                        document.documentElement?.scrollTop = 0.0
                    }
                }

                // Even if the URL hasn't changed, still scroll to the target element if you can. Sometimes a user might
                // scroll the page and then re-enter the same URL to go back.
                if (url.contains('#')) {
                    val fragment = url.substringAfter('#')
                    // HACK ALERT
                    // We need to give the page a chance to render first, or else the element with the ID might not
                    // exist yet. There doesn't seem like a great way to do this consistently that I've found, so for
                    // now, just try a bunch of times in a row, and give up if we continue to fail (at that point, the
                    // page may never have a matching element).
                    var attempts = 10
                    val timeToWaitPerAttempt = 100.milliseconds

                    var handle = CancellableActionHandle.Stub
                    handle = window.setInterval(timeToWaitPerAttempt) {
                        val element = document.getElementById(fragment)
                        if (element != null) {
                            element.scrollIntoView(js("{behavior: \"smooth\"}"))
                            attempts = 0
                        } else {
                            attempts--
                        }

                        if (attempts == 0) {
                            handle.cancel()
                        }
                    }
                }
            }

            true
        } else {
            false
        }
    }

    /**
     * Like [tryRoutingTo] but handle the external navigation as well.
     *
     * You will generally call this method like so:
     *
     * ```
     * onClick { evt ->
     *   evt.preventDefault()
     *   ctx.router.navigateTo(...)
     * ```
     *
     * @param updateHistoryMode This parameter is only used for internal site routing. See [tryRoutingTo] for more
     *   information.
     */
    fun navigateTo(
        pathQueryAndFragment: String,
        updateHistoryMode: UpdateHistoryMode = UpdateHistoryMode.PUSH,
        openInternalLinksStrategy: OpenLinkStrategy = OpenLinkStrategy.IN_PLACE,
        openExternalLinksStrategy: OpenLinkStrategy = OpenLinkStrategy.IN_NEW_TAB,
    ) {
        if (!tryRoutingTo(pathQueryAndFragment, updateHistoryMode, openInternalLinksStrategy)) {
            window.open(pathQueryAndFragment, openExternalLinksStrategy)
        }
    }
}
