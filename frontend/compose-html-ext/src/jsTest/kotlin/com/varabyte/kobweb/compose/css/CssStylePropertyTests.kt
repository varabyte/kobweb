package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSImage
import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.compose.css.functions.brightness
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.truthish.assertThat
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import kotlin.test.Test

class CssStylePropertyTests {
    // Convert all properties in a style to the String that would ultimately get put into an HTML style attribute.
    // In other words, key / values will be split by a ':' and multiple properties by a ';'
    private fun styleToText(block: StyleScope.() -> Unit): String {
        // We don't care about comparing -- but it's an easy way to construct a style scope, as Compose HTML doesn't
        // give us an easy way otherwise.
        val styleScope = ComparableStyleScope()
        block.invoke(styleScope)

        return styleScope.properties.entries.joinToString("; ") { (key, value) -> "$key: $value" }
    }

    @Test
    fun verifyAccentColor() {
        assertThat(styleToText { accentColor(AccentColor.of(Color.red)) }).isEqualTo("accent-color: red")

        assertThat(styleToText { accentColor(AccentColor.Auto) }).isEqualTo("accent-color: auto")

        assertThat(styleToText { accentColor(AccentColor.Inherit) }).isEqualTo("accent-color: inherit")
        assertThat(styleToText { accentColor(AccentColor.Initial) }).isEqualTo("accent-color: initial")
        assertThat(styleToText { accentColor(AccentColor.Revert) }).isEqualTo("accent-color: revert")
        assertThat(styleToText { accentColor(AccentColor.Unset) }).isEqualTo("accent-color: unset")
    }

    @Test
    fun verifyAnimation() {
        assertThat(styleToText {
            animation(
                Animation.of(
                    "slide-in",
                    duration = 3.s,
                    timingFunction = AnimationTimingFunction.EaseIn,
                    delay = 1.s,
                    iterationCount = AnimationIterationCount.of(2),
                    direction = AnimationDirection.Reverse,
                    fillMode = AnimationFillMode.Both,
                    playState = AnimationPlayState.Paused,
                )
            )
        }).isEqualTo("animation: 3s ease-in 1s 2 reverse both paused slide-in")

        assertThat(styleToText {
            animation(
                Animation.of(
                    "slide-in",
                    duration = 3.s,
                    timingFunction = AnimationTimingFunction.Linear,
                ),
                Animation.of(
                    "slide-out",
                    duration = 3.s,
                    timingFunction = AnimationTimingFunction.EaseOut,
                    delay = 5.s
                )
            )
        }).isEqualTo("animation: 3s linear slide-in, 3s ease-out 5s slide-out")

        assertThat(styleToText { animation(Animation.None) }).isEqualTo("animation: none")

        assertThat(styleToText { animation(Animation.Inherit) }).isEqualTo("animation: inherit")
        assertThat(styleToText { animation(Animation.Initial) }).isEqualTo("animation: initial")
        assertThat(styleToText { animation(Animation.Revert) }).isEqualTo("animation: revert")
        assertThat(styleToText { animation(Animation.Unset) }).isEqualTo("animation: unset")
    }

    @Test
    fun verifyAlignContent() {
        assertThat(styleToText { alignContent(AlignContent.Normal) }).isEqualTo("align-content: normal")

        // Positional
        assertThat(styleToText { alignContent(AlignContent.Center) }).isEqualTo("align-content: center")
        assertThat(styleToText { alignContent(AlignContent.Start) }).isEqualTo("align-content: start")
        assertThat(styleToText { alignContent(AlignContent.End) }).isEqualTo("align-content: end")
        assertThat(styleToText { alignContent(AlignContent.FlexStart) }).isEqualTo("align-content: flex-start")
        assertThat(styleToText { alignContent(AlignContent.FlexEnd) }).isEqualTo("align-content: flex-end")

        // Distributed
        assertThat(styleToText { alignContent(AlignContent.SpaceBetween) }).isEqualTo("align-content: space-between")
        assertThat(styleToText { alignContent(AlignContent.SpaceAround) }).isEqualTo("align-content: space-around")
        assertThat(styleToText { alignContent(AlignContent.SpaceEvenly) }).isEqualTo("align-content: space-evenly")
        assertThat(styleToText { alignContent(AlignContent.Stretch) }).isEqualTo("align-content: stretch")

        // Baseline 
        assertThat(styleToText { alignContent(AlignContent.Baseline) }).isEqualTo("align-content: baseline")
        assertThat(styleToText { alignContent(AlignContent.FirstBaseline) }).isEqualTo("align-content: first baseline")
        assertThat(styleToText { alignContent(AlignContent.LastBaseline) }).isEqualTo("align-content: last baseline")

        // Overflow
        assertThat(styleToText { alignContent(AlignContent.Safe(AlignContent.Center)) }).isEqualTo("align-content: safe center")
        assertThat(styleToText { alignContent(AlignContent.Unsafe(AlignContent.FlexEnd)) }).isEqualTo("align-content: unsafe flex-end")

        // Global 
        assertThat(styleToText { alignContent(AlignContent.Inherit) }).isEqualTo("align-content: inherit")
        assertThat(styleToText { alignContent(AlignContent.Initial) }).isEqualTo("align-content: initial")
        assertThat(styleToText { alignContent(AlignContent.Revert) }).isEqualTo("align-content: revert")
        assertThat(styleToText { alignContent(AlignContent.Unset) }).isEqualTo("align-content: unset")
    }

    @Test
    fun verifyAlignItems() {
        assertThat(styleToText { alignItems(AlignItems.Normal) }).isEqualTo("align-items: normal")
        assertThat(styleToText { alignItems(AlignItems.Stretch) }).isEqualTo("align-items: stretch")

        assertThat(styleToText { alignItems(AlignItems.Center) }).isEqualTo("align-items: center")
        assertThat(styleToText { alignItems(AlignItems.Start) }).isEqualTo("align-items: start")
        assertThat(styleToText { alignItems(AlignItems.End) }).isEqualTo("align-items: end")
        assertThat(styleToText { alignItems(AlignItems.SelfStart) }).isEqualTo("align-items: self-start")
        assertThat(styleToText { alignItems(AlignItems.SelfEnd) }).isEqualTo("align-items: self-end")
        assertThat(styleToText { alignItems(AlignItems.FlexStart) }).isEqualTo("align-items: flex-start")
        assertThat(styleToText { alignItems(AlignItems.FlexEnd) }).isEqualTo("align-items: flex-end")

        assertThat(styleToText { alignItems(AlignItems.Baseline) }).isEqualTo("align-items: baseline")
        assertThat(styleToText { alignItems(AlignItems.FirstBaseline) }).isEqualTo("align-items: first baseline")
        assertThat(styleToText { alignItems(AlignItems.LastBaseline) }).isEqualTo("align-items: last baseline")

        assertThat(styleToText { alignItems(AlignItems.Safe(AlignItems.Center)) }).isEqualTo("align-items: safe center")
        assertThat(styleToText { alignItems(AlignItems.Unsafe(AlignItems.FlexEnd)) }).isEqualTo("align-items: unsafe flex-end")

        assertThat(styleToText { alignItems(AlignItems.Inherit) }).isEqualTo("align-items: inherit")
        assertThat(styleToText { alignItems(AlignItems.Initial) }).isEqualTo("align-items: initial")
        assertThat(styleToText { alignItems(AlignItems.Revert) }).isEqualTo("align-items: revert")
        assertThat(styleToText { alignItems(AlignItems.Unset) }).isEqualTo("align-items: unset")
    }

    @Test
    fun verifyAlignSelf() {
        assertThat(styleToText { alignSelf(AlignSelf.Normal) }).isEqualTo("align-self: normal")
        assertThat(styleToText { alignSelf(AlignSelf.Stretch) }).isEqualTo("align-self: stretch")

        assertThat(styleToText { alignSelf(AlignSelf.Center) }).isEqualTo("align-self: center")
        assertThat(styleToText { alignSelf(AlignSelf.Start) }).isEqualTo("align-self: start")
        assertThat(styleToText { alignSelf(AlignSelf.End) }).isEqualTo("align-self: end")
        assertThat(styleToText { alignSelf(AlignSelf.SelfStart) }).isEqualTo("align-self: self-start")
        assertThat(styleToText { alignSelf(AlignSelf.SelfEnd) }).isEqualTo("align-self: self-end")
        assertThat(styleToText { alignSelf(AlignSelf.FlexStart) }).isEqualTo("align-self: flex-start")
        assertThat(styleToText { alignSelf(AlignSelf.FlexEnd) }).isEqualTo("align-self: flex-end")

        assertThat(styleToText { alignSelf(AlignSelf.Baseline) }).isEqualTo("align-self: baseline")
        assertThat(styleToText { alignSelf(AlignSelf.FirstBaseline) }).isEqualTo("align-self: first baseline")
        assertThat(styleToText { alignSelf(AlignSelf.LastBaseline) }).isEqualTo("align-self: last baseline")

        assertThat(styleToText { alignSelf(AlignSelf.Safe(AlignSelf.Center)) }).isEqualTo("align-self: safe center")
        assertThat(styleToText { alignSelf(AlignSelf.Unsafe(AlignSelf.FlexEnd)) }).isEqualTo("align-self: unsafe flex-end")

        assertThat(styleToText { alignSelf(AlignSelf.Inherit) }).isEqualTo("align-self: inherit")
        assertThat(styleToText { alignSelf(AlignSelf.Initial) }).isEqualTo("align-self: initial")
        assertThat(styleToText { alignSelf(AlignSelf.Revert) }).isEqualTo("align-self: revert")
        assertThat(styleToText { alignSelf(AlignSelf.Unset) }).isEqualTo("align-self: unset")
    }

    @Test
    fun verifyAppearance() {
        assertThat(styleToText { appearance(Appearance.None) }).isEqualTo("appearance: none")
        assertThat(styleToText { appearance(Appearance.Auto) }).isEqualTo("appearance: auto")
        assertThat(styleToText { appearance(Appearance.MenuListButton) }).isEqualTo("appearance: menulist-button")
        assertThat(styleToText { appearance(Appearance.TextField) }).isEqualTo("appearance: textfield")

        assertThat(styleToText { appearance(Appearance.Inherit) }).isEqualTo("appearance: inherit")
        assertThat(styleToText { appearance(Appearance.Initial) }).isEqualTo("appearance: initial")
        assertThat(styleToText { appearance(Appearance.Revert) }).isEqualTo("appearance: revert")
        assertThat(styleToText { appearance(Appearance.Unset) }).isEqualTo("appearance: unset")
    }

    @Test
    fun verifyAspectRatio() {
        assertThat(styleToText { aspectRatio(AspectRatio.Auto) }).isEqualTo("aspect-ratio: auto")

        assertThat(styleToText { aspectRatio(AspectRatio.Inherit) }).isEqualTo("aspect-ratio: inherit")
        assertThat(styleToText { aspectRatio(AspectRatio.Initial) }).isEqualTo("aspect-ratio: initial")
        assertThat(styleToText { aspectRatio(AspectRatio.Revert) }).isEqualTo("aspect-ratio: revert")
        assertThat(styleToText { aspectRatio(AspectRatio.Unset) }).isEqualTo("aspect-ratio: unset")
    }

    @Test
    fun verifyBackdropFilter() {
        assertThat(styleToText { backdropFilter(BackdropFilter.None) }).isEqualTo("backdrop-filter: none; -webkit-backdrop-filter: none")

        assertThat(styleToText {
            backdropFilter(BackdropFilter.of(blur(10.px), brightness(0.5)))
        }).isEqualTo("backdrop-filter: blur(10px) brightness(0.5); -webkit-backdrop-filter: blur(10px) brightness(0.5)")

        assertThat(styleToText { backdropFilter(BackdropFilter.Inherit) }).isEqualTo("backdrop-filter: inherit; -webkit-backdrop-filter: inherit")
        assertThat(styleToText { backdropFilter(BackdropFilter.Initial) }).isEqualTo("backdrop-filter: initial; -webkit-backdrop-filter: initial")
        assertThat(styleToText { backdropFilter(BackdropFilter.Revert) }).isEqualTo("backdrop-filter: revert; -webkit-backdrop-filter: revert")
        assertThat(styleToText { backdropFilter(BackdropFilter.Unset) }).isEqualTo("backdrop-filter: unset; -webkit-backdrop-filter: unset")
    }

