package com.varabyte.kobweb.gradle.application.templates

fun createMainFunction(appFqcn: String?, pageFqcnRoutes: Map<String, String>): String {
    val imports = mutableListOf(
        "com.varabyte.kobweb.core.Router",
        "org.jetbrains.compose.web.renderComposable",
        "kotlinx.browser.document",
        "kotlinx.browser.window",
    )
    imports.add(appFqcn ?: "com.varabyte.kobweb.core.DefaultApp")
    imports.addAll(pageFqcnRoutes.keys)
    imports.sort()

    return """
        ${
            imports.joinToString("\n        ") { fcqn -> "import $fcqn" }
        }

        fun main() {
            ${
            // Generates lines like: Router.register("/about") { AboutPage() }
            pageFqcnRoutes.entries.joinToString("\n            ") { entry ->
                val pageFqcn = entry.key
                val route = entry.value

                """Router.register("$route") { ${pageFqcn.substringAfterLast('.')}() }"""
            }
        }
            Router.navigateTo(window.location.pathname)

            // For SEO, we may bake the contents of a page in at build time. However, we will overwrite them the first
            // time we render this page with their composable, dynamic versions. Think of this as poor man's
            // hydration :) See also: https://en.wikipedia.org/wiki/Hydration_(web_development)
            val root = document.getElementById("root")!!
            while (root.firstChild != null) {
                root.removeChild(root.firstChild!!)
            }

            renderComposable(rootElementId = "root") {
                ${appFqcn?.let { appFqcn.substringAfterLast('.') } ?: "DefaultApp"} {
                    Router.renderActivePage()
                }
            }
        }
        """.trimIndent()
}