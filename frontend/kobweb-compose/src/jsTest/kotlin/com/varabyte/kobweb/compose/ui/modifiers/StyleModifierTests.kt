package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toStyles
import com.varabyte.truthish.assertThat
import org.jetbrains.compose.web.css.*
import kotlin.test.Test

class StyleModifierTests {
    // Convert all properties added by all modifier styles to the String that would ultimately get put into an HTML
    // style attribute. In other words, key / values will be split by a ':' and multiple properties by a ';'
    private fun modifierToText(produceModifier: () -> Modifier): String {
        // We don't care about comparing -- but it's an easy way to construct a style scope, as Compose HTML doesn't
        // give us an easy way otherwise.
        val styleScope = ComparableStyleScope()
        val modifier = produceModifier()
        modifier.toStyles().invoke(styleScope)

        return styleScope.properties.entries.joinToString("; ") { (key, value) -> "$key: $value" }
    }

    @Test
    fun verifyMarginInline() {
        assertThat(modifierToText { Modifier.marginInline(both = 10.px) }).isEqualTo("margin-inline: 10px")
        assertThat(modifierToText {
            Modifier.marginInline(start = 10.px, end = 20.px)
        }).isEqualTo("margin-inline: 10px 20px")
        assertThat(modifierToText { Modifier.marginInline(end = 20.px) }).isEqualTo("margin-inline: 0px 20px")

        assertThat(modifierToText {
            Modifier.marginInline {
                start(10.px)
                end(20.px)
            }
        }).isEqualTo("margin-inline-start: 10px; margin-inline-end: 20px")

        assertThat(modifierToText {
            Modifier.marginInline {
                end(20.px)
            }
        }).isEqualTo("margin-inline-end: 20px")
    }

    @Test
    fun verifyOutline() {
        assertThat(modifierToText {
            Modifier.outline(
                Outline.of(
                    OutlineWidth.of(2.px),
                    LineStyle.Dotted,
                    Color.green
                )
            )
        }).isEqualTo("outline: 2px dotted green")

        assertThat(modifierToText {
            Modifier.outline(3.px, LineStyle.Solid, Color.magenta)
        }).isEqualTo("outline: 3px solid magenta")

        assertThat(modifierToText {
            Modifier.outline(Outline.Inherit)
        }).isEqualTo("outline: inherit")

        assertThat(modifierToText {
            Modifier.outline {
                color(Color.red)
                style(LineStyle.Dotted)
                width(2.px)
            }
        }).isEqualTo("outline-color: red; outline-style: dotted; outline-width: 2px")

        assertThat(modifierToText {
            Modifier.outline {
                color(Color.blue)
                width(OutlineWidth.Medium)
            }
        }).isEqualTo("outline-color: blue; outline-width: medium")
    }

    @Test
    fun verifyPerformance() {
        assertThat(modifierToText {
            Modifier.willChange(WillChange.ScrollPosition)
        }).isEqualTo("will-change: scroll-position")

        assertThat(modifierToText {
            Modifier.willChange("top", "left")
        }).isEqualTo("will-change: top, left")
    }

    @Test
    fun verifyReset() {
        assertThat(modifierToText {
            Modifier.all(All.Revert)
        }).isEqualTo("all: revert")
    }

