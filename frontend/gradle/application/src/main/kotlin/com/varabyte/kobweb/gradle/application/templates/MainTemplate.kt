package com.varabyte.kobweb.gradle.application.templates

import com.varabyte.kobweb.gradle.application.BuildTarget

fun createMainFunction(appFqcn: String?, pageFqcnRoutes: Map<String, String>, target: BuildTarget): String {
    val imports = mutableListOf(
        "com.varabyte.kobweb.core.Router",
        "kotlinx.browser.document",
        "kotlinx.browser.window",
        "org.jetbrains.compose.web.renderComposable",
    )

    if (target == BuildTarget.DEBUG) {
        imports.add("kotlinx.dom.hasClass")
        imports.add("kotlinx.dom.removeClass")
        imports.add("com.varabyte.kobweb.compose.css.*")
        imports.add("org.jetbrains.compose.web.css.*")
        imports.add("org.w3c.dom.Element")
        imports.add("org.w3c.dom.get")
    }

    imports.add(appFqcn ?: "com.varabyte.kobweb.core.DefaultApp")
    imports.sort()

    return buildString {
        imports.forEach { fqcn ->
            appendLine("import $fqcn")
        }
        appendLine()
        if (target == BuildTarget.DEBUG) {
            appendLine(
                """
            private fun forceReloadNow() {
                window.stop()
                window.location.reload()
            }

            private fun pollServerStatus() {
                val status = document.getElementById("status")!!
                var lastVersion: Int? = null
                var shouldReload = false

                val warningIcon = status.children[0]!!
                val spinnerIcon = status.children[1]!!
                val statusText = status.children[2]!!

                var lastStatusResponse = ""

                status.addEventListener("transitionend", {
                    if (status.hasClass("fade-out")) {
                        status.removeClass("fade-out")
                        if (shouldReload) {
                            forceReloadNow()
                        }
                    }
                })

                var checkInterval = 0
                checkInterval = window.setInterval(
                    handler = {
                        window.fetch("/api/kobweb/status").then {
                            it.text().then { response ->
                                if (response != lastStatusResponse) {
                                    lastStatusResponse = response
                                    val values: dynamic = JSON.parse<Any>(response)
                                    val text = values.text as String
                                    val isError = (values.isError as String).toBoolean()
                                    if (text.isNotBlank()) {
                                        warningIcon.className = if (isError) "visible" else "hidden"
                                        spinnerIcon.className = if (isError) "hidden" else "visible"
                                        statusText.innerHTML = "<i>${'$'}text</i>"
                                        status.className = "fade-in"
                                    }
                                    else {
                                        if (status.className == "fade-in") {
                                            status.className = "fade-out"
                                        }
                                    }
                                }
                            }
                        }.catch {
                            // The server was probably taken down, so stop checking.
                            window.clearInterval(checkInterval)
                        }

                        window.fetch("/api/kobweb/version").then {
                            it.text().then { response ->
                                val version = response.toInt()
                                if (lastVersion == null) {
                                    lastVersion = version
                                }
                                if (lastVersion != version) {
                                    lastVersion = version
                                    if (status.className.isNotEmpty()) {
                                        shouldReload = true
                                    } else {
                                        forceReloadNow()
                                    }
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
                """.trimIndent()
            )
            appendLine()
        }

        appendLine("fun main() {")
        if (target == BuildTarget.DEBUG) {
            appendLine("    pollServerStatus()")
        }
        pageFqcnRoutes.entries.forEach { entry ->
            val pageFqcn = entry.key
            val route = entry.value

            appendLine("""    Router.register("$route") { $pageFqcn() }""")
        }

        appendLine()
        appendLine(
            """
                Router.navigateTo(window.location.pathname + window.location.search)

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
        )
    }
}