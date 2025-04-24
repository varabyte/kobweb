package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.compose.css.functions.brightness
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
}