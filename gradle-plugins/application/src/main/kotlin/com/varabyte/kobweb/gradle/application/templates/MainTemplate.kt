package com.varabyte.kobweb.gradle.application.templates

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.withIndent
import com.varabyte.kobweb.common.navigation.RoutePrefix
import com.varabyte.kobweb.gradle.application.BuildTarget
import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.application.project.app.AppData
import com.varabyte.kobweb.gradle.core.project.frontend.FrontendData
import com.varabyte.kobweb.gradle.core.project.frontend.merge

private const val KOBWEB_GROUP = "com.varabyte.kobweb"

fun createMainFunction(
    appData: AppData,
    libData: List<FrontendData>,
    usingSilk: Boolean,
    appBlock: AppBlock,
    routePrefix: RoutePrefix,
    target: BuildTarget
): String {
    val appFqn = appData.appEntry?.fqn
        ?: (KOBWEB_GROUP + if (usingSilk) ".silk.SilkApp" else ".core.KobwebApp")

    val frontendData = (listOf(appData.frontendData) + libData).merge()
    val fileBuilder = FileSpec.builder("", "main").indent(" ".repeat(4))

    buildList {
        val defaultImports = listOf(
            "androidx.compose.runtime.CompositionLocalProvider",
            "$KOBWEB_GROUP.core.AppGlobalsLocal",
            "$KOBWEB_GROUP.navigation.RoutePrefix",
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
        if (frontendData.keyframesList.isNotEmpty()) {
            add("$KOBWEB_GROUP.silk.components.animation.registerKeyframes")
        }
        if (usingSilk) {
            add("$KOBWEB_GROUP.silk.defer.renderWithDeferred")
        }
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
                                if (status.className.isNotEmpty()) {
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
                addStatement("RoutePrefix.set(\"$routePrefix\")")
                addStatement("val router = Router()")
                addStatement("$KOBWEB_GROUP.core.init.initKobweb(router) { ctx ->")
                withIndent {
                    frontendData.pages.sortedBy { it.route }.forEach { entry ->
                        addStatement("""ctx.router.register("${entry.route}") { ${entry.fqn}() }""")
                    }
                    addStatement("")

                    frontendData.kobwebInits.forEach { entry ->
                        val ctx = if (entry.acceptsContext) "ctx" else ""
                        addStatement("${entry.fqn}($ctx)")
                    }
                }
                addStatement("}")
                if (appBlock.cleanUrls.get()) {
                    addStatement("router.addRouteInterceptor {")
                    withIndent {
                        addStatement("path = path.removeSuffix(\".html\").removeSuffix(\".htm\")")
                    }
                    addStatement("}")
                }
                addStatement("")
            }.build())

            val needsSilkInit = frontendData.silkInits.isNotEmpty() || frontendData.silkStyles.isNotEmpty()
                || frontendData.silkVariants.isNotEmpty() || frontendData.keyframesList.isNotEmpty()
            if (usingSilk && needsSilkInit) {
                addCode(CodeBlock.builder().apply {
                    addStatement("$KOBWEB_GROUP.silk.init.initSilkHook = { ctx ->")
                    withIndent {
                        frontendData.silkStyles.forEach { entry ->
                            addStatement("ctx.theme.registerComponentStyle(${entry.fqcn})")
                        }
                        frontendData.silkVariants.forEach { entry ->
                            addStatement("ctx.theme.registerComponentVariants(${entry.fqcn})")
                        }
                        frontendData.keyframesList.forEach { entry ->
                            addStatement("ctx.stylesheet.registerKeyframes(${entry.fqcn})")
                        }
                        frontendData.silkInits.forEach { init ->
                            addStatement("${init.fqn}(ctx)")
                        }
                    }
                    addStatement("}")
                }.build())
                addStatement("")
            }

            val appGlobals = appBlock.globals.get()

            // Note: Below, we use %S when specifying key/value pairs. This prevents KotlinPoet from breaking
            // our text in the middle of a String.
            addCode(
                """
                router.navigateTo(window.location.href.removePrefix(window.location.origin), UpdateHistoryMode.REPLACE)

                // For SEO, we may bake the contents of a page in at build time. However, we will overwrite them
                // the first time we render this page with their composable, dynamic versions. Think of this as
                // poor man's hydration :)
                // See also: https://en.wikipedia.org/wiki/Hydration_(web_development)
                val root = document.getElementById("root")!!
                while (root.firstChild != null) {
                    root.removeChild(root.firstChild!!)
                }

                renderComposable(rootElementId = "root") {
                    CompositionLocalProvider(
                        AppGlobalsLocal provides mapOf(${Array(appGlobals.size) { "%S to %S" }.joinToString()})
                    ) { $appFqn {
                          ${if (usingSilk) "router.renderActivePage { renderWithDeferred { it() } }" else "router.renderActivePage()"}
                    } }
                }
                """.trimIndent(),
                *appGlobals.flatMap { entry -> listOf(entry.key, entry.value) }.toTypedArray()
            )
        }.build()
    )

    return fileBuilder.build().toString()
}
