@file:Suppress("FunctionName", "LocalVariableName", "PropertyName")

package com.varabyte.kobweb.ksp.frontend

import com.varabyte.kobweb.ksp.testutil.FrontendKspOptions
import com.varabyte.kobweb.ksp.testutil.KspFixture
import com.varabyte.kobweb.ksp.testutil.appFrontendData
import com.varabyte.kobweb.ksp.testutil.libraryFrontendData
import com.varabyte.kobweb.ksp.testutil.runFrontend
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

    @Test
    fun `styles declared in a class are not registered`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Widget.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.silk.style.CssStyle

                    class Widget {
                        val WidgetStyle = CssStyle {}
                    }
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).containsExactly(
            "Not registering css style `val WidgetStyle`, as local style block declarations are not supported. You can only declare them globally, either at a top level or inside an object singleton. Although fixing this is recommended, you can manually register your css style inside an @InitSilk block instead (`ctx.theme.registerStyle(\"widget\", WidgetStyle)`). Suppress this message by adding a `@Suppress(\"LOCAL_CSS_STYLE\")` annotation."
        )
        assertThat(result.appFrontendData.frontendData.cssStyles).isEmpty()
    }

    @Test
    fun `local style warning can be suppressed`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Widget.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.silk.style.CssStyle

                    class Widget {
                        @Suppress("LOCAL_CSS_STYLE")
                        val WidgetStyle = CssStyle {}
                    }
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.cssStyles).isEmpty()
    }

    @Test
    fun `private styles are not registered`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Styles.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.silk.style.CssStyle

                    private val HiddenStyle = CssStyle {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).containsExactly(
            "Not registering css style `val HiddenStyle`, as it is not public. Although fixing this is recommended, you can manually register your css style inside an @InitSilk block instead (`ctx.theme.registerStyle(\"hidden\", HiddenStyle)`). Suppress this message by adding a `@Suppress(\"PRIVATE_CSS_STYLE\")` annotation or prepending the name with an underscore."
        )
        assertThat(result.appFrontendData.frontendData.cssStyles).isEmpty()
    }

    @Test
    fun `private variants and keyframes are not registered`() {
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
                    import com.varabyte.kobweb.silk.style.animation.Keyframes

                    sealed interface ButtonKind : ComponentKind
                    val ButtonStyle = CssStyle<ButtonKind> {}
                    private val OutlinedButtonVariant = ButtonStyle.addVariant {}

                    private val SpinKeyframes = Keyframes {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).containsExactly(
            "Not registering css style variant `val OutlinedButtonVariant`, as it is not public. Although fixing this is recommended, you can manually register your css style variant inside an @InitSilk block instead (`ctx.theme.registerVariant(\"-outlined\", OutlinedButtonVariant)`). Suppress this message by adding a `@Suppress(\"PRIVATE_CSS_STYLE_VARIANT\")` annotation or prepending the name with an underscore.",
            "Not registering keyframes `val SpinKeyframes`, as it is not public. Although fixing this is recommended, you can manually register your keyframes inside an @InitSilk block instead (`ctx.theme.registerKeyframes(\"spin\", SpinKeyframes)`). Suppress this message by adding a `@Suppress(\"PRIVATE_KEYFRAMES\")` annotation or prepending the name with an underscore.",
        )
        val frontendData = result.appFrontendData.frontendData
        assertThat(frontendData.cssStyleVariants).isEmpty()
        assertThat(frontendData.keyframesList).isEmpty()
    }

    @Test
    fun `private style warning can be suppressed with an annotation`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Styles.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.silk.style.CssStyle

                    @Suppress("PRIVATE_CSS_STYLE")
                    private val HiddenStyle = CssStyle {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.cssStyles).isEmpty()
    }

    @Test
    fun `private style warning can be suppressed with a leading underscore`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Styles.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.silk.style.CssStyle

                    private val _HeroStyle = CssStyle {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.cssStyles).isEmpty()
    }

    @Test
    fun `a component kind can only be used by one style`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/Button.kt" to
                    //language=kotlin
                    """
                    package example.components

                    import com.varabyte.kobweb.silk.style.ComponentKind
                    import com.varabyte.kobweb.silk.style.CssStyle

                    sealed interface ButtonKind : ComponentKind
                    val ButtonStyle = CssStyle<ButtonKind> {}
                    val FancyButtonStyle = CssStyle<ButtonKind> {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`ButtonKind` can only be associated with a single CSS style. `val FancyButtonStyle = CssStyle<ButtonKind>` was declared after `val ButtonStyle = CssStyle<ButtonKind>`. Either remove one of these style declarations, or create a new `ComponentKind` type for one of them."
        )
    }

    @Test
    fun `a component style must be declared in the same file as its kind`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/ButtonKind.kt" to
                    //language=kotlin
                    """
                    package example.components

                    import com.varabyte.kobweb.silk.style.ComponentKind

                    sealed interface ButtonKind : ComponentKind
                """.trimIndent(),
                "example/components/ButtonStyle.kt" to
                    //language=kotlin
                    """
                    package example.components

                    import com.varabyte.kobweb.silk.style.CssStyle

                    val ButtonStyle = CssStyle<ButtonKind> {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`val ButtonStyle = CssStyle<ButtonKind>` is using a kind type defined in a different file, which is not allowed. Please declare a new `ComponentKind` implementation in the same file and use that one, or declare your `CssStyle<ButtonKind>` in the same file as the `ButtonKind` definition."
        )
    }

    @Test
    fun `extension properties on singletons are namespaced by their receiver`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/lib/Styling.kt" to
                    //language=kotlin
                    """
                    package example.lib

                    object LibStyles

                    class Card {
                        companion object
                    }
                """.trimIndent(),
                "example/Extensions.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.silk.style.CssStyle
                    import example.lib.Card
                    import example.lib.LibStyles

                    private val _HeroStyle = CssStyle {}
                    val LibStyles.HeroStyle get() = _HeroStyle

                    private val _TitleStyle = CssStyle {}
                    val Card.Companion.TitleStyle get() = _TitleStyle
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        // Generated code needs the import to access an extension property
        assertThat(result.appFrontendData.frontendData.cssStyles.map { Triple(it.name, it.fqcn, it.import) })
            .containsExactly(
                Triple("lib-styles_hero", "example.lib.LibStyles.HeroStyle", "example.HeroStyle"),
                Triple("card_title", "example.lib.Card.TitleStyle", "example.TitleStyle"),
            )
    }

    @Test
    fun `variants of nested component kinds drop the full kind name`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/Switch.kt" to
                    //language=kotlin
                    """
                    package example.components

                    import com.varabyte.kobweb.silk.style.ComponentKind
                    import com.varabyte.kobweb.silk.style.CssStyle
                    import com.varabyte.kobweb.silk.style.addVariant

                    sealed interface SwitchKind : ComponentKind {
                        sealed interface Track : ComponentKind
                        sealed interface Thumb : ComponentKind
                    }

                    val SwitchStyle = CssStyle<SwitchKind> {}
                    val SwitchTrackStyle = CssStyle<SwitchKind.Track> {}
                    val SwitchThumbStyle = CssStyle<SwitchKind.Thumb> {}

                    val ThinSwitchTrackVariant = SwitchTrackStyle.addVariant {}
                    val LargeSwitchThumbVariant = SwitchThumbStyle.addVariant {}
                    val SwitchThumbRoundVariant = SwitchThumbStyle.addVariant {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        val frontendData = result.appFrontendData.frontendData
        assertThat(frontendData.cssStyles.map { it.name }).containsExactly("switch", "switch-track", "switch-thumb")
        assertThat(frontendData.cssStyleVariants.map { it.name }).containsExactly("-thin", "-large", "-round")
    }
}