    @Test
    fun verifyBackgroundAttachment() {
        assertThat(styleToText { backgroundAttachment(BackgroundAttachment.Scroll) }).isEqualTo("background-attachment: scroll")
        assertThat(styleToText { backgroundAttachment(BackgroundAttachment.Fixed) }).isEqualTo("background-attachment: fixed")
        assertThat(styleToText { backgroundAttachment(BackgroundAttachment.Local) }).isEqualTo("background-attachment: local")

        assertThat(styleToText { backgroundAttachment(BackgroundAttachment.Inherit) }).isEqualTo("background-attachment: inherit")
        assertThat(styleToText { backgroundAttachment(BackgroundAttachment.Initial) }).isEqualTo("background-attachment: initial")
        assertThat(styleToText { backgroundAttachment(BackgroundAttachment.Revert) }).isEqualTo("background-attachment: revert")
        assertThat(styleToText { backgroundAttachment(BackgroundAttachment.Unset) }).isEqualTo("background-attachment: unset")
    }
    
    @Test
    fun verifyBackgroundBlendMode() {
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.Normal) }).isEqualTo("background-blend-mode: normal")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.Multiply) }).isEqualTo("background-blend-mode: multiply")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.Screen) }).isEqualTo("background-blend-mode: screen")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.Overlay) }).isEqualTo("background-blend-mode: overlay")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.Darken) }).isEqualTo("background-blend-mode: darken")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.Lighten) }).isEqualTo("background-blend-mode: lighten")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.ColorDodge) }).isEqualTo("background-blend-mode: color-dodge")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.ColorBurn) }).isEqualTo("background-blend-mode: color-burn")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.HardLight) }).isEqualTo("background-blend-mode: hard-light")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.SoftLight) }).isEqualTo("background-blend-mode: soft-light")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.Difference) }).isEqualTo("background-blend-mode: difference")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.Exclusion) }).isEqualTo("background-blend-mode: exclusion")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.Hue) }).isEqualTo("background-blend-mode: hue")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.Saturation) }).isEqualTo("background-blend-mode: saturation")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.Color) }).isEqualTo("background-blend-mode: color")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.Luminosity) }).isEqualTo("background-blend-mode: luminosity")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.PlusDarker) }).isEqualTo("background-blend-mode: plus-darker")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.PlusLighter) }).isEqualTo("background-blend-mode: plus-lighter")

        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.Inherit) }).isEqualTo("background-blend-mode: inherit")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.Initial) }).isEqualTo("background-blend-mode: initial")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.Revert) }).isEqualTo("background-blend-mode: revert")
        assertThat(styleToText { backgroundBlendMode(BackgroundBlendMode.Unset) }).isEqualTo("background-blend-mode: unset")
    }

    @Test
    fun verifyBackgroundClip() {
        assertThat(styleToText { backgroundClip(BackgroundClip.BorderBox) }).isEqualTo("background-clip: border-box")
        assertThat(styleToText { backgroundClip(BackgroundClip.PaddingBox) }).isEqualTo("background-clip: padding-box")
        assertThat(styleToText { backgroundClip(BackgroundClip.ContentBox) }).isEqualTo("background-clip: content-box")
        assertThat(styleToText { backgroundClip(BackgroundClip.Text) }).isEqualTo("background-clip: text")

        assertThat(styleToText { backgroundClip(BackgroundClip.Inherit) }).isEqualTo("background-clip: inherit")
        assertThat(styleToText { backgroundClip(BackgroundClip.Initial) }).isEqualTo("background-clip: initial")
        assertThat(styleToText { backgroundClip(BackgroundClip.Revert) }).isEqualTo("background-clip: revert")
        assertThat(styleToText { backgroundClip(BackgroundClip.Unset) }).isEqualTo("background-clip: unset")
    }

    @Test
    fun verifyBackgroundColor() {
        assertThat(styleToText { backgroundColor(BackgroundColor.CurrentColor) }).isEqualTo("background-color: currentcolor")
        assertThat(styleToText { backgroundColor(BackgroundColor.Transparent) }).isEqualTo("background-color: transparent")

        assertThat(styleToText { backgroundColor(BackgroundColor.Inherit) }).isEqualTo("background-color: inherit")
        assertThat(styleToText { backgroundColor(BackgroundColor.Initial) }).isEqualTo("background-color: initial")
        assertThat(styleToText { backgroundColor(BackgroundColor.Revert) }).isEqualTo("background-color: revert")
        assertThat(styleToText { backgroundColor(BackgroundColor.Unset) }).isEqualTo("background-color: unset")
    }

    @Test
    fun verifyBackgroundOrigin() {
        assertThat(styleToText { backgroundOrigin(BackgroundOrigin.BorderBox) }).isEqualTo("background-origin: border-box")
        assertThat(styleToText { backgroundOrigin(BackgroundOrigin.PaddingBox) }).isEqualTo("background-origin: padding-box")
        assertThat(styleToText { backgroundOrigin(BackgroundOrigin.ContentBox) }).isEqualTo("background-origin: content-box")

        assertThat(styleToText { backgroundOrigin(BackgroundOrigin.Inherit) }).isEqualTo("background-origin: inherit")
        assertThat(styleToText { backgroundOrigin(BackgroundOrigin.Initial) }).isEqualTo("background-origin: initial")
        assertThat(styleToText { backgroundOrigin(BackgroundOrigin.Revert) }).isEqualTo("background-origin: revert")
        assertThat(styleToText { backgroundOrigin(BackgroundOrigin.Unset) }).isEqualTo("background-origin: unset")
    }

    @Test
    fun verifyBackgroundPosition() {
        assertThat(styleToText { backgroundPosition(BackgroundPosition.of(CSSPosition(10.px, 20.px))) })
            .isEqualTo("background-position: 10px 20px")
        assertThat(styleToText { backgroundPosition(BackgroundPosition.of(CSSPosition.TopLeft)) })
            .isEqualTo("background-position: left 0% top 0%")

        assertThat(styleToText { backgroundPosition(BackgroundPosition.Inherit) })
            .isEqualTo("background-position: inherit")
        assertThat(styleToText { backgroundPosition(BackgroundPosition.Initial) })
            .isEqualTo("background-position: initial")
        assertThat(styleToText { backgroundPosition(BackgroundPosition.Revert) })
            .isEqualTo("background-position: revert")
        assertThat(styleToText { backgroundPosition(BackgroundPosition.Unset) })
            .isEqualTo("background-position: unset")
    }

    @Test
    fun verifyBackgroundRepeat() {
        assertThat(styleToText {
            backgroundRepeat(BackgroundRepeat.of(BackgroundRepeat.Repeat, BackgroundRepeat.NoRepeat))
        }).isEqualTo("background-repeat: repeat no-repeat")

        assertThat(styleToText { backgroundRepeat(BackgroundRepeat.RepeatX) }).isEqualTo("background-repeat: repeat-x")
        assertThat(styleToText { backgroundRepeat(BackgroundRepeat.RepeatY) }).isEqualTo("background-repeat: repeat-y")

        assertThat(styleToText { backgroundRepeat(BackgroundRepeat.Repeat) }).isEqualTo("background-repeat: repeat")
        assertThat(styleToText { backgroundRepeat(BackgroundRepeat.Space) }).isEqualTo("background-repeat: space")
        assertThat(styleToText { backgroundRepeat(BackgroundRepeat.Round) }).isEqualTo("background-repeat: round")
        assertThat(styleToText { backgroundRepeat(BackgroundRepeat.NoRepeat) }).isEqualTo("background-repeat: no-repeat")

        assertThat(styleToText { backgroundRepeat(BackgroundRepeat.Inherit) }).isEqualTo("background-repeat: inherit")
        assertThat(styleToText { backgroundRepeat(BackgroundRepeat.Initial) }).isEqualTo("background-repeat: initial")
        assertThat(styleToText { backgroundRepeat(BackgroundRepeat.Revert) }).isEqualTo("background-repeat: revert")
        assertThat(styleToText { backgroundRepeat(BackgroundRepeat.Unset) }).isEqualTo("background-repeat: unset")
    }

    @Test
    fun verifyBackgroundSize() {
        assertThat(styleToText { backgroundSize(BackgroundSize.of(10.px)) }).isEqualTo("background-size: 10px")
        assertThat(styleToText { backgroundSize(BackgroundSize.of(auto)) }).isEqualTo("background-size: auto")
        assertThat(styleToText {
            backgroundSize(BackgroundSize.of(10.px, 20.px))
        }).isEqualTo("background-size: 10px 20px")
        assertThat(styleToText {
            backgroundSize(BackgroundSize.of(auto, 20.px))
        }).isEqualTo("background-size: auto 20px")
        assertThat(styleToText {
            backgroundSize(BackgroundSize.of(10.px, auto))
        }).isEqualTo("background-size: 10px auto")

        assertThat(styleToText { backgroundSize(BackgroundSize.Cover) }).isEqualTo("background-size: cover")
        assertThat(styleToText { backgroundSize(BackgroundSize.Contain) }).isEqualTo("background-size: contain")

        assertThat(styleToText { backgroundSize(BackgroundSize.Inherit) }).isEqualTo("background-size: inherit")
        assertThat(styleToText { backgroundSize(BackgroundSize.Initial) }).isEqualTo("background-size: initial")
        assertThat(styleToText { backgroundSize(BackgroundSize.Revert) }).isEqualTo("background-size: revert")
        assertThat(styleToText { backgroundSize(BackgroundSize.Unset) }).isEqualTo("background-size: unset")
    }

    @Test
    fun verifyBackground() {
        assertThat(styleToText {
            background(
                Background.of(
                    image = BackgroundImage.of(CSSUrl("test.png")),
                    repeat = BackgroundRepeat.NoRepeat,
                    size = BackgroundSize.of(100.px),
                    position = BackgroundPosition.of(CSSPosition(10.px, 20.px)),
                    blend = BackgroundBlendMode.Multiply,
                    origin = BackgroundOrigin.BorderBox,
                    clip = BackgroundClip.ContentBox,
                    attachment = BackgroundAttachment.Fixed
                )
            )
        }).isEqualTo("background: url(\"test.png\") no-repeat 10px 20px / 100px border-box content-box fixed; background-blend-mode: multiply")

        assertThat(styleToText {
            background(
                Background.of(
                    size = BackgroundSize.of(100.px)
                )
            )
        }).isEqualTo("background: left 0% top 0% / 100px")

        assertThat(styleToText {
            background(
                Color.magenta,
                Background.of(BackgroundImage.of(CSSUrl("test1.png"))),
                Background.of(BackgroundImage.of(CSSUrl("test2.png")))
            )
        }).isEqualTo("background: url(\"test2.png\"), url(\"test1.png\") magenta")

        assertThat(styleToText { background(Background.None) }).isEqualTo("background: none")

        assertThat(styleToText { background(Background.Inherit) }).isEqualTo("background: inherit")
        assertThat(styleToText { background(Background.Initial) }).isEqualTo("background: initial")
        assertThat(styleToText { background(Background.Revert) }).isEqualTo("background: revert")
        assertThat(styleToText { background(Background.Unset) }).isEqualTo("background: unset")
    }

    @Test
    fun verifyBorderCollapse() {
        assertThat(styleToText { borderCollapse(BorderCollapse.Separate) }).isEqualTo("border-collapse: separate")
        assertThat(styleToText { borderCollapse(BorderCollapse.Collapse) }).isEqualTo("border-collapse: collapse")

        assertThat(styleToText { borderCollapse(BorderCollapse.Inherit) }).isEqualTo("border-collapse: inherit")
        assertThat(styleToText { borderCollapse(BorderCollapse.Initial) }).isEqualTo("border-collapse: initial")
        assertThat(styleToText { borderCollapse(BorderCollapse.Revert) }).isEqualTo("border-collapse: revert")
        assertThat(styleToText { borderCollapse(BorderCollapse.Unset) }).isEqualTo("border-collapse: unset")
    }

    @Test
    fun verifyBorderImage() {
        assertThat(styleToText {
            borderImage(
                BorderImage.of(
                    source = BorderImageSource.of(CSSUrl("test.png")),
                    slice = BorderImageSlice.of(50.percent),
                    width = BorderImageWidth.of(10.px),
                    outset = BorderImageOutset.of(5.px),
                    repeat = BorderImageRepeat.Stretch
                )
            )
        }).isEqualTo("border-image: url(\"test.png\") 50% 50% 50% 50% / 10px 10px 10px 10px / 5px 5px 5px 5px stretch")

        assertThat(styleToText { borderImage(BorderImage.Inherit) }).isEqualTo("border-image: inherit")
        assertThat(styleToText { borderImage(BorderImage.Initial) }).isEqualTo("border-image: initial")
        assertThat(styleToText { borderImage(BorderImage.Revert) }).isEqualTo("border-image: revert")
        assertThat(styleToText { borderImage(BorderImage.Unset) }).isEqualTo("border-image: unset")
    }

    @Test
    fun verifyBorderImageOutset() {
        assertThat(styleToText { borderImageOutset(BorderImageOutset.of(50.px)) })
            .isEqualTo("border-image-outset: 50px 50px 50px 50px")

        assertThat(styleToText {
            borderImageOutset(BorderImageOutset.of {
                top(30.px)
                right(40.px)
                bottom(50.px)
                left(60.px)
            })
        }).isEqualTo("border-image-outset: 30px 40px 50px 60px")

        assertThat(styleToText { borderImageOutset(BorderImageOutset.Inherit) }).isEqualTo("border-image-outset: inherit")
        assertThat(styleToText { borderImageOutset(BorderImageOutset.Initial) }).isEqualTo("border-image-outset: initial")
        assertThat(styleToText { borderImageOutset(BorderImageOutset.Revert) }).isEqualTo("border-image-outset: revert")
        assertThat(styleToText { borderImageOutset(BorderImageOutset.Unset) }).isEqualTo("border-image-outset: unset")
    }

    @Test
    fun verifyBorderImageRepeat() {
        assertThat(styleToText { borderImageRepeat(BorderImageRepeat.Stretch) }).isEqualTo("border-image-repeat: stretch")
        assertThat(styleToText { borderImageRepeat(BorderImageRepeat.Repeat) }).isEqualTo("border-image-repeat: repeat")
        assertThat(styleToText { borderImageRepeat(BorderImageRepeat.Round) }).isEqualTo("border-image-repeat: round")
        assertThat(styleToText { borderImageRepeat(BorderImageRepeat.Space) }).isEqualTo("border-image-repeat: space")

        assertThat(styleToText {
            borderImageRepeat(
                BorderImageRepeat.of(
                    BorderImageRepeat.Repeat,
                    BorderImageRepeat.Round
                )
            )
        }).isEqualTo("border-image-repeat: repeat round")

        assertThat(styleToText { borderImageRepeat(BorderImageRepeat.Inherit) }).isEqualTo("border-image-repeat: inherit")
        assertThat(styleToText { borderImageRepeat(BorderImageRepeat.Initial) }).isEqualTo("border-image-repeat: initial")
        assertThat(styleToText { borderImageRepeat(BorderImageRepeat.Revert) }).isEqualTo("border-image-repeat: revert")
        assertThat(styleToText { borderImageRepeat(BorderImageRepeat.Unset) }).isEqualTo("border-image-repeat: unset")
    }

    @Test
    fun verifyBorderImageSource() {
        assertThat(styleToText { borderImageSource(
            BorderImageSource.of(CSSUrl("test.png"))
        ) }).isEqualTo("border-image-source: url(\"test.png\")")

        assertThat(styleToText { borderImageSource(BorderImageSource.None) }).isEqualTo("border-image-source: none")

        assertThat(styleToText { borderImageSource(BorderImageSource.Inherit) }).isEqualTo("border-image-source: inherit")
        assertThat(styleToText { borderImageSource(BorderImageSource.Initial) }).isEqualTo("border-image-source: initial")
        assertThat(styleToText { borderImageSource(BorderImageSource.Revert) }).isEqualTo("border-image-source: revert")
        assertThat(styleToText { borderImageSource(BorderImageSource.Unset) }).isEqualTo("border-image-source: unset")
    }

    @Test
    fun verifyBorderImageSlice() {
        assertThat(styleToText { borderImageSlice(BorderImageSlice.of(50.percent)) })
            .isEqualTo("border-image-slice: 50% 50% 50% 50%")

        assertThat(styleToText {
            borderImageSlice(BorderImageSlice.of {
                top(30.percent)
                right(40.percent)
                bottom(50.percent)
                left(60.percent)
                fill()
            })
        }).isEqualTo("border-image-slice: 30% 40% 50% 60% fill")

        assertThat(styleToText { borderImageSlice(BorderImageSlice.Inherit) }).isEqualTo("border-image-slice: inherit")
        assertThat(styleToText { borderImageSlice(BorderImageSlice.Initial) }).isEqualTo("border-image-slice: initial")
        assertThat(styleToText { borderImageSlice(BorderImageSlice.Revert) }).isEqualTo("border-image-slice: revert")
        assertThat(styleToText { borderImageSlice(BorderImageSlice.Unset) }).isEqualTo("border-image-slice: unset")
    }

    @Test
    fun verifyBorderImageWidth() {
        assertThat(styleToText { borderImageWidth(BorderImageWidth.of(50.percent)) })
            .isEqualTo("border-image-width: 50% 50% 50% 50%")

        assertThat(styleToText {
            borderImageWidth(BorderImageWidth.of {
                top(30.percent)
                right(40.percent)
                bottom(50.percent)
                left(60.percent)
            })
        }).isEqualTo("border-image-width: 30% 40% 50% 60%")

        assertThat(styleToText { borderImageWidth(BorderImageWidth.Inherit) }).isEqualTo("border-image-width: inherit")
        assertThat(styleToText { borderImageWidth(BorderImageWidth.Initial) }).isEqualTo("border-image-width: initial")
        assertThat(styleToText { borderImageWidth(BorderImageWidth.Revert) }).isEqualTo("border-image-width: revert")
        assertThat(styleToText { borderImageWidth(BorderImageWidth.Unset) }).isEqualTo("border-image-width: unset")
    }

    @Test
    fun verifyBoxDecorationBreak() {
        assertThat(styleToText { boxDecorationBreak(BoxDecorationBreak.Slice) }).isEqualTo("box-decoration-break: slice")
        assertThat(styleToText { boxDecorationBreak(BoxDecorationBreak.Clone) }).isEqualTo("box-decoration-break: clone")

        assertThat(styleToText { boxDecorationBreak(BoxDecorationBreak.Inherit) }).isEqualTo("box-decoration-break: inherit")
        assertThat(styleToText { boxDecorationBreak(BoxDecorationBreak.Initial) }).isEqualTo("box-decoration-break: initial")
        assertThat(styleToText { boxDecorationBreak(BoxDecorationBreak.Revert) }).isEqualTo("box-decoration-break: revert")
        assertThat(styleToText { boxDecorationBreak(BoxDecorationBreak.Unset) }).isEqualTo("box-decoration-break: unset")
    }

    @Test
    fun verifyBoxShadow() {
        assertThat(styleToText {
            boxShadow(
                BoxShadow.of(
                    offsetX = 2.px,
                    offsetY = 3.px,
                    blurRadius = 4.px,
                    spreadRadius = 5.px,
                    color = Color.red,
                    inset = true
                )
            )
        }).isEqualTo("box-shadow: inset 2px 3px 4px 5px red")

        assertThat(styleToText {
            boxShadow(
                BoxShadow.of(2.px, 3.px),
                BoxShadow.of(4.px, 5.px, blurRadius = 6.px)
            )
        }).isEqualTo("box-shadow: 2px 3px, 4px 5px 6px")

        assertThat(styleToText { boxShadow(BoxShadow.None) }).isEqualTo("box-shadow: none")

        assertThat(styleToText { boxShadow(BoxShadow.Inherit) }).isEqualTo("box-shadow: inherit")
        assertThat(styleToText { boxShadow(BoxShadow.Initial) }).isEqualTo("box-shadow: initial")
        assertThat(styleToText { boxShadow(BoxShadow.Revert) }).isEqualTo("box-shadow: revert")
        assertThat(styleToText { boxShadow(BoxShadow.Unset) }).isEqualTo("box-shadow: unset")
    }

    @Test
    fun verifyBoxSizing() {
        assertThat(styleToText { boxSizing(BoxSizing.BorderBox) }).isEqualTo("box-sizing: border-box")
        assertThat(styleToText { boxSizing(BoxSizing.ContentBox) }).isEqualTo("box-sizing: content-box")

        assertThat(styleToText { boxSizing(BoxSizing.Inherit) }).isEqualTo("box-sizing: inherit")
        assertThat(styleToText { boxSizing(BoxSizing.Initial) }).isEqualTo("box-sizing: initial")
        assertThat(styleToText { boxSizing(BoxSizing.Revert) }).isEqualTo("box-sizing: revert")
        assertThat(styleToText { boxSizing(BoxSizing.Unset) }).isEqualTo("box-sizing: unset")
    }

    @Test
    fun verifyBreakAfter() {
        assertThat(styleToText { breakAfter(BreakAfter.Auto) }).isEqualTo("break-after: auto")
        assertThat(styleToText { breakAfter(BreakAfter.Avoid) }).isEqualTo("break-after: avoid")
        assertThat(styleToText { breakAfter(BreakAfter.Always) }).isEqualTo("break-after: always")
        assertThat(styleToText { breakAfter(BreakAfter.All) }).isEqualTo("break-after: all")

        assertThat(styleToText { breakAfter(BreakAfter.AvoidPage) }).isEqualTo("break-after: avoid-page")
        assertThat(styleToText { breakAfter(BreakAfter.Page) }).isEqualTo("break-after: page")
        assertThat(styleToText { breakAfter(BreakAfter.Left) }).isEqualTo("break-after: left")
        assertThat(styleToText { breakAfter(BreakAfter.Right) }).isEqualTo("break-after: right")
        assertThat(styleToText { breakAfter(BreakAfter.Recto) }).isEqualTo("break-after: recto")
        assertThat(styleToText { breakAfter(BreakAfter.Verso) }).isEqualTo("break-after: verso")

        assertThat(styleToText { breakAfter(BreakAfter.Column) }).isEqualTo("break-after: column")
        assertThat(styleToText { breakAfter(BreakAfter.AvoidColumn) }).isEqualTo("break-after: avoid-column")

        assertThat(styleToText { breakAfter(BreakAfter.Region) }).isEqualTo("break-after: region")
        assertThat(styleToText { breakAfter(BreakAfter.AvoidRegion) }).isEqualTo("break-after: avoid-region")

        assertThat(styleToText { breakAfter(BreakAfter.Inherit) }).isEqualTo("break-after: inherit")
        assertThat(styleToText { breakAfter(BreakAfter.Initial) }).isEqualTo("break-after: initial")
        assertThat(styleToText { breakAfter(BreakAfter.Revert) }).isEqualTo("break-after: revert")
        assertThat(styleToText { breakAfter(BreakAfter.RevertLayer) }).isEqualTo("break-after: revert-layer")
        assertThat(styleToText { breakAfter(BreakAfter.Unset) }).isEqualTo("break-after: unset")
    }

    @Test
    fun verifyBreakBefore() {
        assertThat(styleToText { breakBefore(BreakBefore.Auto) }).isEqualTo("break-before: auto")
        assertThat(styleToText { breakBefore(BreakBefore.Avoid) }).isEqualTo("break-before: avoid")
        assertThat(styleToText { breakBefore(BreakBefore.Always) }).isEqualTo("break-before: always")
        assertThat(styleToText { breakBefore(BreakBefore.All) }).isEqualTo("break-before: all")

        assertThat(styleToText { breakBefore(BreakBefore.AvoidPage) }).isEqualTo("break-before: avoid-page")
        assertThat(styleToText { breakBefore(BreakBefore.Page) }).isEqualTo("break-before: page")
        assertThat(styleToText { breakBefore(BreakBefore.Left) }).isEqualTo("break-before: left")
        assertThat(styleToText { breakBefore(BreakBefore.Right) }).isEqualTo("break-before: right")
        assertThat(styleToText { breakBefore(BreakBefore.Recto) }).isEqualTo("break-before: recto")
        assertThat(styleToText { breakBefore(BreakBefore.Verso) }).isEqualTo("break-before: verso")

        assertThat(styleToText { breakBefore(BreakBefore.Column) }).isEqualTo("break-before: column")
        assertThat(styleToText { breakBefore(BreakBefore.AvoidColumn) }).isEqualTo("break-before: avoid-column")

        assertThat(styleToText { breakBefore(BreakBefore.Region) }).isEqualTo("break-before: region")
        assertThat(styleToText { breakBefore(BreakBefore.AvoidRegion) }).isEqualTo("break-before: avoid-region")

        assertThat(styleToText { breakBefore(BreakBefore.Inherit) }).isEqualTo("break-before: inherit")
        assertThat(styleToText { breakBefore(BreakBefore.Initial) }).isEqualTo("break-before: initial")
        assertThat(styleToText { breakBefore(BreakBefore.Revert) }).isEqualTo("break-before: revert")
        assertThat(styleToText { breakBefore(BreakBefore.RevertLayer) }).isEqualTo("break-before: revert-layer")
        assertThat(styleToText { breakBefore(BreakBefore.Unset) }).isEqualTo("break-before: unset")
    }

    @Test
    fun verifyBreakInside() {
        assertThat(styleToText { breakInside(BreakInside.Auto) }).isEqualTo("break-inside: auto")
        assertThat(styleToText { breakInside(BreakInside.Avoid) }).isEqualTo("break-inside: avoid")
        assertThat(styleToText { breakInside(BreakInside.AvoidPage) }).isEqualTo("break-inside: avoid-page")
        assertThat(styleToText { breakInside(BreakInside.AvoidColumn) }).isEqualTo("break-inside: avoid-column")
        assertThat(styleToText { breakInside(BreakInside.AvoidRegion) }).isEqualTo("break-inside: avoid-region")

        assertThat(styleToText { breakInside(BreakInside.Inherit) }).isEqualTo("break-inside: inherit")
        assertThat(styleToText { breakInside(BreakInside.Initial) }).isEqualTo("break-inside: initial")
        assertThat(styleToText { breakInside(BreakInside.Revert) }).isEqualTo("break-inside: revert")
        assertThat(styleToText { breakInside(BreakInside.RevertLayer) }).isEqualTo("break-inside: revert-layer")
        assertThat(styleToText { breakInside(BreakInside.Unset) }).isEqualTo("break-inside: unset")
    }

    @Test
    fun verifyCaretColor() {
        assertThat(styleToText { caretColor(CaretColor.Auto) }).isEqualTo("caret-color: auto")
        assertThat(styleToText { caretColor(CaretColor.Transparent) }).isEqualTo("caret-color: transparent")
        assertThat(styleToText { caretColor(CaretColor.CurrentColor) }).isEqualTo("caret-color: currentcolor")

        assertThat(styleToText { caretColor(CaretColor.Inherit) }).isEqualTo("caret-color: inherit")
        assertThat(styleToText { caretColor(CaretColor.Initial) }).isEqualTo("caret-color: initial")
        assertThat(styleToText { caretColor(CaretColor.Revert) }).isEqualTo("caret-color: revert")
        assertThat(styleToText { caretColor(CaretColor.Unset) }).isEqualTo("caret-color: unset")
    }

    @Test
    fun verifyClear() {
        assertThat(styleToText { clear(Clear.None) }).isEqualTo("clear: none")
        assertThat(styleToText { clear(Clear.Left) }).isEqualTo("clear: left")
        assertThat(styleToText { clear(Clear.Right) }).isEqualTo("clear: right")
        assertThat(styleToText { clear(Clear.Both) }).isEqualTo("clear: both")
        assertThat(styleToText { clear(Clear.InlineStart) }).isEqualTo("clear: inline-start")
        assertThat(styleToText { clear(Clear.InlineEnd) }).isEqualTo("clear: inline-end")

        assertThat(styleToText { clear(Clear.Inherit) }).isEqualTo("clear: inherit")
        assertThat(styleToText { clear(Clear.Initial) }).isEqualTo("clear: initial")
        assertThat(styleToText { clear(Clear.Revert) }).isEqualTo("clear: revert")
        assertThat(styleToText { clear(Clear.Unset) }).isEqualTo("clear: unset")
    }
    
    @Test
    fun verifyColorScheme() {
        assertThat(styleToText { colorScheme(ColorScheme.Normal) }).isEqualTo("color-scheme: normal")
        assertThat(styleToText { colorScheme(ColorScheme.Light) }).isEqualTo("color-scheme: light")
        assertThat(styleToText { colorScheme(ColorScheme.Dark) }).isEqualTo("color-scheme: dark")
        assertThat(styleToText { colorScheme(ColorScheme.LightDark) }).isEqualTo("color-scheme: light dark")
        assertThat(styleToText { colorScheme(ColorScheme.DarkLight) }).isEqualTo("color-scheme: dark light")
        assertThat(styleToText { colorScheme(ColorScheme.OnlyLight) }).isEqualTo("color-scheme: only light")
        assertThat(styleToText { colorScheme(ColorScheme.OnlyDark) }).isEqualTo("color-scheme: only dark")

        assertThat(styleToText { colorScheme(ColorScheme.Inherit) }).isEqualTo("color-scheme: inherit")
        assertThat(styleToText { colorScheme(ColorScheme.Initial) }).isEqualTo("color-scheme: initial")
        assertThat(styleToText { colorScheme(ColorScheme.Revert) }).isEqualTo("color-scheme: revert")
        assertThat(styleToText { colorScheme(ColorScheme.Unset) }).isEqualTo("color-scheme: unset")
    }

    @Test
    fun verifyColor() {
        assertThat(styleToText { color(CSSColor.CurrentColor) }).isEqualTo("color: currentColor")

        assertThat(styleToText { color(CSSColor.Inherit) }).isEqualTo("color: inherit")
        assertThat(styleToText { color(CSSColor.Initial) }).isEqualTo("color: initial")
        assertThat(styleToText { color(CSSColor.Revert) }).isEqualTo("color: revert")
        assertThat(styleToText { color(CSSColor.Unset) }).isEqualTo("color: unset")
    }

    @Test
    fun verifyColumnCount() {
        assertThat(styleToText { columnCount(ColumnCount.Auto) }).isEqualTo("column-count: auto")
        assertThat(styleToText { columnCount(ColumnCount.of(3)) }).isEqualTo("column-count: 3")

        assertThat(styleToText { columnCount(ColumnCount.Inherit) }).isEqualTo("column-count: inherit")
        assertThat(styleToText { columnCount(ColumnCount.Initial) }).isEqualTo("column-count: initial")
        assertThat(styleToText { columnCount(ColumnCount.Revert) }).isEqualTo("column-count: revert")
        assertThat(styleToText { columnCount(ColumnCount.RevertLayer) }).isEqualTo("column-count: revert-layer")
        assertThat(styleToText { columnCount(ColumnCount.Unset) }).isEqualTo("column-count: unset")
    }

    @Test
    fun verifyColumnFill() {
        assertThat(styleToText { columnFill(ColumnFill.Auto) }).isEqualTo("column-fill: auto")
        assertThat(styleToText { columnFill(ColumnFill.Balance) }).isEqualTo("column-fill: balance")

        assertThat(styleToText { columnFill(ColumnFill.Inherit) }).isEqualTo("column-fill: inherit")
        assertThat(styleToText { columnFill(ColumnFill.Initial) }).isEqualTo("column-fill: initial")
        assertThat(styleToText { columnFill(ColumnFill.Revert) }).isEqualTo("column-fill: revert")
        assertThat(styleToText { columnFill(ColumnFill.RevertLayer) }).isEqualTo("column-fill: revert-layer")
        assertThat(styleToText { columnFill(ColumnFill.Unset) }).isEqualTo("column-fill: unset")
    }

    @Test
    fun verifyColumnSpan() {
        assertThat(styleToText { columnSpan(ColumnSpan.None) }).isEqualTo("column-span: none")
        assertThat(styleToText { columnSpan(ColumnSpan.All) }).isEqualTo("column-span: all")

        assertThat(styleToText { columnSpan(ColumnSpan.Inherit) }).isEqualTo("column-span: inherit")
        assertThat(styleToText { columnSpan(ColumnSpan.Initial) }).isEqualTo("column-span: initial")
        assertThat(styleToText { columnSpan(ColumnSpan.Revert) }).isEqualTo("column-span: revert")
        assertThat(styleToText { columnSpan(ColumnSpan.RevertLayer) }).isEqualTo("column-span: revert-layer")
        assertThat(styleToText { columnSpan(ColumnSpan.Unset) }).isEqualTo("column-span: unset")
    }

    @Test
    fun verifyContent() {
        // Combinable types
        assertThat(styleToText { content(Content.of("Some text")) }).isEqualTo("content: \"Some text\"")
        assertThat(styleToText { content(Content.of(CSSUrl("test.png"))) }).isEqualTo("content: url(\"test.png\")")
        assertThat(styleToText { content(Content.of(linearGradient(Color.red, Color.green))) }).isEqualTo("content: linear-gradient(red, green)")
        assertThat(styleToText { content(Content.of("Some text"), Content.of(CSSUrl("test.png"))) }).isEqualTo("content: \"Some text\" url(\"test.png\")")
        assertThat(styleToText { content("alt-text", Content.of(CSSUrl("test.png"))) }).isEqualTo("content: url(\"test.png\") / \"alt-text\"")

        // Non-combinable
        assertThat(styleToText { content(Content.None) }).isEqualTo("content: none")
        assertThat(styleToText { content(Content.Normal) }).isEqualTo("content: normal")

        // Language / position-dependent
        assertThat(styleToText { content(Content.CloseQuote) }).isEqualTo("content: close-quote")
        assertThat(styleToText { content(Content.NoCloseQuote) }).isEqualTo("content: no-close-quote")
        assertThat(styleToText { content(Content.NoOpenQuote) }).isEqualTo("content: no-open-quote")
        assertThat(styleToText { content(Content.OpenQuote) }).isEqualTo("content: open-quote")

        // Global keywords
        assertThat(styleToText { content(Content.Inherit) }).isEqualTo("content: inherit")
        assertThat(styleToText { content(Content.Initial) }).isEqualTo("content: initial")
        assertThat(styleToText { content(Content.Revert) }).isEqualTo("content: revert")
        assertThat(styleToText { content(Content.Unset) }).isEqualTo("content: unset")

        // Convenience conversion methods
        assertThat(styleToText { content(CSSUrl("test.png").toContent()) }).isEqualTo("content: url(\"test.png\")")
        assertThat(styleToText { content(linearGradient(Color.red, Color.blue).toContent()) }).isEqualTo("content: linear-gradient(red, blue)")
    }

    @Test
    fun verifyCursor() {
        // General
        assertThat(styleToText { cursor(Cursor.Auto) }).isEqualTo("cursor: auto")
        assertThat(styleToText { cursor(Cursor.Default) }).isEqualTo("cursor: default")
        assertThat(styleToText { cursor(Cursor.None) }).isEqualTo("cursor: none")

        // Links and status
        assertThat(styleToText { cursor(Cursor.ContextMenu) }).isEqualTo("cursor: context-menu")
        assertThat(styleToText { cursor(Cursor.Help) }).isEqualTo("cursor: help")
        assertThat(styleToText { cursor(Cursor.Pointer) }).isEqualTo("cursor: pointer")
        assertThat(styleToText { cursor(Cursor.Progress) }).isEqualTo("cursor: progress")
        assertThat(styleToText { cursor(Cursor.Wait) }).isEqualTo("cursor: wait")

        // Selection
        assertThat(styleToText { cursor(Cursor.Cell) }).isEqualTo("cursor: cell")
        assertThat(styleToText { cursor(Cursor.Crosshair) }).isEqualTo("cursor: crosshair")
        assertThat(styleToText { cursor(Cursor.Text) }).isEqualTo("cursor: text")
        assertThat(styleToText { cursor(Cursor.VerticalText) }).isEqualTo("cursor: vertical-text")

        // Drag and drop
        assertThat(styleToText { cursor(Cursor.Alias) }).isEqualTo("cursor: alias")
        assertThat(styleToText { cursor(Cursor.Copy) }).isEqualTo("cursor: copy")
        assertThat(styleToText { cursor(Cursor.Move) }).isEqualTo("cursor: move")
        assertThat(styleToText { cursor(Cursor.NoDrop) }).isEqualTo("cursor: no-drop")
        assertThat(styleToText { cursor(Cursor.NotAllowed) }).isEqualTo("cursor: not-allowed")
        assertThat(styleToText { cursor(Cursor.Grab) }).isEqualTo("cursor: grab")
        assertThat(styleToText { cursor(Cursor.Grabbing) }).isEqualTo("cursor: grabbing")

        // Resizing and scrolling 
        assertThat(styleToText { cursor(Cursor.AllScroll) }).isEqualTo("cursor: all-scroll")
        assertThat(styleToText { cursor(Cursor.ColumnResize) }).isEqualTo("cursor: col-resize")
        assertThat(styleToText { cursor(Cursor.RowResize) }).isEqualTo("cursor: row-resize")
        assertThat(styleToText { cursor(Cursor.NResize) }).isEqualTo("cursor: n-resize")
        assertThat(styleToText { cursor(Cursor.NeResize) }).isEqualTo("cursor: ne-resize")
        assertThat(styleToText { cursor(Cursor.EResize) }).isEqualTo("cursor: e-resize")
        assertThat(styleToText { cursor(Cursor.SeResize) }).isEqualTo("cursor: se-resize")
        assertThat(styleToText { cursor(Cursor.SResize) }).isEqualTo("cursor: s-resize")
        assertThat(styleToText { cursor(Cursor.SwResize) }).isEqualTo("cursor: sw-resize")
        assertThat(styleToText { cursor(Cursor.WResize) }).isEqualTo("cursor: w-resize")
        assertThat(styleToText { cursor(Cursor.NwResize) }).isEqualTo("cursor: nw-resize")
        assertThat(styleToText { cursor(Cursor.EwResize) }).isEqualTo("cursor: ew-resize")
        assertThat(styleToText { cursor(Cursor.NsResize) }).isEqualTo("cursor: ns-resize")
        assertThat(styleToText { cursor(Cursor.NeswResize) }).isEqualTo("cursor: nesw-resize")
        assertThat(styleToText { cursor(Cursor.NwseResize) }).isEqualTo("cursor: nwse-resize")

        // Zoom
        assertThat(styleToText { cursor(Cursor.ZoomIn) }).isEqualTo("cursor: zoom-in")
        assertThat(styleToText { cursor(Cursor.ZoomOut) }).isEqualTo("cursor: zoom-out")

        // Global
        assertThat(styleToText { cursor(Cursor.Inherit) }).isEqualTo("cursor: inherit")
        assertThat(styleToText { cursor(Cursor.Initial) }).isEqualTo("cursor: initial")
        assertThat(styleToText { cursor(Cursor.Revert) }).isEqualTo("cursor: revert")
        assertThat(styleToText { cursor(Cursor.Unset) }).isEqualTo("cursor: unset")
    }

    @Test
    fun verifyFilter() {
        assertThat(styleToText { filter(Filter.None) }).isEqualTo("filter: none")

        assertThat(styleToText {
            filter(Filter.of(blur(10.px), brightness(0.5)))
        }).isEqualTo("filter: blur(10px) brightness(0.5)")

        assertThat(styleToText { filter(Filter.Inherit) }).isEqualTo("filter: inherit")
        assertThat(styleToText { filter(Filter.Initial) }).isEqualTo("filter: initial")
        assertThat(styleToText { filter(Filter.Revert) }).isEqualTo("filter: revert")
        assertThat(styleToText { filter(Filter.Unset) }).isEqualTo("filter: unset")
    }

    @Test
    fun verifyFlexBasis() {
        assertThat(styleToText { flexBasis(FlexBasis.Auto) }).isEqualTo("flex-basis: auto")

        // Intrinsic sizing
        assertThat(styleToText { flexBasis(FlexBasis.MaxContent) }).isEqualTo("flex-basis: max-content")
        assertThat(styleToText { flexBasis(FlexBasis.MinContent) }).isEqualTo("flex-basis: min-content")
        assertThat(styleToText { flexBasis(FlexBasis.FitContent) }).isEqualTo("flex-basis: fit-content")

        // Content sizing
        assertThat(styleToText { flexBasis(FlexBasis.Content) }).isEqualTo("flex-basis: content")

        // Global
        assertThat(styleToText { flexBasis(FlexBasis.Inherit) }).isEqualTo("flex-basis: inherit")
        assertThat(styleToText { flexBasis(FlexBasis.Initial) }).isEqualTo("flex-basis: initial")
        assertThat(styleToText { flexBasis(FlexBasis.Revert) }).isEqualTo("flex-basis: revert")
        assertThat(styleToText { flexBasis(FlexBasis.Unset) }).isEqualTo("flex-basis: unset")
    }

    @Test
    fun verifyFontOpticalSizing() {
        assertThat(styleToText { fontOpticalSizing(FontOpticalSizing.Auto) }).isEqualTo("font-optical-sizing: auto")
        assertThat(styleToText { fontOpticalSizing(FontOpticalSizing.None) }).isEqualTo("font-optical-sizing: none")

        assertThat(styleToText { fontOpticalSizing(FontOpticalSizing.Inherit) }).isEqualTo("font-optical-sizing: inherit")
        assertThat(styleToText { fontOpticalSizing(FontOpticalSizing.Initial) }).isEqualTo("font-optical-sizing: initial")
        assertThat(styleToText { fontOpticalSizing(FontOpticalSizing.Revert) }).isEqualTo("font-optical-sizing: revert")
        assertThat(styleToText { fontOpticalSizing(FontOpticalSizing.Unset) }).isEqualTo("font-optical-sizing: unset")
    }

    @Test
    fun verifyFontStyle() {
        assertThat(styleToText { fontStyle(FontStyle.Normal) }).isEqualTo("font-style: normal")
        assertThat(styleToText { fontStyle(FontStyle.Italic) }).isEqualTo("font-style: italic")
        assertThat(styleToText { fontStyle(FontStyle.Oblique) }).isEqualTo("font-style: oblique")
        assertThat(styleToText { fontStyle(FontStyle.Oblique(45.deg)) }).isEqualTo("font-style: oblique 45deg")

        assertThat(styleToText { fontStyle(FontStyle.Inherit) }).isEqualTo("font-style: inherit")
        assertThat(styleToText { fontStyle(FontStyle.Initial) }).isEqualTo("font-style: initial")
        assertThat(styleToText { fontStyle(FontStyle.Revert) }).isEqualTo("font-style: revert")
        assertThat(styleToText { fontStyle(FontStyle.Unset) }).isEqualTo("font-style: unset")
    }

    @Test
    fun verifyFontVariantAlternates() {
        assertThat(styleToText { fontVariantAlternates(FontVariantAlternates.Normal) }).isEqualTo("font-variant-alternates: normal")
        assertThat(styleToText { fontVariantAlternates(FontVariantAlternates.HistoricalForms) }).isEqualTo("font-variant-alternates: historical-forms")

        assertThat(styleToText { fontVariantAlternates(FontVariantAlternates.Stylistic("ident")) }).isEqualTo("font-variant-alternates: stylistic(ident)")
        assertThat(styleToText { fontVariantAlternates(FontVariantAlternates.Styleset("ident")) }).isEqualTo("font-variant-alternates: styleset(ident)")
        assertThat(styleToText { fontVariantAlternates(FontVariantAlternates.CharacterVariant("ident")) }).isEqualTo("font-variant-alternates: character-variant(ident)")
        assertThat(styleToText { fontVariantAlternates(FontVariantAlternates.Swash("ident")) }).isEqualTo("font-variant-alternates: swash(ident)")
        assertThat(styleToText { fontVariantAlternates(FontVariantAlternates.Ornaments("ident")) }).isEqualTo("font-variant-alternates: ornaments(ident)")
        assertThat(styleToText { fontVariantAlternates(FontVariantAlternates.Annotation("ident")) }).isEqualTo("font-variant-alternates: annotation(ident)")

        assertThat(styleToText {
            fontVariantAlternates(
                FontVariantAlternates.of(
                    FontVariantAlternates.Stylistic("ident1"),
                    FontVariantAlternates.Swash("ident2")
                )
            )
        }).isEqualTo("font-variant-alternates: stylistic(ident1) swash(ident2)")

        assertThat(styleToText { fontVariantAlternates(FontVariantAlternates.Inherit) }).isEqualTo("font-variant-alternates: inherit")
        assertThat(styleToText { fontVariantAlternates(FontVariantAlternates.Initial) }).isEqualTo("font-variant-alternates: initial")
        assertThat(styleToText { fontVariantAlternates(FontVariantAlternates.Revert) }).isEqualTo("font-variant-alternates: revert")
        assertThat(styleToText { fontVariantAlternates(FontVariantAlternates.Unset) }).isEqualTo("font-variant-alternates: unset")
    }

    @Test
    fun verifyFontVariantCaps() {
        assertThat(styleToText { fontVariantCaps(FontVariantCaps.Normal) }).isEqualTo("font-variant-caps: normal")
        assertThat(styleToText { fontVariantCaps(FontVariantCaps.SmallCaps) }).isEqualTo("font-variant-caps: small-caps")
        assertThat(styleToText { fontVariantCaps(FontVariantCaps.AllSmallCaps) }).isEqualTo("font-variant-caps: all-small-caps")
        assertThat(styleToText { fontVariantCaps(FontVariantCaps.PetiteCaps) }).isEqualTo("font-variant-caps: petite-caps")
        assertThat(styleToText { fontVariantCaps(FontVariantCaps.AllPetiteCaps) }).isEqualTo("font-variant-caps: all-petite-caps")
        assertThat(styleToText { fontVariantCaps(FontVariantCaps.Unicase) }).isEqualTo("font-variant-caps: unicase")
        assertThat(styleToText { fontVariantCaps(FontVariantCaps.TitlingCaps) }).isEqualTo("font-variant-caps: titling-caps")

        assertThat(styleToText { fontVariantCaps(FontVariantCaps.Inherit) }).isEqualTo("font-variant-caps: inherit")
        assertThat(styleToText { fontVariantCaps(FontVariantCaps.Initial) }).isEqualTo("font-variant-caps: initial")
        assertThat(styleToText { fontVariantCaps(FontVariantCaps.Revert) }).isEqualTo("font-variant-caps: revert")
        assertThat(styleToText { fontVariantCaps(FontVariantCaps.Unset) }).isEqualTo("font-variant-caps: unset")
    }

    @Test
    fun verifyFontVariantEastAsian() {
        assertThat(styleToText { fontVariantEastAsian(FontVariantEastAsian.Normal) }).isEqualTo("font-variant-east-asian: normal")
        assertThat(styleToText { fontVariantEastAsian(FontVariantEastAsian.Ruby) }).isEqualTo("font-variant-east-asian: ruby")

        // East Asian variants
        assertThat(styleToText { fontVariantEastAsian(FontVariantEastAsian.Jis78) }).isEqualTo("font-variant-east-asian: jis78")
        assertThat(styleToText { fontVariantEastAsian(FontVariantEastAsian.Jis83) }).isEqualTo("font-variant-east-asian: jis83")
        assertThat(styleToText { fontVariantEastAsian(FontVariantEastAsian.Jis90) }).isEqualTo("font-variant-east-asian: jis90")
        assertThat(styleToText { fontVariantEastAsian(FontVariantEastAsian.Jis04) }).isEqualTo("font-variant-east-asian: jis04")
        assertThat(styleToText { fontVariantEastAsian(FontVariantEastAsian.Simplified) }).isEqualTo("font-variant-east-asian: simplified")
        assertThat(styleToText { fontVariantEastAsian(FontVariantEastAsian.Traditional) }).isEqualTo("font-variant-east-asian: traditional")

        // East Asian widths
        assertThat(styleToText { fontVariantEastAsian(FontVariantEastAsian.FullWidth) }).isEqualTo("font-variant-east-asian: full-width")
        assertThat(styleToText { fontVariantEastAsian(FontVariantEastAsian.ProportionalWidth) }).isEqualTo("font-variant-east-asian: proportional-width")

        assertThat(styleToText {
            fontVariantEastAsian(FontVariantEastAsian.of(FontVariantEastAsian.Ruby, FontVariantEastAsian.Jis78))
        }).isEqualTo("font-variant-east-asian: ruby jis78")

        assertThat(styleToText { fontVariantEastAsian(FontVariantEastAsian.Inherit) }).isEqualTo("font-variant-east-asian: inherit")
        assertThat(styleToText { fontVariantEastAsian(FontVariantEastAsian.Initial) }).isEqualTo("font-variant-east-asian: initial")
        assertThat(styleToText { fontVariantEastAsian(FontVariantEastAsian.Revert) }).isEqualTo("font-variant-east-asian: revert")
        assertThat(styleToText { fontVariantEastAsian(FontVariantEastAsian.Unset) }).isEqualTo("font-variant-east-asian: unset")
    }

    @Test
    fun verifyFontVariantEmoji() {
        assertThat(styleToText { fontVariantEmoji(FontVariantEmoji.Normal) }).isEqualTo("font-variant-emoji: normal")
        assertThat(styleToText { fontVariantEmoji(FontVariantEmoji.Text) }).isEqualTo("font-variant-emoji: text")
        assertThat(styleToText { fontVariantEmoji(FontVariantEmoji.Emoji) }).isEqualTo("font-variant-emoji: emoji")
        assertThat(styleToText { fontVariantEmoji(FontVariantEmoji.Unicode) }).isEqualTo("font-variant-emoji: unicode")

        assertThat(styleToText { fontVariantEmoji(FontVariantEmoji.Inherit) }).isEqualTo("font-variant-emoji: inherit")
        assertThat(styleToText { fontVariantEmoji(FontVariantEmoji.Initial) }).isEqualTo("font-variant-emoji: initial")
        assertThat(styleToText { fontVariantEmoji(FontVariantEmoji.Revert) }).isEqualTo("font-variant-emoji: revert")
        assertThat(styleToText { fontVariantEmoji(FontVariantEmoji.Unset) }).isEqualTo("font-variant-emoji: unset")
    }

    @Test
    fun verifyFontVariantLigatures() {
        assertThat(styleToText { fontVariantLigatures(FontVariantLigatures.Normal) }).isEqualTo("font-variant-ligatures: normal")
        assertThat(styleToText { fontVariantLigatures(FontVariantLigatures.None) }).isEqualTo("font-variant-ligatures: none")

        // Common ligature values
        assertThat(styleToText { fontVariantLigatures(FontVariantLigatures.CommonLigatures) }).isEqualTo("font-variant-ligatures: common-ligatures")
        assertThat(styleToText { fontVariantLigatures(FontVariantLigatures.NoCommonLigatures) }).isEqualTo("font-variant-ligatures: no-common-ligatures")

        // Discretionary ligature values  
        assertThat(styleToText { fontVariantLigatures(FontVariantLigatures.DiscretionaryLigatures) }).isEqualTo("font-variant-ligatures: discretionary-ligatures")
        assertThat(styleToText { fontVariantLigatures(FontVariantLigatures.NoDiscretionaryLigatures) }).isEqualTo("font-variant-ligatures: no-discretionary-ligatures")

        // Historical ligature values
        assertThat(styleToText { fontVariantLigatures(FontVariantLigatures.HistoricalLigatures) }).isEqualTo("font-variant-ligatures: historical-ligatures")
        assertThat(styleToText { fontVariantLigatures(FontVariantLigatures.NoHistoricalLigatures) }).isEqualTo("font-variant-ligatures: no-historical-ligatures")

        // Contextual ligature values
        assertThat(styleToText { fontVariantLigatures(FontVariantLigatures.Contextual) }).isEqualTo("font-variant-ligatures: contextual")
        assertThat(styleToText { fontVariantLigatures(FontVariantLigatures.NoContextual) }).isEqualTo("font-variant-ligatures: no-contextual")

        assertThat(styleToText {
            fontVariantLigatures(
                FontVariantLigatures.of(
                    FontVariantLigatures.CommonLigatures,
                    FontVariantLigatures.HistoricalLigatures
                )
            )
        }).isEqualTo("font-variant-ligatures: common-ligatures historical-ligatures")

        assertThat(styleToText {
            fontVariantLigatures(
                FontVariantLigatures.of(
                    common = true,
                    discretionary = false,
                    historical = true,
                    contextual = false
                )
            )
        }).isEqualTo("font-variant-ligatures: common-ligatures no-discretionary-ligatures historical-ligatures no-contextual")

        // Global values
        assertThat(styleToText { fontVariantLigatures(FontVariantLigatures.Inherit) }).isEqualTo("font-variant-ligatures: inherit")
        assertThat(styleToText { fontVariantLigatures(FontVariantLigatures.Initial) }).isEqualTo("font-variant-ligatures: initial")
        assertThat(styleToText { fontVariantLigatures(FontVariantLigatures.Revert) }).isEqualTo("font-variant-ligatures: revert")
        assertThat(styleToText { fontVariantLigatures(FontVariantLigatures.Unset) }).isEqualTo("font-variant-ligatures: unset")
    }

    @Test
    fun verifyFontVariantNumeric() {
        assertThat(styleToText { fontVariantNumeric(FontVariantNumeric.Normal) }).isEqualTo("font-variant-numeric: normal")
        assertThat(styleToText { fontVariantNumeric(FontVariantNumeric.Ordinal) }).isEqualTo("font-variant-numeric: ordinal")
        assertThat(styleToText { fontVariantNumeric(FontVariantNumeric.SlashedZero) }).isEqualTo("font-variant-numeric: slashed-zero")

        // Numeric figure
        assertThat(styleToText { fontVariantNumeric(FontVariantNumeric.LiningNums) }).isEqualTo("font-variant-numeric: lining-nums")
        assertThat(styleToText { fontVariantNumeric(FontVariantNumeric.OldstyleNums) }).isEqualTo("font-variant-numeric: oldstyle-nums")

        // Numeric spacing
        assertThat(styleToText { fontVariantNumeric(FontVariantNumeric.ProportionalNums) }).isEqualTo("font-variant-numeric: proportional-nums")
        assertThat(styleToText { fontVariantNumeric(FontVariantNumeric.TabularNums) }).isEqualTo("font-variant-numeric: tabular-nums")

        // Numeric fractions 
        assertThat(styleToText { fontVariantNumeric(FontVariantNumeric.DiagonalFractions) }).isEqualTo("font-variant-numeric: diagonal-fractions")
        assertThat(styleToText { fontVariantNumeric(FontVariantNumeric.StackedFractions) }).isEqualTo("font-variant-numeric: stacked-fractions")

        assertThat(styleToText {
            fontVariantNumeric(
                FontVariantNumeric.of(
                    FontVariantNumeric.Ordinal,
                    FontVariantNumeric.SlashedZero
                )
            )
        }).isEqualTo("font-variant-numeric: ordinal slashed-zero")

        assertThat(styleToText { fontVariantNumeric(FontVariantNumeric.Inherit) }).isEqualTo("font-variant-numeric: inherit")
        assertThat(styleToText { fontVariantNumeric(FontVariantNumeric.Initial) }).isEqualTo("font-variant-numeric: initial")
        assertThat(styleToText { fontVariantNumeric(FontVariantNumeric.Revert) }).isEqualTo("font-variant-numeric: revert")
        assertThat(styleToText { fontVariantNumeric(FontVariantNumeric.Unset) }).isEqualTo("font-variant-numeric: unset")
    }

    @Test
    fun verifyFontVariantPosition() {
        assertThat(styleToText { fontVariantPosition(FontVariantPosition.Normal) }).isEqualTo("font-variant-position: normal")
        assertThat(styleToText { fontVariantPosition(FontVariantPosition.Sub) }).isEqualTo("font-variant-position: sub")
        assertThat(styleToText { fontVariantPosition(FontVariantPosition.Super) }).isEqualTo("font-variant-position: super")

        assertThat(styleToText { fontVariantPosition(FontVariantPosition.Inherit) }).isEqualTo("font-variant-position: inherit")
        assertThat(styleToText { fontVariantPosition(FontVariantPosition.Initial) }).isEqualTo("font-variant-position: initial")
        assertThat(styleToText { fontVariantPosition(FontVariantPosition.Revert) }).isEqualTo("font-variant-position: revert")
        assertThat(styleToText { fontVariantPosition(FontVariantPosition.Unset) }).isEqualTo("font-variant-position: unset")
    }

    @Test
    fun verifyFontVariant() {
        // Individual values
        assertThat(styleToText { fontVariant(alternates = FontVariantAlternates.Normal) })
            .isEqualTo("font-variant: normal")
        assertThat(styleToText { fontVariant(caps = FontVariantCaps.SmallCaps) })
            .isEqualTo("font-variant: small-caps")
        assertThat(styleToText { fontVariant(eastAsian = FontVariantEastAsian.Ruby) })
            .isEqualTo("font-variant: ruby")
        assertThat(styleToText { fontVariant(emoji = FontVariantEmoji.Text) })
            .isEqualTo("font-variant: text")
        assertThat(styleToText { fontVariant(ligatures = FontVariantLigatures.CommonLigatures) })
            .isEqualTo("font-variant: common-ligatures")
        assertThat(styleToText { fontVariant(numeric = FontVariantNumeric.Ordinal) })
            .isEqualTo("font-variant: ordinal")
        assertThat(styleToText { fontVariant(position = FontVariantPosition.Super) })
            .isEqualTo("font-variant: super")

        // Multiple values
        assertThat(styleToText {
            fontVariant(
                alternates = FontVariantAlternates.Normal,
                caps = FontVariantCaps.SmallCaps,
                eastAsian = FontVariantEastAsian.Ruby,
                emoji = FontVariantEmoji.Text,
                ligatures = FontVariantLigatures.CommonLigatures,
                numeric = FontVariantNumeric.Ordinal,
                position = FontVariantPosition.Super
            )
        }).isEqualTo("font-variant: normal small-caps ruby text common-ligatures ordinal super")
    }

    @Test
    fun verifyFontVariationSettings() {
         assertThat(styleToText { fontVariationSettings(FontVariationSettings.Normal) })
             .isEqualTo("font-variation-settings: normal")

         assertThat(styleToText { fontVariationSettings(FontVariationSettings.Axis("wght", 400)) })
             .isEqualTo("font-variation-settings: \"wght\" 400")

         assertThat(styleToText { fontVariationSettings(
             FontVariationSettings.Axes(
                 FontVariationSettings.Axis("wght", 400),
                 FontVariationSettings.Axis("slnt", 0)
             )
         ) }).isEqualTo("font-variation-settings: \"wght\" 400,\"slnt\" 0")

         assertThat(styleToText { fontVariationSettings(FontVariationSettings.Inherit) })
             .isEqualTo("font-variation-settings: inherit")
         assertThat(styleToText { fontVariationSettings(FontVariationSettings.Initial) })
             .isEqualTo("font-variation-settings: initial")
         assertThat(styleToText { fontVariationSettings(FontVariationSettings.Revert) })
             .isEqualTo("font-variation-settings: revert")
         assertThat(styleToText { fontVariationSettings(FontVariationSettings.Unset) })
             .isEqualTo("font-variation-settings: unset")
    }

    @Test
    fun verifyFontWeight() {
        // Common value constants
        assertThat(styleToText { fontWeight(FontWeight.Thin) }).isEqualTo("font-weight: 100")
        assertThat(styleToText { fontWeight(FontWeight.ExtraLight) }).isEqualTo("font-weight: 200")
        assertThat(styleToText { fontWeight(FontWeight.Light) }).isEqualTo("font-weight: 300")
        assertThat(styleToText { fontWeight(FontWeight.Medium) }).isEqualTo("font-weight: 500")
        assertThat(styleToText { fontWeight(FontWeight.SemiBold) }).isEqualTo("font-weight: 600")
        assertThat(styleToText { fontWeight(FontWeight.ExtraBold) }).isEqualTo("font-weight: 800")
        assertThat(styleToText { fontWeight(FontWeight.Black) }).isEqualTo("font-weight: 900")
        assertThat(styleToText { fontWeight(FontWeight.ExtraBlack) }).isEqualTo("font-weight: 950")

        // Keyword
        assertThat(styleToText { fontWeight(FontWeight.Normal) }).isEqualTo("font-weight: normal")
        assertThat(styleToText { fontWeight(FontWeight.Bold) }).isEqualTo("font-weight: bold")

        // Relative
        assertThat(styleToText { fontWeight(FontWeight.Lighter) }).isEqualTo("font-weight: lighter")
        assertThat(styleToText { fontWeight(FontWeight.Bolder) }).isEqualTo("font-weight: bolder")

        // Global
        assertThat(styleToText { fontWeight(FontWeight.Inherit) }).isEqualTo("font-weight: inherit")
        assertThat(styleToText { fontWeight(FontWeight.Initial) }).isEqualTo("font-weight: initial")
        assertThat(styleToText { fontWeight(FontWeight.Revert) }).isEqualTo("font-weight: revert")
        assertThat(styleToText { fontWeight(FontWeight.Unset) }).isEqualTo("font-weight: unset")
    }

    @Test
    fun verifyFontSize() {
        // Absolute keywords
        assertThat(styleToText { fontSize(FontSize.XXSmall) }).isEqualTo("font-size: xx-small")
        assertThat(styleToText { fontSize(FontSize.XSmall) }).isEqualTo("font-size: x-small")
        assertThat(styleToText { fontSize(FontSize.Small) }).isEqualTo("font-size: small")
        assertThat(styleToText { fontSize(FontSize.Medium) }).isEqualTo("font-size: medium")
        assertThat(styleToText { fontSize(FontSize.Large) }).isEqualTo("font-size: large")
        assertThat(styleToText { fontSize(FontSize.XLarge) }).isEqualTo("font-size: x-large")
        assertThat(styleToText { fontSize(FontSize.XXLarge) }).isEqualTo("font-size: xx-large")

        // Relative keywords
        assertThat(styleToText { fontSize(FontSize.Smaller) }).isEqualTo("font-size: smaller")
        assertThat(styleToText { fontSize(FontSize.Larger) }).isEqualTo("font-size: larger")

        // Global values
        assertThat(styleToText { fontSize(FontSize.Inherit) }).isEqualTo("font-size: inherit")
        assertThat(styleToText { fontSize(FontSize.Initial) }).isEqualTo("font-size: initial")
        assertThat(styleToText { fontSize(FontSize.Revert) }).isEqualTo("font-size: revert")
        assertThat(styleToText { fontSize(FontSize.Unset) }).isEqualTo("font-size: unset")
    }

    @Test
    fun verifyJustifyContent() {
        assertThat(styleToText { justifyContent(JustifyContent.Normal) }).isEqualTo("justify-content: normal")
        assertThat(styleToText { justifyContent(JustifyContent.Stretch) }).isEqualTo("justify-content: stretch")

        assertThat(styleToText { justifyContent(JustifyContent.Center) }).isEqualTo("justify-content: center")
        assertThat(styleToText { justifyContent(JustifyContent.Start) }).isEqualTo("justify-content: start")
        assertThat(styleToText { justifyContent(JustifyContent.End) }).isEqualTo("justify-content: end")
        assertThat(styleToText { justifyContent(JustifyContent.FlexStart) }).isEqualTo("justify-content: flex-start")
        assertThat(styleToText { justifyContent(JustifyContent.FlexEnd) }).isEqualTo("justify-content: flex-end")
        assertThat(styleToText { justifyContent(JustifyContent.Left) }).isEqualTo("justify-content: left")
        assertThat(styleToText { justifyContent(JustifyContent.Right) }).isEqualTo("justify-content: right")

        assertThat(styleToText { justifyContent(JustifyContent.SpaceBetween) }).isEqualTo("justify-content: space-between")
        assertThat(styleToText { justifyContent(JustifyContent.SpaceAround) }).isEqualTo("justify-content: space-around")
        assertThat(styleToText { justifyContent(JustifyContent.SpaceEvenly) }).isEqualTo("justify-content: space-evenly")
        assertThat(styleToText { justifyContent(JustifyContent.Stretch) }).isEqualTo("justify-content: stretch")

        assertThat(styleToText { justifyContent(JustifyContent.Safe(JustifyContent.Center)) }).isEqualTo("justify-content: safe center")
        assertThat(styleToText { justifyContent(JustifyContent.Unsafe(JustifyContent.FlexEnd)) }).isEqualTo("justify-content: unsafe flex-end")

        assertThat(styleToText { justifyContent(JustifyContent.Inherit) }).isEqualTo("justify-content: inherit")
        assertThat(styleToText { justifyContent(JustifyContent.Initial) }).isEqualTo("justify-content: initial")
        assertThat(styleToText { justifyContent(JustifyContent.Revert) }).isEqualTo("justify-content: revert")
        assertThat(styleToText { justifyContent(JustifyContent.Unset) }).isEqualTo("justify-content: unset")
    }

    @Test
    fun verifyJustifyItems() {
        assertThat(styleToText { justifyItems(JustifyItems.Normal) }).isEqualTo("justify-items: normal")
        assertThat(styleToText { justifyItems(JustifyItems.Stretch) }).isEqualTo("justify-items: stretch")

        assertThat(styleToText { justifyItems(JustifyItems.Center) }).isEqualTo("justify-items: center")
        assertThat(styleToText { justifyItems(JustifyItems.Start) }).isEqualTo("justify-items: start")
        assertThat(styleToText { justifyItems(JustifyItems.End) }).isEqualTo("justify-items: end")
        assertThat(styleToText { justifyItems(JustifyItems.FlexStart) }).isEqualTo("justify-items: flex-start")
        assertThat(styleToText { justifyItems(JustifyItems.FlexEnd) }).isEqualTo("justify-items: flex-end")
        assertThat(styleToText { justifyItems(JustifyItems.Left) }).isEqualTo("justify-items: left")
        assertThat(styleToText { justifyItems(JustifyItems.Right) }).isEqualTo("justify-items: right")

        assertThat(styleToText { justifyItems(JustifyItems.Baseline) }).isEqualTo("justify-items: baseline")
        assertThat(styleToText { justifyItems(JustifyItems.FirstBaseline) }).isEqualTo("justify-items: first baseline")
        assertThat(styleToText { justifyItems(JustifyItems.LastBaseline) }).isEqualTo("justify-items: last baseline")

        assertThat(styleToText { justifyItems(JustifyItems.Safe(JustifyItems.Center)) }).isEqualTo("justify-items: safe center")
        assertThat(styleToText { justifyItems(JustifyItems.Unsafe(JustifyItems.FlexEnd)) }).isEqualTo("justify-items: unsafe flex-end")

        assertThat(styleToText { justifyItems(JustifyItems.Inherit) }).isEqualTo("justify-items: inherit")
        assertThat(styleToText { justifyItems(JustifyItems.Initial) }).isEqualTo("justify-items: initial")
        assertThat(styleToText { justifyItems(JustifyItems.Revert) }).isEqualTo("justify-items: revert")
        assertThat(styleToText { justifyItems(JustifyItems.Unset) }).isEqualTo("justify-items: unset")
    }

    @Test
    fun verifyJustifySelf() {
        assertThat(styleToText { justifySelf(JustifySelf.Auto) }).isEqualTo("justify-self: auto")
        assertThat(styleToText { justifySelf(JustifySelf.Normal) }).isEqualTo("justify-self: normal")
        assertThat(styleToText { justifySelf(JustifySelf.Stretch) }).isEqualTo("justify-self: stretch")

        assertThat(styleToText { justifySelf(JustifySelf.Center) }).isEqualTo("justify-self: center")
        assertThat(styleToText { justifySelf(JustifySelf.Start) }).isEqualTo("justify-self: start")
        assertThat(styleToText { justifySelf(JustifySelf.End) }).isEqualTo("justify-self: end")
        assertThat(styleToText { justifySelf(JustifySelf.SelfStart) }).isEqualTo("justify-self: self-start")
        assertThat(styleToText { justifySelf(JustifySelf.SelfEnd) }).isEqualTo("justify-self: self-end")
        assertThat(styleToText { justifySelf(JustifySelf.FlexStart) }).isEqualTo("justify-self: flex-start")
        assertThat(styleToText { justifySelf(JustifySelf.FlexEnd) }).isEqualTo("justify-self: flex-end")
        assertThat(styleToText { justifySelf(JustifySelf.Left) }).isEqualTo("justify-self: left")
        assertThat(styleToText { justifySelf(JustifySelf.Right) }).isEqualTo("justify-self: right")

        assertThat(styleToText { justifySelf(JustifySelf.Baseline) }).isEqualTo("justify-self: baseline")
        assertThat(styleToText { justifySelf(JustifySelf.FirstBaseline) }).isEqualTo("justify-self: first baseline")
        assertThat(styleToText { justifySelf(JustifySelf.LastBaseline) }).isEqualTo("justify-self: last baseline")

        assertThat(styleToText { justifySelf(JustifySelf.Safe(JustifySelf.Center)) }).isEqualTo("justify-self: safe center")
        assertThat(styleToText { justifySelf(JustifySelf.Unsafe(JustifySelf.FlexEnd)) }).isEqualTo("justify-self: unsafe flex-end")

        assertThat(styleToText { justifySelf(JustifySelf.Inherit) }).isEqualTo("justify-self: inherit")
        assertThat(styleToText { justifySelf(JustifySelf.Initial) }).isEqualTo("justify-self: initial")
        assertThat(styleToText { justifySelf(JustifySelf.Revert) }).isEqualTo("justify-self: revert")
        assertThat(styleToText { justifySelf(JustifySelf.Unset) }).isEqualTo("justify-self: unset")
    }

    @Test
    fun verifyLineHeight() {
        assertThat(styleToText { lineHeight(LineHeight.Normal) }).isEqualTo("line-height: normal")

        assertThat(styleToText { lineHeight(LineHeight.Inherit) }).isEqualTo("line-height: inherit")
        assertThat(styleToText { lineHeight(LineHeight.Initial) }).isEqualTo("line-height: initial")
        assertThat(styleToText { lineHeight(LineHeight.Revert) }).isEqualTo("line-height: revert")
        assertThat(styleToText { lineHeight(LineHeight.Unset) }).isEqualTo("line-height: unset")
    }

    @Test
    fun verifyListStyle() {
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.ArabicIndic)) }).isEqualTo("list-style: arabic-indic")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Armenian)) }).isEqualTo("list-style: armenian")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Bengali)) }).isEqualTo("list-style: bengali")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Cambodian)) }).isEqualTo("list-style: cambodian")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Circle)) }).isEqualTo("list-style: circle")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.CjkDecimal)) }).isEqualTo("list-style: cjk-decimal")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.CjkEarthlyBranch)) }).isEqualTo("list-style: cjk-earthly-branch")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.CjkHeavenlyStem)) }).isEqualTo("list-style: cjk-heavenly-stem")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.CjkIdeographic)) }).isEqualTo("list-style: cjk-ideographic")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Decimal)) }).isEqualTo("list-style: decimal")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.DecimalLeadingZero)) }).isEqualTo("list-style: decimal-leading-zero")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Devanagari)) }).isEqualTo("list-style: devanagari")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Disc)) }).isEqualTo("list-style: disc")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.DisclosureClosed)) }).isEqualTo("list-style: disclosure-closed")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.DisclosureOpen)) }).isEqualTo("list-style: disclosure-open")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.EthiopicNumeric)) }).isEqualTo("list-style: ethiopic-numeric")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Georgian)) }).isEqualTo("list-style: georgian")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Gujarati)) }).isEqualTo("list-style: gujarati")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Gurmukhi)) }).isEqualTo("list-style: gurmukhi")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Hebrew)) }).isEqualTo("list-style: hebrew")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Hiragana)) }).isEqualTo("list-style: hiragana")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.HiraganaIroha)) }).isEqualTo("list-style: hiragana-iroha")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.JapaneseFormal)) }).isEqualTo("list-style: japanese-formal")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.JapaneseInformal)) }).isEqualTo("list-style: japanese-informal")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Kannada)) }).isEqualTo("list-style: kannada")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Katakana)) }).isEqualTo("list-style: katakana")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.KatakanaIroha)) }).isEqualTo("list-style: katakana-iroha")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Khmer)) }).isEqualTo("list-style: khmer")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.KoreanHangulFormal)) }).isEqualTo("list-style: korean-hangul-formal")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.KoreanHanjaFormal)) }).isEqualTo("list-style: korean-hanja-formal")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.KoreanHanjaInformal)) }).isEqualTo("list-style: korean-hanja-informal")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Lao)) }).isEqualTo("list-style: lao")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.LowerAlpha)) }).isEqualTo("list-style: lower-alpha")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.LowerArmenian)) }).isEqualTo("list-style: lower-armenian")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.LowerGreek)) }).isEqualTo("list-style: lower-greek")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.LowerLatin)) }).isEqualTo("list-style: lower-latin")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.LowerRoman)) }).isEqualTo("list-style: lower-roman")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Malayalam)) }).isEqualTo("list-style: malayalam")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Mongolian)) }).isEqualTo("list-style: mongolian")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Myanmar)) }).isEqualTo("list-style: myanmar")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Oriya)) }).isEqualTo("list-style: oriya")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Persian)) }).isEqualTo("list-style: persian")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.SimpChineseFormal)) }).isEqualTo("list-style: simp-chinese-formal")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.SimpChineseInformal)) }).isEqualTo("list-style: simp-chinese-informal")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Square)) }).isEqualTo("list-style: square")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Tamil)) }).isEqualTo("list-style: tamil")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Telugu)) }).isEqualTo("list-style: telugu")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Thai)) }).isEqualTo("list-style: thai")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Tibetan)) }).isEqualTo("list-style: tibetan")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.TradChineseFormal)) }).isEqualTo("list-style: trad-chinese-formal")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.TradChineseInformal)) }).isEqualTo("list-style: trad-chinese-informal")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.UpperAlpha)) }).isEqualTo("list-style: upper-alpha")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.UpperArmenian)) }).isEqualTo("list-style: upper-armenian")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.UpperLatin)) }).isEqualTo("list-style: upper-latin")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.UpperRoman)) }).isEqualTo("list-style: upper-roman")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.None)) }).isEqualTo("list-style: none")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Inherit)) }).isEqualTo("list-style: inherit")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Initial)) }).isEqualTo("list-style: initial")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Revert)) }).isEqualTo("list-style: revert")
        assertThat(styleToText { listStyle(ListStyle.of(type = ListStyleType.Unset)) }).isEqualTo("list-style: unset")

        assertThat(styleToText { listStyle(ListStyle.of(position = ListStylePosition.Inside)) }).isEqualTo("list-style: inside")
        assertThat(styleToText { listStyle(ListStyle.of(image = ListStyleImage.of(CSSUrl("test.png")))) })
            .isEqualTo("list-style: url(\"test.png\")")

        assertThat(styleToText {
            listStyle(ListStyle.of(ListStyleType.Circle, ListStylePosition.Outside))
        }).isEqualTo("list-style: circle outside")
        assertThat(styleToText {
            listStyle(ListStyle.of(ListStyleType.Square, ListStylePosition.Inside, ListStyleImage.of(CSSUrl("test.png"))))
        }).isEqualTo("list-style: square inside url(\"test.png\")")

        assertThat(styleToText { listStyle(ListStyle.None) }).isEqualTo("list-style: none")

        assertThat(styleToText { listStyle(ListStyle.Inherit) }).isEqualTo("list-style: inherit")
        assertThat(styleToText { listStyle(ListStyle.Initial) }).isEqualTo("list-style: initial")
        assertThat(styleToText { listStyle(ListStyle.Revert) }).isEqualTo("list-style: revert")
        assertThat(styleToText { listStyle(ListStyle.Unset) }).isEqualTo("list-style: unset")
    }

    @Test
    fun verifyMixBlendMode() {
        assertThat(styleToText { mixBlendMode(MixBlendMode.Normal) }).isEqualTo("mix-blend-mode: normal")
        assertThat(styleToText { mixBlendMode(MixBlendMode.Multiply) }).isEqualTo("mix-blend-mode: multiply")
        assertThat(styleToText { mixBlendMode(MixBlendMode.Screen) }).isEqualTo("mix-blend-mode: screen")
        assertThat(styleToText { mixBlendMode(MixBlendMode.Overlay) }).isEqualTo("mix-blend-mode: overlay")
        assertThat(styleToText { mixBlendMode(MixBlendMode.Darken) }).isEqualTo("mix-blend-mode: darken")
        assertThat(styleToText { mixBlendMode(MixBlendMode.Lighten) }).isEqualTo("mix-blend-mode: lighten")
        assertThat(styleToText { mixBlendMode(MixBlendMode.ColorDodge) }).isEqualTo("mix-blend-mode: color-dodge")
        assertThat(styleToText { mixBlendMode(MixBlendMode.ColorBurn) }).isEqualTo("mix-blend-mode: color-burn")
        assertThat(styleToText { mixBlendMode(MixBlendMode.HardLight) }).isEqualTo("mix-blend-mode: hard-light")
        assertThat(styleToText { mixBlendMode(MixBlendMode.SoftLight) }).isEqualTo("mix-blend-mode: soft-light")
        assertThat(styleToText { mixBlendMode(MixBlendMode.Difference) }).isEqualTo("mix-blend-mode: difference")
        assertThat(styleToText { mixBlendMode(MixBlendMode.Exclusion) }).isEqualTo("mix-blend-mode: exclusion")
        assertThat(styleToText { mixBlendMode(MixBlendMode.Hue) }).isEqualTo("mix-blend-mode: hue")
        assertThat(styleToText { mixBlendMode(MixBlendMode.Saturation) }).isEqualTo("mix-blend-mode: saturation")
        assertThat(styleToText { mixBlendMode(MixBlendMode.Color) }).isEqualTo("mix-blend-mode: color")
        assertThat(styleToText { mixBlendMode(MixBlendMode.Luminosity) }).isEqualTo("mix-blend-mode: luminosity")
        assertThat(styleToText { mixBlendMode(MixBlendMode.PlusDarker) }).isEqualTo("mix-blend-mode: plus-darker")
        assertThat(styleToText { mixBlendMode(MixBlendMode.PlusLighter) }).isEqualTo("mix-blend-mode: plus-lighter")

        assertThat(styleToText { mixBlendMode(MixBlendMode.Inherit) }).isEqualTo("mix-blend-mode: inherit")
        assertThat(styleToText { mixBlendMode(MixBlendMode.Initial) }).isEqualTo("mix-blend-mode: initial")
        assertThat(styleToText { mixBlendMode(MixBlendMode.Revert) }).isEqualTo("mix-blend-mode: revert")
        assertThat(styleToText { mixBlendMode(MixBlendMode.Unset) }).isEqualTo("mix-blend-mode: unset")
    }

    @Test
    fun verifyObjectFit() {
        assertThat(styleToText { objectFit(ObjectFit.Contain) }).isEqualTo("object-fit: contain")
        assertThat(styleToText { objectFit(ObjectFit.Cover) }).isEqualTo("object-fit: cover")
        assertThat(styleToText { objectFit(ObjectFit.Fill) }).isEqualTo("object-fit: fill")
        assertThat(styleToText { objectFit(ObjectFit.None) }).isEqualTo("object-fit: none")
        assertThat(styleToText { objectFit(ObjectFit.ScaleDown) }).isEqualTo("object-fit: scale-down")

        assertThat(styleToText { objectFit(ObjectFit.Inherit) }).isEqualTo("object-fit: inherit")
        assertThat(styleToText { objectFit(ObjectFit.Initial) }).isEqualTo("object-fit: initial")
        assertThat(styleToText { objectFit(ObjectFit.Revert) }).isEqualTo("object-fit: revert")
        assertThat(styleToText { objectFit(ObjectFit.Unset) }).isEqualTo("object-fit: unset")
    }

    @Test
    fun verifyResize() {
        assertThat(styleToText { resize(Resize.None) }).isEqualTo("resize: none")
        assertThat(styleToText { resize(Resize.Both) }).isEqualTo("resize: both")
        assertThat(styleToText { resize(Resize.Horizontal) }).isEqualTo("resize: horizontal")
        assertThat(styleToText { resize(Resize.Vertical) }).isEqualTo("resize: vertical")
        assertThat(styleToText { resize(Resize.Block) }).isEqualTo("resize: block")
        assertThat(styleToText { resize(Resize.Inline) }).isEqualTo("resize: inline")

        assertThat(styleToText { resize(Resize.Inherit) }).isEqualTo("resize: inherit")
        assertThat(styleToText { resize(Resize.Initial) }).isEqualTo("resize: initial")
        assertThat(styleToText { resize(Resize.Revert) }).isEqualTo("resize: revert")
        assertThat(styleToText { resize(Resize.Unset) }).isEqualTo("resize: unset")
    }
    
    @Test
    fun verifyTouchAction() {
        assertThat(styleToText { touchAction(TouchAction.Auto) }).isEqualTo("touch-action: auto")
        assertThat(styleToText { touchAction(TouchAction.None) }).isEqualTo("touch-action: none")
        assertThat(styleToText { touchAction(TouchAction.PanX) }).isEqualTo("touch-action: pan-x")
        assertThat(styleToText { touchAction(TouchAction.PanY) }).isEqualTo("touch-action: pan-y")
        assertThat(styleToText { touchAction(TouchAction.PinchZoom) }).isEqualTo("touch-action: pinch-zoom")
        assertThat(styleToText { touchAction(TouchAction.Manipulation) }).isEqualTo("touch-action: manipulation")

        assertThat(styleToText {
            touchAction(TouchAction.of(TouchAction.PanX, TouchAction.PanY))
        }).isEqualTo("touch-action: pan-x pan-y")

        assertThat(styleToText {
            touchAction(TouchAction.of(TouchAction.PanX, TouchAction.PanY, withPinchZoom = true))
        }).isEqualTo("touch-action: pan-x pan-y pinch-zoom")

        assertThat(styleToText {
            touchAction(TouchAction.of(TouchAction.PanX, withPinchZoom = true))
        }).isEqualTo("touch-action: pan-x pinch-zoom")

        assertThat(styleToText {
            touchAction(TouchAction.of(TouchAction.PanY, withPinchZoom = true))
        }).isEqualTo("touch-action: pan-y pinch-zoom")

        assertThat(styleToText { touchAction(TouchAction.Inherit) }).isEqualTo("touch-action: inherit")
        assertThat(styleToText { touchAction(TouchAction.Initial) }).isEqualTo("touch-action: initial")
        assertThat(styleToText { touchAction(TouchAction.Revert) }).isEqualTo("touch-action: revert")
        assertThat(styleToText { touchAction(TouchAction.Unset) }).isEqualTo("touch-action: unset")
    }

    @Test
    fun verifyVerticalAlign() {
        assertThat(styleToText { verticalAlign(VerticalAlign.Baseline) }).isEqualTo("vertical-align: baseline")
        assertThat(styleToText { verticalAlign(VerticalAlign.Sub) }).isEqualTo("vertical-align: sub")
        assertThat(styleToText { verticalAlign(VerticalAlign.Super) }).isEqualTo("vertical-align: super")
        assertThat(styleToText { verticalAlign(VerticalAlign.TextTop) }).isEqualTo("vertical-align: text-top")
        assertThat(styleToText { verticalAlign(VerticalAlign.TextBottom) }).isEqualTo("vertical-align: text-bottom")
        assertThat(styleToText { verticalAlign(VerticalAlign.Middle) }).isEqualTo("vertical-align: middle")
        assertThat(styleToText { verticalAlign(VerticalAlign.Top) }).isEqualTo("vertical-align: top")
        assertThat(styleToText { verticalAlign(VerticalAlign.Bottom) }).isEqualTo("vertical-align: bottom")

        assertThat(styleToText { verticalAlign(VerticalAlign.Inherit) }).isEqualTo("vertical-align: inherit")
        assertThat(styleToText { verticalAlign(VerticalAlign.Initial) }).isEqualTo("vertical-align: initial")
        assertThat(styleToText { verticalAlign(VerticalAlign.Revert) }).isEqualTo("vertical-align: revert")
        assertThat(styleToText { verticalAlign(VerticalAlign.Unset) }).isEqualTo("vertical-align: unset")
    }

    @Test
    fun verifyAll() {
        assertThat(styleToText { all(All.Inherit) }).isEqualTo("all: inherit")
        assertThat(styleToText { all(All.Initial) }).isEqualTo("all: initial")
        assertThat(styleToText { all(All.Revert) }).isEqualTo("all: revert")
        assertThat(styleToText { all(All.RevertLayer) }).isEqualTo("all: revert-layer")
        assertThat(styleToText { all(All.Unset) }).isEqualTo("all: unset")
    }

    @Test
    fun verifyRubyPosition() {
        assertThat(styleToText { rubyPosition(RubyPosition.Over) }).isEqualTo("ruby-position: over")
        assertThat(styleToText { rubyPosition(RubyPosition.Under) }).isEqualTo("ruby-position: under")

        assertThat(styleToText { rubyPosition(RubyPosition.Inherit) }).isEqualTo("ruby-position: inherit")
        assertThat(styleToText { rubyPosition(RubyPosition.Initial) }).isEqualTo("ruby-position: initial")
        assertThat(styleToText { rubyPosition(RubyPosition.Revert) }).isEqualTo("ruby-position: revert")
        assertThat(styleToText { rubyPosition(RubyPosition.RevertLayer) }).isEqualTo("ruby-position: revert-layer")
        assertThat(styleToText { rubyPosition(RubyPosition.Unset) }).isEqualTo("ruby-position: unset")
    }

    @Test
    fun verifyWordSpacing() {
        assertThat(styleToText { wordSpacing(WordSpacing.Normal) }).isEqualTo("word-spacing: normal")
        assertThat(styleToText { wordSpacing(WordSpacing.of(12.px)) }).isEqualTo("word-spacing: 12px")

        assertThat(styleToText { wordSpacing(WordSpacing.Inherit) }).isEqualTo("word-spacing: inherit")
        assertThat(styleToText { wordSpacing(WordSpacing.Initial) }).isEqualTo("word-spacing: initial")
        assertThat(styleToText { wordSpacing(WordSpacing.Revert) }).isEqualTo("word-spacing: revert")
        assertThat(styleToText { wordSpacing(WordSpacing.RevertLayer) }).isEqualTo("word-spacing: revert-layer")
        assertThat(styleToText { wordSpacing(WordSpacing.Unset) }).isEqualTo("word-spacing: unset")
    }

    @Test
    fun verifyBackfaceVisibility() {
        assertThat(styleToText { backFaceVisibility(BackfaceVisibility.Visible) }).isEqualTo("backface-visibility: visible")
        assertThat(styleToText { backFaceVisibility(BackfaceVisibility.Hidden) }).isEqualTo("backface-visibility: hidden")

        assertThat(styleToText { backFaceVisibility(BackfaceVisibility.Inherit) }).isEqualTo("backface-visibility: inherit")
        assertThat(styleToText { backFaceVisibility(BackfaceVisibility.Initial) }).isEqualTo("backface-visibility: initial")
        assertThat(styleToText { backFaceVisibility(BackfaceVisibility.Revert) }).isEqualTo("backface-visibility: revert")
        assertThat(styleToText { backFaceVisibility(BackfaceVisibility.RevertLayer) }).isEqualTo("backface-visibility: revert-layer")
        assertThat(styleToText { backFaceVisibility(BackfaceVisibility.Unset) }).isEqualTo("backface-visibility: unset")
    }

    @Test
    fun verifyWidows() {
        assertThat(styleToText { widows(Widows.of(1)) }).isEqualTo("widows: 1")

        assertThat(styleToText { widows(Widows.Inherit) }).isEqualTo("widows: inherit")
        assertThat(styleToText { widows(Widows.Initial) }).isEqualTo("widows: initial")
        assertThat(styleToText { widows(Widows.Revert) }).isEqualTo("widows: revert")
        assertThat(styleToText { widows(Widows.RevertLayer) }).isEqualTo("widows: revert-layer")
        assertThat(styleToText { widows(Widows.Unset) }).isEqualTo("widows: unset")
    }

    @Test
    fun verifyWillChange() {
        assertThat(styleToText { willChange(WillChange.Auto) }).isEqualTo("will-change: auto")
        assertThat(styleToText { willChange(WillChange.ScrollPosition) }).isEqualTo("will-change: scroll-position")
        assertThat(styleToText { willChange(WillChange.Contents) }).isEqualTo("will-change: contents")

        assertThat(styleToText { willChange(WillChange.of("left","top")) }).isEqualTo("will-change: left, top")

        assertThat(styleToText { willChange(WillChange.Inherit) }).isEqualTo("will-change: inherit")
        assertThat(styleToText { willChange(WillChange.Initial) }).isEqualTo("will-change: initial")
        assertThat(styleToText { willChange(WillChange.Initial) }).isEqualTo("will-change: initial")
        assertThat(styleToText { willChange(WillChange.Revert) }).isEqualTo("will-change: revert")
        assertThat(styleToText { willChange(WillChange.RevertLayer) }).isEqualTo("will-change: revert-layer")
        assertThat(styleToText { willChange(WillChange.Unset) }).isEqualTo("will-change: unset")
    }
}
