@file:Suppress("FunctionName")

package com.varabyte.kobweb.ksp.frontend

import com.varabyte.kobweb.ksp.testutil.FrontendKspOptions
import com.varabyte.kobweb.ksp.testutil.KspFixture
import com.varabyte.kobweb.ksp.testutil.appFrontendData
import com.varabyte.truthish.assertThat
import org.junit.Rule
import org.junit.Test

class InitKspTest {
    @get:Rule
    val ksp = KspFixture()

    @Test
    fun `init kobweb functions are collected with or without a context`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Init.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.core.init.InitKobweb
                    import com.varabyte.kobweb.core.init.InitKobwebContext

                    @InitKobweb
                    fun initRouter(ctx: InitKobwebContext) {}

                    @InitKobweb
                    fun initAnalytics() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.kobwebInits.map { it.fqn to it.acceptsContext })
            .containsExactly(
                "example.initRouter" to true,
                "example.initAnalytics" to false,
            )
    }

    @Test
    fun `init silk functions are collected`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Theme.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.silk.init.InitSilk
                    import com.varabyte.kobweb.silk.init.InitSilkContext

                    @InitSilk
                    fun initTheme(ctx: InitSilkContext) {}

                    @InitSilk
                    fun initWidgetStyles(ctx: InitSilkContext) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.silkInits.map { it.fqn })
            .containsExactly("example.initTheme", "example.initWidgetStyles")
    }

    @Test
    fun `init kobweb with other parameters is an error`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Init.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.core.init.InitKobweb

                    class Settings

                    @InitKobweb
                    fun initRouter(settings: Settings) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`example.initRouter` is tagged with the `@InitKobweb` annotation but takes parameters that aren't allowed for init methods. It should take (optionally) an InitKobwebContext parameter or nothing."
        )
    }

    @Test
    fun `init silk without a context is an error`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Theme.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.silk.init.InitSilk

                    @InitSilk
                    fun initTheme() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`example.initTheme` is tagged with the `@InitSilk` annotation but doesn't take an InitSilkContext as its only parameter."
        )
    }

    @Test
    fun `init silk with other parameters is an error`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Theme.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.silk.init.InitSilk

                    class Settings

                    @InitSilk
                    fun initTheme(settings: Settings) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`example.initTheme` is tagged with the `@InitSilk` annotation but doesn't take an InitSilkContext as its only parameter."
        )
    }
}
