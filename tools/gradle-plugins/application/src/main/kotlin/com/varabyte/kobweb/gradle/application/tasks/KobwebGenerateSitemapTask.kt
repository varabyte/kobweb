package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.application.extensions.sitemap
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class KobwebGenerateSitemapTask @Inject constructor(
) : KobwebGenerateTask("Generate an XML sitemap for the site") {

    @get:Input
    abstract val baseUrl: Property<String>

    @get:Input
    abstract val includeDynamicRoutes: Property<Boolean>

    @get:Input
    abstract val extraRoutes: ListProperty<String>

    @get:Input
    abstract val excludeRoutes: SetProperty<String>

    @get:Input
    abstract val routes: ListProperty<String>

    /**
     * A filter to determine which routes should be included in the sitemap.
     * Return true to include the route, false to exclude it.
     *
     * Note: This is marked @Internal to avoid configuration cache issues with lambda serialization.
     */
    @get:Internal
    abstract val routeFilter: Property<(String) -> Boolean>

    @get:OutputFile
    abstract val sitemapFile: RegularFileProperty

    @TaskAction
    fun execute() {
        // Skip execution if baseUrl is not configured
        if (!baseUrl.isPresent) {
            logger.info("Skipping sitemap generation as baseUrl is not configured")
            return
        }

        val baseUrlValue = baseUrl.get()
        val includeDynamic = includeDynamicRoutes.get()
        val extraRoutesValue = extraRoutes.get()
        val excludeRoutesValue = excludeRoutes.get()
        val routeFilterValue = routeFilter.orNull

        // Validate baseUrl
        validateBaseUrl(baseUrlValue)

        // Start with discovered routes
        val allRoutes = routes.get().toMutableSet()

        // Add extra routes
        allRoutes.addAll(extraRoutesValue)

        // Apply filters
        val filteredRoutes = allRoutes
            .filter { route ->
                // Exclude routes that match any exclude pattern (prefix or exact match)
                if (isRouteExcluded(route, excludeRoutesValue)) return@filter false

                // Exclude dynamic routes if configured (stricter check)
                if (!includeDynamic && isDynamicRoute(route)) return@filter false

                // Apply custom filter if provided
                routeFilterValue?.invoke(route) ?: true
            }
            .sorted()

        // Check size limits and warn
        if (filteredRoutes.size > 50000) {
            logger.warn("Sitemap contains ${filteredRoutes.size} URLs, exceeding the 50,000 URL limit. Consider using excludeRoutes to reduce size.")
        }

        // Generate XML sitemap
        val sitemapXml = generateSitemapXml(baseUrlValue, filteredRoutes)

        // Check size limit
        if (sitemapXml.length > 50 * 1024 * 1024) { // 50MB
            logger.warn("Generated sitemap is ${sitemapXml.length / (1024 * 1024)}MB, exceeding the 50MB limit.")
        }

        sitemapFile.get().asFile.writeText(sitemapXml)

        logger.lifecycle("Generated sitemap with ${filteredRoutes.size} routes")
        if (filteredRoutes.size <= 20) {
            // Only log individual routes for small sitemaps to avoid noise
            filteredRoutes.forEach { route ->
                logger.lifecycle("  ${createAbsoluteUrl(baseUrlValue, route)}")
            }
        }
    }

    private fun validateBaseUrl(baseUrl: String) {
        when {
            baseUrl.isBlank() -> throw GradleException("sitemap.baseUrl cannot be empty")
            !baseUrl.startsWith("http://") && !baseUrl.startsWith("https://") -> {
                throw GradleException(
                    "sitemap.baseUrl must be absolute (start with http:// or https://), got: $baseUrl\n" +
                        "Examples:\n" +
                        "  Production: \"https://mysite.com\"\n" +
                        "  Testing: \"http://localhost:8080\""
                )
            }

            baseUrl.endsWith("/") ->
                throw GradleException("sitemap.baseUrl should not end with a trailing slash, got: $baseUrl")
        }
    }

    private fun isDynamicRoute(route: String): Boolean {
        // More precise check than just contains('{')
        return route.contains('{') && route.contains('}')
    }

    private fun isRouteExcluded(route: String, excludeRoutes: Set<String>): Boolean {
        return excludeRoutes.any { excludeRoute ->
            route.startsWith(excludeRoute) || route == excludeRoute
        }
    }

    private fun createAbsoluteUrl(baseUrl: String, route: String): String {
        val cleanBaseUrl = baseUrl.trimEnd('/')
        val cleanRoute = if (route.startsWith("/")) route else "/$route"
        return cleanBaseUrl + cleanRoute
    }

    private fun generateSitemapXml(baseUrl: String, routes: List<String>): String {
        return buildString {
            appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
            appendLine("""<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">""")

            routes.forEach { route ->
                val absoluteUrl = createAbsoluteUrl(baseUrl, route)
                val escapedUrl = escapeXml(absoluteUrl)

                appendLine("  <url>")
                appendLine("    <loc>$escapedUrl</loc>")
                // Note: We don't include <lastmod> as we don't have reliable modification dates
                // Future enhancement could track file modification times or add annotation support
                appendLine("  </url>")
            }

            appendLine("</urlset>")
        }
    }

    private fun escapeXml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
}