@file:Suppress("FunctionName")

package com.varabyte.kobweb.ksp.frontend

import com.varabyte.kobweb.ksp.testutil.FrontendKspOptions
import com.varabyte.kobweb.ksp.testutil.KspFixture
import com.varabyte.kobweb.ksp.testutil.libraryFrontendData
import com.varabyte.kobweb.ksp.testutil.runFrontend
import com.varabyte.truthish.assertThat
import org.junit.Rule
import org.junit.Test

class LibraryKspTest {
    @get:Rule
    val ksp = KspFixture()

    @Test
    fun `library writes its frontend data to META-INF`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.library(projectGroup = "example"),
            sources = mapOf(
                "example/pages/About.kt" to
                    //language=kotlin
                    """
                    package example.pages

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page

                    @Page
                    @Composable
                    fun AboutPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.generatedFiles.keys).containsExactly("META-INF/kobweb/frontend.json")
        assertThat(result.libraryFrontendData.pages.map { it.fqn to it.route })
            .containsExactly("example.pages.AboutPage" to "/about")
    }
}
