package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.truthish.assertThat
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword
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
                Background.of(image = BackgroundImage.of(CSSUrl("test1.png"))),
                Background.of(image = BackgroundImage.of(CSSUrl("test2.png")))
            )
        }).isEqualTo("background: url(\"test2.png\"), url(\"test1.png\") magenta")

        assertThat(styleToText { background(Background.None) }).isEqualTo("background: none")

        assertThat(styleToText { background(Background.Inherit) }).isEqualTo("background: inherit")
        assertThat(styleToText { background(Background.Initial) }).isEqualTo("background: initial")
        assertThat(styleToText { background(Background.Revert) }).isEqualTo("background: revert")
        assertThat(styleToText { background(Background.Unset) }).isEqualTo("background: unset")
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