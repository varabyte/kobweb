package com.varabyte.kobweb.gradle.application.templates

fun createMainFunction(appFqcn: String?, pageFqcnRoutes: Map<String, String>): String {
    val imports = mutableListOf(
        "com.varabyte.kobweb.core.Router",
        "kotlinx.browser.document",
        "kotlinx.browser.window",
        "org.jetbrains.compose.web.renderComposable",
        // TODO(Bug #13): Move the kobwebHook logic into the server
        "org.w3c.dom.css.ElementCSSInlineStyle",
    )
    imports.add(appFqcn ?: "com.varabyte.kobweb.core.DefaultApp")
    imports.sort()

    // TODO(Bug #13): Move the kobwebHook logic into the server
    return """
        ${
            imports.joinToString("\n        ") { fcqn -> "import $fcqn" }
        }

        // This hook is provided so that a Kobweb server can insert in live reloading logic when run in development
        // mode. Of course, in production, it's a no-op.
        fun kobwebHook() {
            run {
                val root = document.getElementById("root")!!
                var lastVersion: Int? = null
                var checkInterval = 0
                checkInterval = window.setInterval(
                    handler = {
                        window.fetch("${'$'}{window.location.origin}/api/kobweb/status").then {
                            it.text().then { text ->
                                @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
                                (root as ElementCSSInlineStyle).style.opacity = if (text.isNotBlank()) {
                                    "0.3"
                                } else {
                                    "1.0"
                                }
                            }
                        }.catch {
                            // The server was probably taken down, so stop checking.
                            window.clearInterval(checkInterval)
                        }
                        window.fetch("${'$'}{window.location.origin}/api/kobweb/version").then {
                            it.text().then { text ->
                                val version = text.toInt()
                                if (lastVersion == null) {
                                    lastVersion = version
                                }
                                if (lastVersion != version) {
                                    lastVersion = version
                                    window.stop()
                                    window.location.reload()
                                }
                            }
                        }.catch {
                            // The server was probably taken down, so stop checking.
                            window.clearInterval(checkInterval)
                        }
                    },
                    timeout = 250,
                )
            }
        }

        fun main() {
            kobwebHook()

            ${
                // Generates lines like: Router.register("/about") { AboutPage() }
                pageFqcnRoutes.entries.joinToString("\n            ") { entry ->
                    val pageFqcn = entry.key
                    val route = entry.value

                    """Router.register("$route") { $pageFqcn() }"""
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