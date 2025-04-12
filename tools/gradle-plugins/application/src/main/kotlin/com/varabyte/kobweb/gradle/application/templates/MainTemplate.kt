package com.varabyte.kobweb.gradle.application.templates

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.withIndent
import com.varabyte.kobweb.common.navigation.BasePath
import com.varabyte.kobweb.gradle.application.BuildTarget
import com.varabyte.kobweb.project.conf.Server
import com.varabyte.kobweb.project.frontend.AppFrontendData

private const val KOBWEB_GROUP = "com.varabyte.kobweb"

enum class SilkSupport {
    NONE,
    FOUNDATION,
    FULL,
}

fun createMainFunction(
    appFrontendData: AppFrontendData,
    silkSupport: SilkSupport,
    appGlobals: Map<String, String>,
    cleanUrls: Boolean,
    basePath: BasePath,
    redirects: List<Server.Redirect>,
    target: BuildTarget,
): String {
    val usingSilkFoundation = silkSupport != SilkSupport.NONE
    val usingSilkWidgets = silkSupport == SilkSupport.FULL

    val appFqn = appFrontendData.appEntry?.fqn
        ?: (KOBWEB_GROUP + if (usingSilkWidgets) ".silk.SilkApp" else ".core.KobwebApp")

    val frontendData = appFrontendData.frontendData
    val fileBuilder = FileSpec.builder("", "main").indent(" ".repeat(4))

    buildSet {
        val defaultImports = listOf(
            "androidx.compose.runtime.CompositionLocalProvider",
            "$KOBWEB_GROUP.core.AppGlobals",
            "$KOBWEB_GROUP.navigation.remove",
            "$KOBWEB_GROUP.navigation.BasePath",
            "$KOBWEB_GROUP.navigation.Router",
            "$KOBWEB_GROUP.navigation.UpdateHistoryMode",
            "kotlinx.browser.document",
            "kotlinx.browser.window",
            "org.jetbrains.compose.web.renderComposable",
        )
        addAll(defaultImports)
        if (target == BuildTarget.DEBUG) {
            val debugImports = listOf(
                "$KOBWEB_GROUP.browser.api",
                "kotlinx.dom.hasClass",
                "kotlinx.dom.removeClass",
                "org.w3c.dom.EventSource",
                "org.w3c.dom.EventSourceInit",
                "org.w3c.dom.MessageEvent",
                "org.w3c.dom.get",
            )
            addAll(debugImports)
        }
        if (frontendData.kobwebInits.any { it.acceptsContext }) {
            add("$KOBWEB_GROUP.core.init.InitKobwebContext")
        }
        if (usingSilkFoundation) {
            add("$KOBWEB_GROUP.silk.defer.DeferringHost")
        }
        frontendData.cssStyles.mapNotNull { it.import }.forEach { add(it) }
        frontendData.cssStyleVariants.mapNotNull { it.import }.forEach { add(it) }
        frontendData.keyframesList.mapNotNull { it.import }.forEach { add(it) }

    }.sorted().forEach { import ->
        fileBuilder.addImport(import.substringBeforeLast('.'), import.substringAfterLast('.'))
    }

    // region debug-only functions
    if (target == BuildTarget.DEBUG) {
        fileBuilder.addFunction(
            FunSpec.builder("forceReloadNow")
                .addModifiers(KModifier.PRIVATE)
                .addCode(
                    """
                        window.stop()
                        window.location.reload()
                    """.trimIndent()
                )
                .build()
        )

        fileBuilder.addFunction(
            FunSpec.builder("handleServerStatusEvents")
                .addModifiers(KModifier.PRIVATE)
                // language=kotlin
                .addCode(
                    """
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
                                if (document.asDynamic().hidden) {
                                    // Reload immediately when the page is not visible as the animation will not run
                                    forceReloadNow()
                                } else if (status.className.isNotEmpty()) {
                                    shouldReload = true
                                } else {
                                    // Not sure if we can get here but if we can't rely on the status transition
                                    // to reload we should do it ourselves.
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
                    """.trimIndent()
                ).build()
        )
    }
    // endregion

    fileBuilder.addFunction(
        FunSpec.builder("main").apply {
            if (target == BuildTarget.DEBUG) {
                addStatement("handleServerStatusEvents()")
                addStatement("")
                addStatement("window.api.logOnError = true")
                addStatement("")
            }

            addCode(CodeBlock.builder().apply {
                // We use %S when specifying key/value pairs. This prevents KotlinPoet from breaking our text in the
                // middle of a String.
                addStatement(
                    "AppGlobals.initialize(mapOf(${Array(appGlobals.size) { "%S to %S" }.joinToString()}))",
                    *appGlobals.flatMap { entry -> listOf(entry.key, entry.value) }.toTypedArray()
                )
                addStatement("BasePath.set(\"$basePath\")")
                addStatement("val router = Router()")
                addStatement("$KOBWEB_GROUP.core.init.initKobweb(router) { ctx ->")
                withIndent {
                    frontendData.pages.sortedBy { it.route }.forEach { entry ->
                        addStatement(
                            buildString {
                                append("ctx.router.register(\"${entry.route}\") { ")
                                if (entry.acceptsContext) {
                                    append("pageCtx -> ")
                                }
                                append("${entry.fqn}(")
                                if (entry.acceptsContext) {
                                    append("pageCtx")
                                }
                                append(") }")
                            }
                        )
                    }
                    redirects.sortedBy { it.to }.forEach { entry ->
                        addStatement("""ctx.router.registerRedirect("${entry.from}", "${entry.to}")""")
                    }
                    addStatement("")

                    frontendData.kobwebInits.forEach { entry ->
                        val ctx = if (entry.acceptsContext) "ctx" else ""
                        addStatement("${entry.fqn}($ctx)")
                    }
                }
                addStatement("}")
                if (cleanUrls) {
                    addStatement("router.addRouteInterceptor {")
                    withIndent {
                        addStatement("path = path.removeSuffix(\".html\").removeSuffix(\".htm\")")
                    }
                    addStatement("}")
                }
                addStatement("")
            }.build())

            if (usingSilkFoundation) {
                addCode(CodeBlock.builder().apply {
                    addStatement("$KOBWEB_GROUP.silk.init.additionalSilkInitialization = { ctx ->")
                    withIndent {
                        if (usingSilkWidgets) {
                            addStatement("com.varabyte.kobweb.silk.init.initSilkWidgets(ctx)")
                            addStatement("com.varabyte.kobweb.silk.init.initSilkWidgetsKobweb(ctx)")
                        }
                        frontendData.cssStyles.forEach { entry ->
                            addStatement(
                                buildString {
                                    append("ctx.theme.registerStyle(\"${entry.name}\", ${entry.fqcn}")
                                    entry.layer?.let { layer -> append(", layer = \"$layer\"") }
                                    append(")")
                                }
                            )
                        }
                        frontendData.cssStyleVariants.forEach { entry ->
                            addStatement(
                                buildString {
                                    addStatement("ctx.theme.registerVariant(\"${entry.name}\", ${entry.fqcn}")
                                    entry.layer?.let { layer -> append(", layer = \"$layer\"") }
                                    append(")")
                                }
                            )
                        }
                        frontendData.keyframesList.forEach { entry ->
                            addStatement("ctx.theme.registerKeyframes(\"${entry.name}\", ${entry.fqcn})")
                        }
                        frontendData.silkInits.forEach { init ->
                            addStatement("${init.fqn}(ctx)")
                        }
                    }
                    addStatement("}")
                }.build())
                addStatement("")
            }

            addCode(CodeBlock.Builder().apply {
                addStatement("router.tryRoutingTo(BasePath.remove(window.location.href.removePrefix(window.origin)), UpdateHistoryMode.REPLACE)")
                addStatement("")
            }.build())

            addComment("For SEO, we may bake the contents of a page in at build time. However, we will")
            addComment("overwrite them the first time we render this page with their composable, dynamic")
            addComment("versions. Think of this as poor man's hydration :)")
            addComment("See also: https://en.wikipedia.org/wiki/Hydration_(web_development)")
            addCode(CodeBlock.Builder().apply {
                addStatement("val root = document.getElementById(\"_kobweb-root\")!!")
                addStatement("while (root.firstChild != null) { root.removeChild(root.firstChild!!) }")
                addStatement("")
            }.build())

            addCode(CodeBlock.Builder().apply {
                addStatement("renderComposable(rootElementId = \"_kobweb-root\") {")
                withIndent {
                    addStatement("$appFqn {")
                    withIndent {
                        if (usingSilkFoundation) {
                            addStatement("router.renderActivePage { DeferringHost { it() } }")
                        } else {
                            addStatement("router.renderActivePage()")
                        }
                    }
                    addStatement("}")
                }
                addStatement("}")
            }.build())
        }.build()
    )

    return fileBuilder.build().toString()
}
