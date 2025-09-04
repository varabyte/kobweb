package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.application.extensions.sitemap
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class KobwebGenerateSitemapTask : KobwebGenerateTask("Generate an XML sitemap for the site") {

    @get:Input
    abstract val basePath: Property<String>

    @get:Nested
    abstract val sitemapBlock: Property<AppBlock.SitemapBlock>

    @get:Input
    abstract val routes: ListProperty<String>

    @get:OutputFile
    abstract val sitemapFile: RegularFileProperty

    @TaskAction
    fun execute() {
        val baseUrlValue = sitemapBlock.get().baseUrl.get()

        // Validate baseUrl
        validateBaseUrl(baseUrlValue)

        // Start with discovered routes and add extra routes
        val allRoutes = routes.get().toMutableSet().apply {
            addAll(sitemapBlock.get().extraRoutes.get())
        }

        // Apply filters: always exclude dynamic routes first, then apply user filter
        val filteredRoutes = allRoutes
            .filter { route ->
                    // Always exclude dynamic routes regardless of user filter
                val isDynamicRoute = route.contains('{') && route.contains('}')
                if (isDynamicRoute) {
                    logger.debug("Route: '$route' -> EXCLUDE (dynamic route)")
                    return@filter false
                }

                // Apply user filter for non-dynamic routes
                val ctx = AppBlock.SitemapBlock.SitemapFilterContext(route)
                val shouldInclude = sitemapBlock.get().filter.get()(ctx)
                logger.debug("Route: '$route' -> ${if (shouldInclude) "INCLUDE" else "EXCLUDE"} (user filter)")
                shouldInclude
            }
            .sorted()

        logger.info("Filtered ${allRoutes.size} routes down to ${filteredRoutes.size} routes")
        logger.info(
            "Excluded routes: ${
                allRoutes.filter { route ->
                    // Same logic as above: exclude dynamic routes or routes that fail user filter
                    val isDynamicRoute = route.contains('{') && route.contains('}')
                    if (isDynamicRoute) return@filter true
                    val ctx = AppBlock.SitemapBlock.SitemapFilterContext(route)
                    !sitemapBlock.get().filter.get()(ctx)
                }
            }"
        )

        // Size warnings
        if (filteredRoutes.size > 50000) {
            logger.warn("Sitemap contains ${filteredRoutes.size} URLs, exceeding the 50,000 URL limit. Consider using filter to reduce size.")
        }

        // Generate XML sitemap in build dir (already configured via sitemapFile)
        val sitemapXml = generateSitemapXml(baseUrlValue, filteredRoutes)

        if (sitemapXml.length > 50 * 1024 * 1024) { // 50MB
            logger.warn("Generated sitemap is ${sitemapXml.length / (1024 * 1024)}MB, exceeding the 50MB limit.")
        }

        sitemapFile.get().asFile.writeText(sitemapXml)

        logger.info("Generated sitemap with ${filteredRoutes.size} routes.")
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
        val basePathValue = basePath.get()
        if (basePathValue.isNotBlank()) {
            try {
                val baseUrlPath = java.net.URI(baseUrl).path
                if (baseUrlPath.isNotEmpty() && !baseUrlPath.endsWith("/$basePathValue")) {
                    throw GradleException(
                        "sitemap.baseUrl path ('$baseUrlPath') does not end with the project's configured " +
                            "base path ('/$basePathValue'). This will result in incorrect sitemap links. " +
                            "Please set baseUrl to include the base path, e.g., \"https://example.com/$basePathValue\"."
                    )
                }
            } catch (e: java.net.URISyntaxException) {
                // Already handled by the http/https check, but catch to be safe
                logger.warn("Could not parse sitemap.baseUrl to validate against base path: ${e.message}")
            }
        }
    }


    private fun createAbsoluteUrl(baseUrl: String, route: String): String {
        val cleanBaseUrl = baseUrl.trimEnd('/')
        val cleanRoute = if (route.startsWith("/")) route else "/$route"
        return cleanBaseUrl + cleanRoute
    }

    private fun generateSitemapXml(baseUrl: String, routes: List<String>): String {
        return buildString {
            appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
            appendLine("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">")

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