    @Test
    fun verifyScroll() {
        assertThat(modifierToText {
            Modifier.overscrollBehavior(OverscrollBehavior.Contain)
        }).isEqualTo("overscroll-behavior: contain")

        assertThat(modifierToText {
            Modifier.overscrollBehavior(OverscrollBehavior.Auto, OverscrollBehavior.None)
        }).isEqualTo("overscroll-behavior: auto none")

        assertThat(modifierToText {
            Modifier.overscrollBehavior {
                x(OverscrollBehavior.None)
                y(OverscrollBehavior.Contain)
            }
        }).isEqualTo("overscroll-behavior-x: none; overscroll-behavior-y: contain")

        assertThat(modifierToText {
            Modifier.overscrollBehaviorBlock(OverscrollBehaviorBlock.Contain)
        }).isEqualTo("overscroll-behavior-block: contain")

        assertThat(modifierToText {
            Modifier.overscrollBehaviorInline(OverscrollBehaviorInline.None)
        }).isEqualTo("overscroll-behavior-inline: none")

        assertThat(modifierToText {
            Modifier.scrollBehavior(ScrollBehavior.Smooth)
        }).isEqualTo("scroll-behavior: smooth")

        assertThat(modifierToText {
            Modifier.scrollSnapType(ScrollSnapType.X)
        }).isEqualTo("scroll-snap-type: x")

        assertThat(modifierToText {
            Modifier.scrollSnapType(ScrollSnapType.Y, ScrollSnapType.Strictness.Mandatory)
        }).isEqualTo("scroll-snap-type: y mandatory")

        assertThat(modifierToText {
            Modifier.scrollPadding(10.px)
        }).isEqualTo("scroll-padding: 10px")

        assertThat(modifierToText {
            Modifier.scrollPadding(10.px, 20.px)
        }).isEqualTo("scroll-padding: 10px 20px")

        assertThat(modifierToText {
            Modifier.scrollPadding(10.px, 20.px, 30.px)
        }).isEqualTo("scroll-padding: 10px 20px 30px")

        assertThat(modifierToText {
            Modifier.scrollPadding(10.px, 20.px, 30.px, 40.px)
        }).isEqualTo("scroll-padding: 10px 20px 30px 40px")

        assertThat(modifierToText {
            Modifier.scrollPaddingInline(10.px)
        }).isEqualTo("scroll-padding-inline: 10px")

        assertThat(modifierToText {
            Modifier.scrollPaddingInline(10.px, 20.px)
        }).isEqualTo("scroll-padding-inline: 10px 20px")

        assertThat(modifierToText {
            Modifier.scrollPaddingBlock(10.px)
        }).isEqualTo("scroll-padding-block: 10px")

        assertThat(modifierToText {
            Modifier.scrollPaddingBlock(10.px, 20.px)
        }).isEqualTo("scroll-padding-block: 10px 20px")

        assertThat(modifierToText {
            Modifier.scrollSnapAlign(ScrollSnapAlign.Center)
        }).isEqualTo("scroll-snap-align: center")

        assertThat(modifierToText {
            Modifier.scrollSnapAlign(ScrollSnapAlign.Start, ScrollSnapAlign.End)
        }).isEqualTo("scroll-snap-align: start end")

        assertThat(modifierToText {
            Modifier.scrollSnapStop(ScrollSnapStop.Always)
        }).isEqualTo("scroll-snap-stop: always")

        assertThat(modifierToText {
            Modifier.scrollMargin(10.px)
        }).isEqualTo("scroll-margin: 10px")

        assertThat(modifierToText {
            Modifier.scrollMargin(10.px, 20.px)
        }).isEqualTo("scroll-margin: 10px 20px")

        assertThat(modifierToText {
            Modifier.scrollMargin(10.px, 20.px, 30.px)
        }).isEqualTo("scroll-margin: 10px 20px 30px")

        assertThat(modifierToText {
            Modifier.scrollMargin(10.px, 20.px, 30.px, 40.px)
        }).isEqualTo("scroll-margin: 10px 20px 30px 40px")

        assertThat(modifierToText {
            Modifier.scrollMarginInline(10.px)
        }).isEqualTo("scroll-margin-inline: 10px")

        assertThat(modifierToText {
            Modifier.scrollMarginInline(10.px, 20.px)
        }).isEqualTo("scroll-margin-inline: 10px 20px")

        assertThat(modifierToText {
            Modifier.scrollMarginBlock(10.px)
        }).isEqualTo("scroll-margin-block: 10px")

        assertThat(modifierToText {
            Modifier.scrollMarginBlock(10.px, 20.px)
        }).isEqualTo("scroll-margin-block: 10px 20px")

        assertThat(modifierToText {
            Modifier.scrollbarWidth(ScrollbarWidth.Thin)
        }).isEqualTo("scrollbar-width: thin")
    }

