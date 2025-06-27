package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.compose.css.functions.url
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
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
    fun verifyBackground() {
        assertThat(modifierToText {
            Modifier.background(Colors.Red)
        }).isEqualTo("background: red")

        assertThat(modifierToText {
            Modifier.background(Colors.Blue, Background.of(BackgroundImage.of(url("test.png"))))
        }).isEqualTo("background: url(\"test.png\") blue")

        assertThat(modifierToText {
            Modifier.backgroundAttachment(BackgroundAttachment.Fixed)
        }).isEqualTo("background-attachment: fixed")

        assertThat(modifierToText {
            Modifier.backgroundBlendMode(BackgroundBlendMode.Multiply)
        }).isEqualTo("background-blend-mode: multiply")

        assertThat(modifierToText {
            Modifier.backgroundBlendMode(BackgroundBlendMode.Screen, BackgroundBlendMode.Overlay)
        }).isEqualTo("background-blend-mode: screen, overlay")

        assertThat(modifierToText {
            Modifier.backgroundBlendMode(listOf(BackgroundBlendMode.Multiply, BackgroundBlendMode.ColorDodge))
        }).isEqualTo("background-blend-mode: multiply, color-dodge")

        assertThat(modifierToText {
            Modifier.backgroundClip(BackgroundClip.ContentBox)
        }).isEqualTo("background-clip: content-box")

        assertThat(modifierToText {
            Modifier.backgroundColor(Colors.Blue)
        }).isEqualTo("background-color: blue")

        assertThat(modifierToText {
            Modifier.backgroundColor(BackgroundColor.Inherit)
        }).isEqualTo("background-color: inherit")

        assertThat(modifierToText {
            Modifier.backgroundImage(BackgroundImage.None)
        }).isEqualTo("background-image: none")

        assertThat(modifierToText {
            Modifier.backgroundImage(url("test.png"))
        }).isEqualTo("background-image: url(\"test.png\")")

        assertThat(modifierToText {
            Modifier.backgroundImage(linearGradient(Colors.Red, Colors.Green))
        }).isEqualTo("background-image: linear-gradient(red, green)")

        assertThat(modifierToText {
            Modifier.backgroundOrigin(BackgroundOrigin.BorderBox)
        }).isEqualTo("background-origin: border-box")

        assertThat(modifierToText {
            Modifier.backgroundPosition(BackgroundPosition.of(CSSPosition.Center))
        }).isEqualTo("background-position: left 50% top 50%")

        assertThat(modifierToText {
            Modifier.backgroundRepeat(BackgroundRepeat.NoRepeat)
        }).isEqualTo("background-repeat: no-repeat")

        assertThat(modifierToText {
            Modifier.backgroundRepeat(BackgroundRepeat.Space, BackgroundRepeat.Round)
        }).isEqualTo("background-repeat: space round")

        assertThat(modifierToText {
            Modifier.backgroundSize(BackgroundSize.Contain)
        }).isEqualTo("background-size: contain")
    }

    @Test
    fun verifyBox() {
        assertThat(modifierToText {
            Modifier.boxDecorationBreak(BoxDecorationBreak.Clone)
        }).isEqualTo("box-decoration-break: clone")

        assertThat(modifierToText {
            Modifier.boxSizing(BoxSizing.BorderBox)
        }).isEqualTo("box-sizing: border-box")

        assertThat(modifierToText {
            Modifier.boxShadow(
                offsetX = 0.px,
                offsetY = 1.px,
                blurRadius = 3.px,
                spreadRadius = 1.px,
                color = Colors.Gray,
                inset = true
            )
        }).isEqualTo("box-shadow: inset 0px 1px 3px 1px gray")

        assertThat(modifierToText {
            Modifier.boxShadow(
                BoxShadow.of(0.px, 1.px, 3.px, 1.px, Colors.Red),
                BoxShadow.of(0.px, 1.px, 2.px, 0.px, Colors.Gray)
            )
        }).isEqualTo("box-shadow: 0px 1px 3px 1px red, 0px 1px 2px 0px gray")

        assertThat(modifierToText {
            Modifier.boxShadow(BoxShadow.None)
        }).isEqualTo("box-shadow: none")

        assertThat(modifierToText {
            Modifier.boxShadow(BoxShadow.Unset)
        }).isEqualTo("box-shadow: unset")

        assertThat(modifierToText {
            Modifier.boxShadow(BoxShadow.of(0.px, 1.px, 3.px, 1.px, Colors.Gray))
        }).isEqualTo("box-shadow: 0px 1px 3px 1px gray")
    }

    @Test
    fun verifyContain() {
        assertThat(modifierToText {
            Modifier.contain(Contain.Strict)
        }).isEqualTo("contain: strict")

        assertThat(modifierToText {
            Modifier.contain(Contain.Size, Contain.Paint)
        }).isEqualTo("contain: size paint")
        assertThat(modifierToText {
            Modifier.contain(listOf(Contain.Size, Contain.Layout, Contain.Paint))
        }).isEqualTo("contain: size layout paint")

        assertThat(modifierToText {
            Modifier.containIntrinsicBlockSize(ContainIntrinsicBlockSize.None)
        }).isEqualTo("contain-intrinsic-block-size: none")
        assertThat(modifierToText {
            Modifier.containIntrinsicBlockSize(ContainIntrinsicBlockSize.of(100.px))
        }).isEqualTo(
            "contain-intrinsic-block-size: 100px"
        )
        assertThat(modifierToText {
            Modifier.containIntrinsicBlockSize(100.px, auto = true)
        }).isEqualTo("contain-intrinsic-block-size: auto 100px")

        assertThat(modifierToText {
            Modifier.containIntrinsicInlineSize(ContainIntrinsicInlineSize.None)
        })
            .isEqualTo("contain-intrinsic-inline-size: none")
        assertThat(modifierToText { Modifier.containIntrinsicInlineSize(ContainIntrinsicInlineSize.of(100.px)) }).isEqualTo(
            "contain-intrinsic-inline-size: 100px"
        )
        assertThat(modifierToText {
            Modifier.containIntrinsicInlineSize(100.px, auto = true)
        }).isEqualTo("contain-intrinsic-inline-size: auto 100px")
    }

    @Test
    fun verifyHyphenateCharacter() {
        assertThat(modifierToText {
            Modifier.hyphenateCharacter(HyphenateCharacter.Auto)
        }).isEqualTo("hyphenate-character: auto")

        assertThat(modifierToText {
            Modifier.hyphenateCharacter("-")
        }).isEqualTo("hyphenate-character: \"-\"")
    }

    @Test
    fun verifyLayout() {
        assertThat(modifierToText {
            Modifier.aspectRatio(1.5)
        }).isEqualTo("aspect-ratio: 1.5")

        assertThat(modifierToText {
            Modifier.aspectRatio(16, 9)
        }).isEqualTo("aspect-ratio: 16 / 9")

        assertThat(modifierToText {
            Modifier.aspectRatio(AspectRatio.Auto)
        }).isEqualTo("aspect-ratio: auto")

        assertThat(modifierToText {
            Modifier.clear(Clear.Both)
        }).isEqualTo("clear: both")

        assertThat(modifierToText {
            Modifier.lineHeight(20.px)
        }).isEqualTo("line-height: 20px")

        assertThat(modifierToText {
            Modifier.lineHeight(1.5)
        }).isEqualTo("line-height: 1.5")

        assertThat(modifierToText {
            Modifier.lineHeight(LineHeight.Normal)
        }).isEqualTo("line-height: normal")

        assertThat(modifierToText {
            Modifier.margin(10.px)
        }).isEqualTo("margin: 10px")

        assertThat(modifierToText {
            Modifier.margin {
                top(10.px)
                right(20.px)
                bottom(30.px)
                left(40.px)
            }
        }).isEqualTo("margin-top: 10px; margin-right: 20px; margin-bottom: 30px; margin-left: 40px")

        assertThat(modifierToText {
            Modifier.margin(10.px, 20.px)
        }).isEqualTo("margin: 10px 20px")

        assertThat(modifierToText {
            Modifier.margin(10.px, 20.px, 30.px)
        }).isEqualTo("margin: 10px 20px 30px")

        assertThat(modifierToText {
            Modifier.margin(10.px, 20.px, 30.px, 40.px)
        }).isEqualTo("margin: 10px 20px 30px 40px")

        assertThat(modifierToText {
            Modifier.marginBlock(10.px)
        }).isEqualTo("margin-block: 10px")

        assertThat(modifierToText {
            Modifier.marginBlock(10.px, 20.px)
        }).isEqualTo("margin-block: 10px 20px")

        assertThat(modifierToText {
            Modifier.marginBlock {
                start(10.px)
                end(20.px)
            }
        }).isEqualTo("margin-block-start: 10px; margin-block-end: 20px")

        assertThat(modifierToText {
            Modifier.overflow(Overflow.Hidden)
        }).isEqualTo("overflow: hidden")

        assertThat(modifierToText {
            Modifier.overflow(Overflow.Auto, Overflow.Scroll)
        }).isEqualTo("overflow: auto scroll")

        assertThat(modifierToText {
            Modifier.overflow {
                x(Overflow.Hidden)
                y(Overflow.Scroll)
            }
        }).isEqualTo("overflow-x: hidden; overflow-y: scroll")

        assertThat(modifierToText {
            Modifier.overflowWrap(OverflowWrap.BreakWord)
        }).isEqualTo("overflow-wrap: break-word")

        assertThat(modifierToText {
            Modifier.padding(10.px)
        }).isEqualTo("padding: 10px")

        assertThat(modifierToText {
            Modifier.padding {
                top(10.px)
                right(20.px)
                bottom(30.px)
                left(40.px)
            }
        }).isEqualTo("padding-top: 10px; padding-right: 20px; padding-bottom: 30px; padding-left: 40px")

        assertThat(modifierToText {
            Modifier.padding(10.px, 20.px)
        }).isEqualTo("padding: 10px 20px")

        assertThat(modifierToText {
            Modifier.padding(10.px, 20.px, 30.px)
        }).isEqualTo("padding: 10px 20px 30px")

        assertThat(modifierToText {
            Modifier.padding(10.px, 20.px, 30.px, 40.px)
        }).isEqualTo("padding: 10px 20px 30px 40px")

        assertThat(modifierToText {
            Modifier.paddingBlock(10.px)
        }).isEqualTo("padding-block: 10px")

        assertThat(modifierToText {
            Modifier.paddingBlock(10.px, 20.px)
        }).isEqualTo("padding-block: 10px 20px")

        assertThat(modifierToText {
            Modifier.paddingBlock {
                start(10.px)
                end(20.px)
            }
        }).isEqualTo("padding-block-start: 10px; padding-block-end: 20px")

        assertThat(modifierToText {
            Modifier.resize(Resize.Both)
        }).isEqualTo("resize: both")

        assertThat(modifierToText {
            Modifier.verticalAlign(VerticalAlign.Middle)
        }).isEqualTo("vertical-align: middle")

        assertThat(modifierToText {
            Modifier.verticalAlign(10.px)
        }).isEqualTo("vertical-align: 10px")

        assertThat(modifierToText {
            Modifier.zIndex(100)
        }).isEqualTo("z-index: 100")
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
    fun verifyOrphans() {
        assertThat(modifierToText {
            Modifier.orphans(2)
        }).isEqualTo("orphans: 2")

        assertThat(modifierToText {
            Modifier.orphans(Orphans.Inherit)
        }).isEqualTo("orphans: inherit")
    }

    @Test
    fun verifyOutline() {
        assertThat(modifierToText {
            Modifier.outline(
                Outline.of(
                    OutlineWidth.of(2.px),
                    LineStyle.Dotted,
                    Colors.Green
                )
            )
        }).isEqualTo("outline: 2px dotted green")

        assertThat(modifierToText {
            Modifier.outline(3.px, LineStyle.Solid, Colors.Magenta)
        }).isEqualTo("outline: 3px solid magenta")

        assertThat(modifierToText {
            Modifier.outline(Outline.Inherit)
        }).isEqualTo("outline: inherit")

        assertThat(modifierToText {
            Modifier.outline {
                color(Colors.Red)
                style(LineStyle.Dotted)
                width(2.px)
            }
        }).isEqualTo("outline-color: red; outline-style: dotted; outline-width: 2px")

        assertThat(modifierToText {
            Modifier.outline {
                color(Colors.Blue)
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
            Modifier.textShadow(2.px, 2.px, 2.px, Colors.Gray)
        }).isEqualTo("text-shadow: 2px 2px 2px gray")

        assertThat(modifierToText {
            Modifier.textShadow(TextShadow.of(2.px, 2.px), TextShadow.of(4.px, 4.px, 2.px, Colors.Red))
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
    fun verifyTextEmphasisPosition() {
        assertThat(modifierToText {
            Modifier.textEmphasisPosition(TextEmphasisPosition.Auto)
        }).isEqualTo("text-emphasis-position: auto")

        assertThat(modifierToText {
            Modifier.textEmphasisPosition(TextEmphasisPosition.Over, TextEmphasisPosition.Left)
        }).isEqualTo("text-emphasis-position: over left")

        assertThat(modifierToText {
            Modifier.textEmphasisPosition(TextEmphasisPosition.Right, TextEmphasisPosition.Under)
        }).isEqualTo("text-emphasis-position: right under")
    }

    @Test
    fun verifyTextIndent() {
        assertThat(modifierToText {
            Modifier.textIndent(25.px)
        }).isEqualTo("text-indent: 25px")
        assertThat(modifierToText {
            Modifier.textIndent(25.em)
        }).isEqualTo("text-indent: 25em")
        assertThat(modifierToText {
            Modifier.textIndent(15.percent)
        }).isEqualTo("text-indent: 15%")
    }

    @Test
    fun verifyTextUnderlineOffset() {
        assertThat(modifierToText {
            Modifier.textUnderlineOffset(TextUnderlineOffset.Auto)
        }).isEqualTo("text-underline-offset: auto")

        assertThat(modifierToText {
            Modifier.textUnderlineOffset(TextUnderlineOffset.of(0.1.em))
        }).isEqualTo("text-underline-offset: 0.1em")

        assertThat(modifierToText {
            Modifier.textUnderlineOffset(TextUnderlineOffset.of(20.percent))
        }).isEqualTo("text-underline-offset: 20%")
    }

    @Test
    fun verifyTextUnderlinePosition() {
        assertThat(modifierToText {
            Modifier.textUnderlinePosition(TextUnderlinePosition.Auto)
        }).isEqualTo("text-underline-position: auto")

        assertThat(modifierToText {
            Modifier.textUnderlinePosition(TextUnderlinePosition.Under, TextUnderlinePosition.Left)
        }).isEqualTo("text-underline-position: under left")

        assertThat(modifierToText {
            Modifier.textUnderlinePosition(TextUnderlinePosition.Right, TextUnderlinePosition.FromFont)
        }).isEqualTo("text-underline-position: right from-font")
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