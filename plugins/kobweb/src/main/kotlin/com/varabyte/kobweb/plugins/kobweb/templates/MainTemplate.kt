package com.varabyte.kobweb.plugins.kobweb.templates

fun createMainFunction(appFqcn: String?, pageFqcnRoutes: Map<String, String>, defaultRoute: String): String {
    val imports = mutableListOf(
        "com.varabyte.kobweb.core.Router",
        "org.jetbrains.compose.web.renderComposable",
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
            Router.navigateTo("$defaultRoute")

            renderComposable(rootElementId = "root") {
                ${appFqcn?.let { appFqcn.substringAfterLast('.') } ?: "DefaultApp"} {
                    Router.renderActivePage()
                }
            }
        }
        """.trimIndent()
}