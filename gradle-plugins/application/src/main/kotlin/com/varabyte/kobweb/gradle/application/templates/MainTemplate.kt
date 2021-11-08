package com.varabyte.kobweb.gradle.application.templates

import com.varabyte.kobweb.gradle.application.BuildTarget

fun createMainFunction(appFqcn: String?, pageFqcnRoutes: Map<String, String>, target: BuildTarget): String {
    val imports = mutableListOf(
        "com.varabyte.kobweb.navigation.Router",
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
        imports.add("org.w3c.dom.EventSource")
        imports.add("org.w3c.dom.EventSourceInit")
        imports.add("org.w3c.dom.MessageEvent")
        imports.add("org.w3c.dom.get")
    }

    imports.add(appFqcn ?: "com.varabyte.kobweb.core.KobwebApp")
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

            private fun handleServerStatusEvents() {
                val status = document.getElementById("status")!!
                var lastVersion: Int? = null
                var shouldReload = false

                val warningIcon = status.children[0]!!
                val spinnerIcon = status.children[1]!!
                val statusText = status.children[2]!!

                status.addEventListener("transitionend", {
                    if (status.hasClass("fade-out")) {
                        status.removeClass("fade-out")
                        if (shouldReload) {
                            forceReloadNow()
                        }
                    }
                })

                val eventSource = EventSource("/api/kobweb-status", EventSourceInit(true))
                eventSource.addEventListener("version", { evt ->
                    val version = (evt as MessageEvent).data.toString().toInt()
                    if (lastVersion == null) {
                        lastVersion = version
                    }
                    if (lastVersion != version) {
                        lastVersion = version
                        if (status.className.isNotEmpty()) {
                            shouldReload = true
                        } else {
                            // Not sure if we can get here but if we can't rely on the status transition to reload
                            // we should do it ourselves.
                            forceReloadNow()
                        }
                    }
                })

                eventSource.addEventListener("status", { evt ->
                    val values: dynamic = JSON.parse<Any>((evt as MessageEvent).data.toString())
                    val text = values.text as String
                    val isError = (values.isError as String).toBoolean()
                    if (text.isNotBlank()) {
                        warningIcon.className = if (isError) "visible" else "hidden"
                        spinnerIcon.className = if (isError) "hidden" else "visible"
                        statusText.innerHTML = "<i>${'$'}text</i>"
                        status.className = "fade-in"
                    } else {
                        if (status.className == "fade-in") {
                            status.className = "fade-out"
                        }
                    }
                })

                eventSource.onerror = { eventSource.close() }
            }
                """.trimIndent()
            )
            appendLine()
        }

        appendLine("fun main() {")
        if (target == BuildTarget.DEBUG) {
            appendLine("    handleServerStatusEvents()")
            appendLine()
        }
        appendLine("    val router = Router()")
        pageFqcnRoutes.entries.forEach { entry ->
            val pageFqcn = entry.key
            val route = entry.value

            appendLine("""    router.register("$route") { $pageFqcn() }""")
        }

        appendLine()
        appendLine(
            """
                router.navigateTo(window.location.pathname + window.location.search)

                // For SEO, we may bake the contents of a page in at build time. However, we will overwrite them the first
                // time we render this page with their composable, dynamic versions. Think of this as poor man's
                // hydration :) See also: https://en.wikipedia.org/wiki/Hydration_(web_development)
                val root = document.getElementById("root")!!
                while (root.firstChild != null) {
                    root.removeChild(root.firstChild!!)
                }

                renderComposable(rootElementId = "root") {
                    ${appFqcn?.let { appFqcn.substringAfterLast('.') } ?: "KobwebApp"} {
                        router.renderActivePage()
                    }
                }
            }
            """.trimIndent()
        )
    }
}