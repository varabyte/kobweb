@file:Suppress("FunctionName", "LocalVariableName")

package com.varabyte.kobweb.ksp.frontend

import com.varabyte.kobweb.ksp.testutil.FrontendKspOptions
import com.varabyte.kobweb.ksp.testutil.KspFixture
import com.varabyte.kobweb.ksp.testutil.appFrontendData
import com.varabyte.kobweb.ksp.testutil.libraryFrontendData
import com.varabyte.truthish.assertThat
import org.junit.Rule
import org.junit.Test

class SilkStyleKspTest {
    @get:Rule
    val ksp = KspFixture()

    @Test
    fun `styles are named after their property`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/Styles.kt" to
                    //language=kotlin
                    """
                    package example.components

                    import com.varabyte.kobweb.silk.style.ComponentKind
                    import com.varabyte.kobweb.silk.style.CssStyle

                    val HeroTextStyle = CssStyle {}
                    val Banner = CssStyle {}

                    sealed interface FancyButtonKind : ComponentKind
                    val FancyButtonStyle = CssStyle<FancyButtonKind> {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.cssStyles.map { it.name to it.fqcn }).containsExactly(
            "hero-text" to "example.components.HeroTextStyle",
            "banner" to "example.components.Banner",
            "fancy-button" to "example.components.FancyButtonStyle",
        )
    }

    @Test
    fun `variants are named relative to their style`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/Button.kt" to
                    //language=kotlin
                    """
                    package example.components

                    import com.varabyte.kobweb.silk.style.ComponentKind
                    import com.varabyte.kobweb.silk.style.CssStyle
                    import com.varabyte.kobweb.silk.style.addVariant

                    sealed interface ButtonKind : ComponentKind
                    val ButtonStyle = CssStyle<ButtonKind> {}

                    val OutlinedButtonVariant = ButtonStyle.addVariant {}
                    val ButtonRoundedVariant = ButtonStyle.addVariant {}
                    val LargeVariant = ButtonStyle.addVariant {}
                    val ButtonVariant = ButtonStyle.addVariant {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.cssStyleVariants.map { it.name to it.fqcn }).containsExactly(
            "-outlined" to "example.components.OutlinedButtonVariant",
            "-rounded" to "example.components.ButtonRoundedVariant",
            "-large" to "example.components.LargeVariant",
            "-button" to "example.components.ButtonVariant",
        )
    }

    @Test
    fun `keyframes are named after their property`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Animations.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.silk.style.animation.Keyframes

                    val BounceKeyframes = Keyframes {}
                    val FadeAnim = Keyframes {}
                    val SpinAnimation = Keyframes {}
                    val Wobble = Keyframes {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.keyframesList.map { it.name to it.fqcn }).containsExactly(
            "bounce" to "example.BounceKeyframes",
            "fade" to "example.FadeAnim",
            "spin" to "example.SpinAnimation",
            "wobble" to "example.Wobble",
        )
    }

    @Test
    fun `css name overrides the generated name`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Styles.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.silk.style.ComponentKind
                    import com.varabyte.kobweb.silk.style.CssName
                    import com.varabyte.kobweb.silk.style.CssStyle
                    import com.varabyte.kobweb.silk.style.addVariant

                    @CssName("info")
                    val InfoBubbleStyle = CssStyle {}

                    sealed interface ButtonKind : ComponentKind
                    val ButtonStyle = CssStyle<ButtonKind> {}

                    @CssName("-gst")
                    val GhostButtonVariant = ButtonStyle.addVariant {}

                    @CssName("outline")
                    val OutlinedButtonVariant = ButtonStyle.addVariant {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        val frontendData = result.appFrontendData.frontendData
        assertThat(frontendData.cssStyles.map { it.name }).containsExactly("info", "button")
        // A leading dash keeps a variant's name relative to its style's name; without one, the name is used as is
        assertThat(frontendData.cssStyleVariants.map { it.name }).containsExactly("-gst", "outline")
    }

    @Test
    fun `styles in objects are namespaced by their container`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Styles.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.silk.style.CssName
                    import com.varabyte.kobweb.silk.style.CssStyle

                    object SiteStyles {
                        val HeroStyle = CssStyle {}
                    }

                    class Card {
                        companion object {
                            val TitleStyle = CssStyle {}
                        }
                    }

                    @CssName("txt")
                    object TextStyles {
                        val BodyStyle = CssStyle {}
                    }
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.cssStyles.map { it.name to it.fqcn }).containsExactly(
            "site-styles_hero" to "example.SiteStyles.HeroStyle",
            "card_title" to "example.Card.Companion.TitleStyle",
            "txt_body" to "example.TextStyles.BodyStyle",
        )
    }

    @Test
    fun `css prefix on a property beats one on its container`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Styles.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.silk.style.CssPrefix
                    import com.varabyte.kobweb.silk.style.CssStyle

                    @CssPrefix("ex")
                    val HeroStyle = CssStyle {}

                    @CssPrefix("ex")
                    object SiteStyles {
                        val BannerStyle = CssStyle {}

                        @CssPrefix("own")
                        val FooterStyle = CssStyle {}
                    }
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.cssStyles.map { it.name }).containsExactly(
            "ex-hero",
            "ex-site-styles_banner",
            "own-site-styles_footer",
        )
    }

    @Test
    fun `default css prefix applies to all names except relative variants`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.library(projectGroup = "example", defaultCssPrefix = "kmd"),
            sources = mapOf(
                "example/components/Button.kt" to
                    //language=kotlin
                    """
                    package example.components

                    import com.varabyte.kobweb.silk.style.ComponentKind
                    import com.varabyte.kobweb.silk.style.CssPrefix
                    import com.varabyte.kobweb.silk.style.CssStyle
                    import com.varabyte.kobweb.silk.style.addVariant
                    import com.varabyte.kobweb.silk.style.animation.Keyframes

                    sealed interface ButtonKind : ComponentKind
                    val ButtonStyle = CssStyle<ButtonKind> {}
                    val OutlinedButtonVariant = ButtonStyle.addVariant {}

                    @CssPrefix("")
                    val PlainStyle = CssStyle {}

                    val SpinKeyframes = Keyframes {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        val frontendData = result.libraryFrontendData
        assertThat(frontendData.cssStyles.map { it.name }).containsExactly("kmd-button", "plain")
        assertThat(frontendData.cssStyleVariants.map { it.name }).containsExactly("-outlined")
        assertThat(frontendData.keyframesList.map { it.name }).containsExactly("kmd-spin")
    }

    @Test
    fun `css layer is recorded`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Styles.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.silk.style.CssLayer
                    import com.varabyte.kobweb.silk.style.CssStyle

                    val RegularStyle = CssStyle {}

                    @CssLayer("important")
                    val ImportantStyle = CssStyle {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.cssStyles.map { it.name to it.layer }).containsExactly(
            "regular" to null,
            "important" to "important",
        )
    }
}
