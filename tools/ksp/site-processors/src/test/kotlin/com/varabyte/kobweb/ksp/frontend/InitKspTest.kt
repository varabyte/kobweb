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
}
