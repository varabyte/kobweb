package com.varabyte.kobweb.ksp.testutil

/**
 * Source files declaring stand-ins for the framework annotations that the processors look for.
 *
 * The processors find annotations by fully qualified name, so a stub in the right package works just like the real
 * thing, without needing the real (JS) libraries on the classpath.
 */
object KobwebStubs {
    val frontend = mapOf(
        "androidx/compose/runtime/Composable.kt" to """
            package androidx.compose.runtime

            annotation class Composable
        """.trimIndent(),
        "com/varabyte/kobweb/core/App.kt" to """
            package com.varabyte.kobweb.core

            annotation class App
        """.trimIndent(),
        "com/varabyte/kobweb/core/PackageMapping.kt" to """
            package com.varabyte.kobweb.core

            annotation class PackageMapping(val value: String)
        """.trimIndent(),
        "com/varabyte/kobweb/core/Page.kt" to """
            package com.varabyte.kobweb.core

            annotation class Page(val routeOverride: String = "")
        """.trimIndent(),
        "com/varabyte/kobweb/core/PageContext.kt" to """
            package com.varabyte.kobweb.core

            class PageContext
        """.trimIndent(),
        "com/varabyte/kobweb/core/init/InitKobweb.kt" to """
            package com.varabyte.kobweb.core.init

            annotation class InitKobweb

            class InitKobwebContext
        """.trimIndent(),
        "com/varabyte/kobweb/core/init/InitRoute.kt" to """
            package com.varabyte.kobweb.core.init

            annotation class InitRoute

            class InitRouteContext
        """.trimIndent(),
        "com/varabyte/kobweb/core/layout/Layout.kt" to """
            package com.varabyte.kobweb.core.layout

            annotation class Layout(val fqn: String = "")
        """.trimIndent(),
        "com/varabyte/kobweb/core/layout/NoLayout.kt" to """
            package com.varabyte.kobweb.core.layout

            annotation class NoLayout
        """.trimIndent(),
        "com/varabyte/kobweb/silk/init/InitSilk.kt" to """
            package com.varabyte.kobweb.silk.init

            annotation class InitSilk

            class InitSilkContext
        """.trimIndent(),
        "com/varabyte/kobweb/silk/style/CssLayer.kt" to """
            package com.varabyte.kobweb.silk.style

            annotation class CssLayer(val name: String)
        """.trimIndent(),
        "com/varabyte/kobweb/silk/style/CssName.kt" to """
            package com.varabyte.kobweb.silk.style

            annotation class CssName(val name: String)

            annotation class CssPrefix(val prefix: String)
        """.trimIndent(),
        "com/varabyte/kobweb/silk/style/CssStyle.kt" to """
            package com.varabyte.kobweb.silk.style

            sealed interface CssKind
            sealed interface GeneralKind : CssKind
            sealed interface RestrictedKind : CssKind
            interface ComponentKind : CssKind

            abstract class CssStyle<K : CssKind>

            fun CssStyle(init: () -> Unit): CssStyle<GeneralKind> = object : CssStyle<GeneralKind>() {}

            fun <K : ComponentKind> CssStyle(init: () -> Unit): CssStyle<K> = object : CssStyle<K>() {}
        """.trimIndent(),
        "com/varabyte/kobweb/silk/style/CssStyleVariant.kt" to """
            package com.varabyte.kobweb.silk.style

            abstract class CssStyleVariant<K : ComponentKind>

            fun <K : ComponentKind> CssStyle<K>.addVariant(init: () -> Unit): CssStyleVariant<K> =
                object : CssStyleVariant<K>() {}
        """.trimIndent(),
        "com/varabyte/kobweb/silk/style/animation/Keyframes.kt" to """
            package com.varabyte.kobweb.silk.style.animation

            class Keyframes(init: () -> Unit)
        """.trimIndent(),
    )
}
