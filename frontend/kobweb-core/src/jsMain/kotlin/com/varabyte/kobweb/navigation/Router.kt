package com.varabyte.kobweb.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.AlignItems
import com.varabyte.kobweb.compose.css.JustifyContent
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.PageContextLocal
import com.varabyte.kobweb.core.data.MutableData
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.NO_LAYOUT_FQN
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.INSTANT
import org.w3c.dom.MutationObserver
import org.w3c.dom.MutationObserverInit
import org.w3c.dom.ScrollBehavior
import org.w3c.dom.ScrollToOptions
import org.w3c.dom.asList
import org.w3c.dom.url.URL
import org.w3c.xhr.XMLHttpRequest

@Page
@Composable
private fun DefaultErrorPage() {
    Div(attrs = {
        style {
            width(100.percent)
            height(100.vh)
            display(DisplayStyle.Flex)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
        }
    }) {
        Text("Page Not Found")
    }
}

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
     * A simple data class containing information about a route.
     *
     * @property path The route's path, e.g. "/a/b/c". If a route has a dynamic part, that part will be surrounded in
     *   curly braces, e.g. "/users/{user}/posts/{post}"
     * @property isDynamic Whether the route has at least one dynamic part, which can be useful in case the user wants
     *   to filter these out.
     */
    class RouteEntry internal constructor(
        val path: String,
        val isDynamic: Boolean, // Whether the path is dynamic or not, in case users want to filter them out
    )

    private var errorPageMethod: PageMethod = { ctx -> DefaultErrorPage() }

    private val pageDataStore = MutableData()
    private val layouts = mutableMapOf<String, LayoutMethod>()
        .apply { this[NO_LAYOUT_FQN] = { ctx, content -> content(ctx) } }
    private val layoutIdForPage = mutableMapOf<PageMethod, String>()
        // Error page shouldn't use a layout (users can override this behavior using `setErrorHandler` if they need to)
        .apply { this[errorPageMethod] = NO_LAYOUT_FQN }
    private val layoutIdForLayout = mutableMapOf<LayoutMethod, String>()
    private val initRouteForPage = mutableMapOf<PageMethod, InitRouteMethod>()
    private val initRouteForLayout = mutableMapOf<LayoutMethod, InitRouteMethod>()
    private var activePageMethod by mutableStateOf<PageMethod?>(null)
    private val routeTree = RouteTree<PageMethod>()
    private val interceptors = mutableListOf<RouteInterceptorScope.() -> Unit>()

    /**
     * A sequence of all routes registered with this router.
     *
     * Users may want to filter out dynamic routes from the final list. Doing that looks like this:
     *
     * ```
     * ctx.router.routes.filter { !it.isDynamic }.map { it.path }.forEach { routePath -> ... }
     * ```
     */
    val routes: Sequence<RouteEntry>
        get() = routeTree.nodes
            // Make sure we discard partial routes -- only the last node of actual routes have data attached to them
            .filter { nodeList -> nodeList.last().data != null }
            .map { nodeList ->
            RouteEntry(
                nodeList.joinToString("/") { node -> node.sourceRouteSegment },
                nodeList.any { it is RouteTree.DynamicNode }
            )
        }

    init {
        PageContext.init(this, pageDataStore)
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
            val routeInfo = routeState.value
            if (routeInfo != null) {
                routeState.value = routeInfo.copy(fragment = pathQueryAndFragment.removePrefix("#"))
                return true
            } else {
                return false
            }
        }

        val route = Route.tryCreate(pathQueryAndFragment)
        return if (route != null) {
            val data = routeTree.createPageData(route, errorPageMethod)
            activePageMethod = data.pageMethod
            this.route = data.routeInfo
            
            val initRouteMethods = mutableListOf<InitRouteMethod>()
            initRouteForPage[activePageMethod]?.let { initRouteMethods.add(it) }
            data.pageMethod.parentLayouts.forEach { layoutMethod ->
                initRouteForLayout[layoutMethod]?.let { initRouteMethods.add(it) }
            }

            this@Router.pageDataStore.clear()
            val ctx = InitRouteContext(data.routeInfo, this@Router.pageDataStore)
            initRouteMethods.forEach { it.invoke(ctx) }

            true
        } else {
            false
        }
    }

    /**
     * The ancestor layouts for this page (if any), in order from closet to most distance ancestor.
     */
    val PageMethod.parentLayouts: List<LayoutMethod> get() {
        var layoutMethod: LayoutMethod? = layoutIdForPage[this]?.let { layouts[it] }
        return buildList {
            while (layoutMethod != null) {
                add(layoutMethod)
                layoutMethod = layoutIdForLayout[layoutMethod]?.let { layouts[it] }
            }
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
                // If a user navigates between two different dynamic routes, e.g. "/users/a" and "/users/b" for route
                // "/users/{user}", we want to treat this as a recomposition, since from the user's point of view, they
                // are different URLs. Query params changing should NOT cause a recomposition though!
                val keyedPageMethod: PageMethod = { ctx ->
                    key(PageContext.instance.route.path) { pageMethod(ctx) }
                }

                // When rendering a page, composition order starts at the top ancestor and works its way down
                pageMethod.parentLayouts.asReversed().foldRight(keyedPageMethod) { layout, accum ->
                    { ctx -> layout(ctx, accum) }
                }.invoke(PageContext.instance)
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

        // By design, whether a site has a base path or not should be invisible to the user. So here, we remove a
        // prefix if it is present only to put it back again (in the case that we removed it) after the interceptors
        // all have their pass.
        val withoutPrefix = BasePath.remove(hrefResolved).takeIf { it.isNotEmpty() } ?: "/"
        val hadPrefix = withoutPrefix != hrefResolved

        return BasePath.prependIf(hadPrefix, interceptors.fold(Route(withoutPrefix).toString()) { acc, intercept ->
            val interceptor = RouteInterceptorScope(acc)
            interceptor.intercept()
            interceptor.pathQueryAndFragment
        })
    }

    /**
     * Split a URL into its path part and the rest of the URL.
     *
     * For example, `/path?query#fragment` would return `/path` and `?query#fragment`.
     *
     * If there are neither query parameters nor a fragment, the second part will be an empty string.
     */
    private fun String.partitionPath(): Pair<String, String> {
        val pathPart = this.substringBefore('?').substringBefore('#')
        return pathPart to this.removePrefix(pathPart)
    }

    fun registerLayout(layoutId: String, parentLayoutId: String? = null, initRouteMethod: InitRouteMethod? = null, layoutMethod: LayoutMethod) {
        layouts[layoutId] = layoutMethod
        parentLayoutId?.let { layoutIdForLayout[layoutMethod] = it }
        initRouteMethod?.let { initRouteForLayout[layoutMethod] = it }
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
     */
    @Suppress("unused") // Called by generated code
    fun register(route: String, layoutId: String? = null, initRouteMethod: InitRouteMethod? = null, pageMethod: PageMethod) {
        require(Route.isRoute(route) && route.startsWith('/')) { "Registration only allowed for internal, rooted routes, e.g. /example/path. Got: $route" }
        require(
            routeTree.register(BasePath.prependTo(route), pageMethod)
        ) { "Registration failure. Path is already registered: $route" }

        layoutId?.let { layoutIdForPage[pageMethod] = it }
        initRouteMethod?.let { initRouteForPage[pageMethod] = it }
    }

    @Suppress("unused") // Called by generated code
    fun registerRedirect(fromRoute: String, toRoute: String) {
        listOf(fromRoute, toRoute).forEach {
            require(Route.isRoute(it) && it.startsWith('/')) { "Registration only allowed for rooted routes, e.g. `/example/path`. Got: $it" }
        }

        // Don't use BasePath.prepend here because `fromRoute` and `toRoute` are not strictly routes (the first is a
        // regex and the latter is a route that might have variables in it).
        val prefix = BasePath.value.removeSuffix("/")
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
    fun setErrorPage(layoutId: String? = NO_LAYOUT_FQN, pageMethod: PageMethod) {
        this.errorPageMethod = pageMethod
        if (layoutId == null) {
            layoutIdForPage.remove(errorPageMethod)
        } else {
            layoutIdForPage[errorPageMethod] = layoutId
        }
    }

    @Deprecated("Use `setErrorPage` instead. This old method required you take in a numeric value representing the error code, but it was always 404, so by moving towards a more standard format (that takes a `PageContext` as its main argument), we can simplify the codebase.")
    fun setErrorHandler(layoutId: String? = "", @Suppress("DEPRECATION") errorPageMethod: ErrorPageMethod) {
        setErrorPage(layoutId) { ctx ->
            errorPageMethod(404)
        }
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
     * By internally, I mean this method expects a route path only -- no https:// origin in other words. "/",
     * "/about", "/help/contact", and "user/123" are valid examples.
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
     * Note that this method will automatically prepend this site's [BasePath] if it is configured and if
     * [pathQueryAndFragment] is absolute.
     *
     * See also: [navigateTo], which, if called, handles the external navigation for you.
     *
     * @param pathQueryAndFragment The path to a page, including (optional) search params and hash,
     *   e.g. "/example/path?arg=1234#fragment". See also the
     *   [standards](https://www.rfc-editor.org/rfc/rfc3986#section-3.3) documentation.
     *
     * @param updateHistoryMode How this new path should affect the history. See [UpdateHistoryMode] docs for more
     *   details. Note that this value will be ignored if [pathQueryAndFragment] refers to an external link.
     */
    fun tryRoutingTo(
        pathQueryAndFragment: String,
        updateHistoryMode: UpdateHistoryMode = UpdateHistoryMode.PUSH,
        openLinkStrategy: OpenLinkStrategy = OpenLinkStrategy.IN_PLACE,
    ): Boolean {
        if (pathQueryAndFragment.contains("://")) return false

        @Suppress("NAME_SHADOWING") // Intentionally transformed
        var pathQueryAndFragment = BasePath.prependTo(pathQueryAndFragment)
        if (Route.isRoute(pathQueryAndFragment)) {
            pathQueryAndFragment = pathQueryAndFragment.normalize()

            // Next, we check a common edge case where the site has registered "slug" and the user typed "slug/"
            // OR vice versa ("slug/" and user typed "slug"). Let's help the user find the right place.
            run {
                val (pathPart, queryAndFragmentPart) = pathQueryAndFragment.partitionPath()

                // Unlikely but if user never defines a root page, `isRegistered("/")` will return false. We don't want
                // to add or remove slashes in that case!
                if (pathPart != "/") {
                    if (!routeTree.isRegistered(pathPart)) {
                        if (pathPart.endsWith('/')) {
                            val withoutSlash = pathPart.removeSuffix("/")
                            if (routeTree.isRegistered(withoutSlash)) {
                                pathQueryAndFragment = withoutSlash + queryAndFragmentPart
                            }
                        } else {
                            val withSlash = "$pathPart/"
                            if (routeTree.isRegistered(withSlash)) {
                                pathQueryAndFragment = withSlash + queryAndFragmentPart
                            }
                        }
                    }
                }
            }

            // Occasionally, a path might not be registered with our router BUT the server would respond to it. For
            // example, perhaps the incoming path refers to a file that lives on the server e.g.
            // "documents/external.md". So we ask the server if it's there. If so, we treat this navigation as "handled"
            // and kick off a request to the server to download the file.
            run {
                val (pathPart, _) = pathQueryAndFragment.partitionPath()

                if (!routeTree.isRegistered(pathPart)) {
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
                        window.open(pathQueryAndFragment, OpenLinkStrategy.IN_PLACE)
                        return true
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
                // It's possible only the search params or hash changed, in which case we don't want to reset the
                // current page scroll. (pathname is the part of the URL that is just the path, no origin or search
                // params or hash).
                val onNewPage = window.location.pathname != Route.fromUrl(URL(url)).path

                if (window.location.href != url) {
                    when (updateHistoryMode) {
                        UpdateHistoryMode.PUSH -> window.history.pushState(window.history.state, "", url)
                        UpdateHistoryMode.REPLACE -> window.history.replaceState(window.history.state, "", url)
                    }

                    if (onNewPage) {
                        window.scroll(ScrollToOptions(0.0, 0.0, ScrollBehavior.INSTANT))
                    }
                }

                // Even if the URL hasn't changed, still scroll to the target element if you can. Sometimes a user might
                // scroll the page and then re-enter the same URL to go back.
                if (url.contains('#')) {
                    fun scrollElementIntoView() = document.getElementById(url.substringAfter('#'))?.scrollIntoView()
                    if (onNewPage) {
                        // We need to give the page a chance to render first, or else the element with the ID might not
                        // exist yet.
                        MutationObserver { mutations, observer ->
                            mutations.forEach { mutation ->
                                // Only scroll if elements were added, removals do not signal that the page is rendered.
                                if (mutation.type == "childList" && mutation.addedNodes.asList().isNotEmpty()) {
                                    scrollElementIntoView()
                                    observer.disconnect()
                                }
                            }
                        }.observe(document.body!!, MutationObserverInit(childList = true, subtree = true))
                    } else {
                        scrollElementIntoView()
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
     * In other words, it can handle paths like `/example/route`, which would be considered an internal route, and also
     * `https://example.com/some/route`, referencing some outside domain, which would be considered external.
     *
     * Internal routes open instantly without needing to fetch any additional information from the server, while
     * external paths make a server request.
     *
     * You will generally call this method like so:
     *
     * ```
     * onClick { evt ->
     *   evt.preventDefault()
     *   ctx.router.navigateTo(...)
     * ```
     *
     * Note that if [pathQueryAndFragment] is a domain-less, absolute route (that is, it starts with a slash), then this
     * method will automatically prepend this site's [BasePath] if it is configured.
     *
     * Finally, if you pass in a path that starts with the current site's domain, then this method will treat it as an
     * external navigation *except* it will use [openInternalLinksStrategy] instead of [openExternalLinksStrategy] when
     * opening the link. This will also skip adding the [BasePath] prefix, because it is assumed you are grounding your
     * link to your own domain on purpose at that point.
     *
     * It may seem like an odd choice to allow an external navigation to your own site, but this enables a use-case
     * where you might split your Kobweb site across multiple servers, e.g. one for the main site and another for
     * subfolders, and then you can use external navigations against your own domain to jump between them.
     *
     * @param updateHistoryMode This parameter is only used for internal site routing. See [tryRoutingTo] for more
     *   information.
     * @param openInternalLinksStrategy The tab opening strategy to use when [pathQueryAndFragment] is a link that
     *   would result in the user staying inside the same domain.
     * @param openExternalLinksStrategy The tab opening strategy to use when [pathQueryAndFragment] is a link that
     *   would result in the user leaving the current domain.
     */
    fun navigateTo(
        pathQueryAndFragment: String,
        updateHistoryMode: UpdateHistoryMode = UpdateHistoryMode.PUSH,
        openInternalLinksStrategy: OpenLinkStrategy = OpenLinkStrategy.IN_PLACE,
        openExternalLinksStrategy: OpenLinkStrategy = OpenLinkStrategy.IN_NEW_TAB,
    ) {
        if (!tryRoutingTo(
                pathQueryAndFragment,
                updateHistoryMode,
                openInternalLinksStrategy,
            )
        ) {
            window.open(pathQueryAndFragment,
                if (pathQueryAndFragment.startsWith(window.origin)) {
                    openInternalLinksStrategy
                } else {
                    openExternalLinksStrategy
                }
            )
        }
    }
}