    @Test
    fun verifyText() {
        assertThat(modifierToText {
            Modifier.rubyPosition(RubyPosition.Under)
        }).isEqualTo("ruby-position: under")

        assertThat(modifierToText {
            Modifier.textAlign(TextAlign.Center)
        }).isEqualTo("text-align: center")

        assertThat(modifierToText {
            Modifier.textDecorationLine(TextDecorationLine.Underline)
        }).isEqualTo("text-decoration-line: underline")

        assertThat(modifierToText {
            Modifier.textOverflow(TextOverflow.Ellipsis)
        }).isEqualTo("text-overflow: ellipsis")

        assertThat(modifierToText {
            Modifier.textShadow(2.px, 2.px, 2.px, Color.gray)
        }).isEqualTo("text-shadow: 2px 2px 2px gray")

        assertThat(modifierToText {
            Modifier.textShadow(TextShadow.of(2.px, 2.px), TextShadow.of(4.px, 4.px, 2.px, Color.red))
        }).isEqualTo("text-shadow: 2px 2px, 4px 4px 2px red")

        assertThat(modifierToText {
            Modifier.textShadow(TextShadow.Initial)
        }).isEqualTo("text-shadow: initial")

        assertThat(modifierToText {
            Modifier.textTransform(TextTransform.Capitalize)
        }).isEqualTo("text-transform: capitalize")

        assertThat(modifierToText {
            Modifier.whiteSpace(WhiteSpace.NoWrap)
        }).isEqualTo("white-space: nowrap")

        assertThat(modifierToText {
            Modifier.wordBreak(WordBreak.BreakAll)
        }).isEqualTo("word-break: break-all")

        assertThat(modifierToText {
            Modifier.wordSpacing(WordSpacing.Normal)
        }).isEqualTo("word-spacing: normal")

        assertThat(modifierToText {
            Modifier.wordSpacing(20.px)
        }).isEqualTo("word-spacing: 20px")

        assertThat(modifierToText {
            Modifier.writingMode(WritingMode.VerticalRl)
        }).isEqualTo("writing-mode: vertical-rl")
    }
    
