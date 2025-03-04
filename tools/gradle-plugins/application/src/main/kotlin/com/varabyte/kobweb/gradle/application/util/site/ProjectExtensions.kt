package com.varabyte.kobweb.gradle.application.util.site

import com.varabyte.kobweb.gradle.application.tasks.KobwebCacheAppFrontendDataTask
import com.varabyte.kobweb.project.frontend.AppFrontendData
import kotlinx.serialization.json.Json
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.utils.named

/**
 * Returns a list of routes registered by this site.
 *
 * To use it in your site, query it during the configuration phase and set it as an input, before querying the value
 * during the execution phase. Putting it all together looks like this:
 *
 * ```
 * tasks.register("createSitemap") {
 *     val routesProvider = kobwebSiteRoutes
 *     inputs.property("routes", routesProvider)
 *     doLast {
 *         val routes = routesProvider.get()
 *         val sitemapStr = buildList {
 *             routes.forEach { route -> add("https://example.com$route") }
 *         }.joinToString("\n")
 *         // save sitemapStr to disk...
 *     }
 * }
 * ```
 *
 * IMPORTANT: If you call this method during the execution phase, you will trigger a configuration cache error, so try
 * to stick to the template above.
 *
 * Note that this returned list includes dynamic routes, e.g. `/users/{user}/posts/{post}`. If you'd like to exclude
 * those, you can filter out any string that has a left curly brace in it, like so:
 * ```
 * // Filter out dynamic routes
 * routers.filter { !it.contains('{') }.forEach { /* ... */ }
 * ```
 */
val Project.kobwebSiteRoutes: Provider<List<String>>
    get() = tasks.named<KobwebCacheAppFrontendDataTask>("kobwebCacheAppFrontendData").map { task ->
        val pageEntries =
            Json.decodeFromString<AppFrontendData>(task.appDataFile.get().asFile.readText()).frontendData.pages
        pageEntries
            .asSequence()
            .map { it.route }
            .sorted()
            .toList()
    }
