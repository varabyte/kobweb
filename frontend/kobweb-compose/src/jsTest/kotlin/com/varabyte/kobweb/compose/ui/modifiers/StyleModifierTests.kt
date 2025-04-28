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
}