    @Test
    fun verifyTransform() {
        assertThat(modifierToText {
            Modifier.backfaceVisibility(BackfaceVisibility.Hidden)
        }).isEqualTo("backface-visibility: hidden")

        assertThat(modifierToText {
            Modifier.transform {
                translateX(10.px)
                scale(2.0)
            }
        }).isEqualTo("transform: translateX(10px) scale(2)")

        assertThat(modifierToText {
            Modifier.rotate(45.deg)
        }).isEqualTo("rotate: 45deg")

        assertThat(modifierToText {
            Modifier.rotate(1, 0, 0, 45.deg)
        }).isEqualTo("rotate: 1 0 0 45deg")

        assertThat(modifierToText {
            Modifier.rotateX(90.deg)
        }).isEqualTo("rotate: x 90deg")

        assertThat(modifierToText {
            Modifier.rotateY(180.deg)
        }).isEqualTo("rotate: y 180deg")

        assertThat(modifierToText {
            Modifier.rotateZ(270.deg)
        }).isEqualTo("rotate: z 270deg")

        assertThat(modifierToText {
            Modifier.scale(2)
        }).isEqualTo("scale: 2")

        assertThat(modifierToText {
            Modifier.scale(2, 3)
        }).isEqualTo("scale: 2 3")

        assertThat(modifierToText {
            Modifier.scale(2, 3, 4)
        }).isEqualTo("scale: 2 3 4")

        assertThat(modifierToText {
            Modifier.scaleX(2)
        }).isEqualTo("scale: 2 1")

        assertThat(modifierToText {
            Modifier.scaleY(2)
        }).isEqualTo("scale: 1 2")

        assertThat(modifierToText {
            Modifier.scaleZ(2)
        }).isEqualTo("scale: 1 1 2")

        assertThat(modifierToText {
            Modifier.scale(50.percent)
        }).isEqualTo("scale: 50%")

        assertThat(modifierToText {
            Modifier.scale(50.percent, 75.percent)
        }).isEqualTo("scale: 50% 75%")

        assertThat(modifierToText {
            Modifier.scale(50.percent, 75.percent, 100.percent)
        }).isEqualTo("scale: 50% 75% 100%")

        assertThat(modifierToText {
            Modifier.scaleX(50.percent)
        }).isEqualTo("scale: 50% 100%")

        assertThat(modifierToText {
            Modifier.scaleY(60.percent)
        }).isEqualTo("scale: 100% 60%")

        assertThat(modifierToText {
            Modifier.scaleZ(70.percent)
        }).isEqualTo("scale: 100% 100% 70%")

        assertThat(modifierToText {
            Modifier.transformBox(TransformBox.ContentBox)
        }).isEqualTo("transform-box: content-box")

        assertThat(modifierToText {
            Modifier.transformOrigin(TransformOrigin.Center)
        }).isEqualTo("transform-origin: center center")

        assertThat(modifierToText {
            Modifier.transformStyle(TransformStyle.Preserve3d)
        }).isEqualTo("transform-style: preserve-3d")

        assertThat(modifierToText {
            Modifier.translate(10.px)
        }).isEqualTo("translate: 10px")

        assertThat(modifierToText {
            Modifier.translate(10.px, 20.px)
        }).isEqualTo("translate: 10px 20px")

        assertThat(modifierToText {
            Modifier.translate(10.px, 20.px, 30.px)
        }).isEqualTo("translate: 10px 20px 30px")

        assertThat(modifierToText {
            Modifier.translateX(10.px)
        }).isEqualTo("translate: 10px")

        assertThat(modifierToText {
            Modifier.translateY(20.px)
        }).isEqualTo("translate: 0% 20px")

        assertThat(modifierToText {
            Modifier.translateZ(30.px)
        }).isEqualTo("translate: 0% 0% 30px")
    }

    @Test
    fun verifyTransition() {
        assertThat(modifierToText {
            Modifier.transition(Transition.of(TransitionProperty.All, 200.ms))
        }).isEqualTo("transition: all 200ms")

        assertThat(modifierToText {
            Modifier.transition(
                Transition.of("color", 2.s, TransitionTimingFunction.EaseIn),
                Transition.of("transform", 500.ms)
            )
        }).isEqualTo("transition: color 2s ease-in, transform 500ms")

        assertThat(modifierToText {
            val transitions = listOf(
                Transition.of("color", 2.s, TransitionTimingFunction.EaseIn),
                Transition.of("transform", 500.ms)
            )
            Modifier.transition(transitions)
        }).isEqualTo("transition: color 2s ease-in, transform 500ms")

        assertThat(modifierToText {
            Modifier.transition(
                Transition.group(listOf("width", "height"), 2.s)
            )
        }).isEqualTo("transition: width 2s, height 2s")

        assertThat(modifierToText {
            Modifier.transition {
                property("color")
                duration(300.ms)
                timingFunction(TransitionTimingFunction.Linear)
                delay(100.ms)
                behavior(TransitionBehavior.AllowDiscrete)
            }
        }).isEqualTo("transition-property: color; transition-duration: 300ms; transition-timing-function: linear; transition-delay: 100ms; transition-behavior: allow-discrete")

        assertThat(modifierToText {
            Modifier.transition {
                property("margin-right", "color")
                duration(2.s, 1.s)
            }
        }).isEqualTo("transition-property: margin-right, color; transition-duration: 2s, 1s")
    }

    @Test
    fun verifyTypography() {
        assertThat(modifierToText { Modifier.widows(Widows.of(2)) }).isEqualTo("widows: 2")
        assertThat(modifierToText { Modifier.widows(3) }).isEqualTo("widows: 3")
        assertThat(modifierToText { Modifier.widows(Widows.Revert) }).isEqualTo("widows: revert")
    }
}