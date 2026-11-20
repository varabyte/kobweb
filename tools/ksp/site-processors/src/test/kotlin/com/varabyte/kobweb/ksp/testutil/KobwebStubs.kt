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
        "com/varabyte/kobweb/core/layout/Layout.kt" to """
            package com.varabyte.kobweb.core.layout

            annotation class Layout(val fqn: String = "")
        """.trimIndent(),
        "com/varabyte/kobweb/core/layout/NoLayout.kt" to """
            package com.varabyte.kobweb.core.layout

            annotation class NoLayout
        """.trimIndent(),
        "com/varabyte/kobweb/core/init/InitRoute.kt" to """
            package com.varabyte.kobweb.core.init

            annotation class InitRoute

            class InitRouteContext
        """.trimIndent(),
    )
}
