package com.varabyte.kobweb.silk.components.overlay

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.ElementTarget
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.defer.renderWithDeferred
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Br
import org.w3c.dom.HTMLElement

private fun Modifier.triangleUp(color: Color) = styleModifier {
    property("border-color", "$color transparent transparent transparent")
}

private fun Modifier.triangleLeft(color: Color) = styleModifier {
    property("border-color", "transparent $color transparent transparent")
}

private fun Modifier.triangleDown(color: Color) = styleModifier {
    property("border-color", "transparent transparent $color transparent")
}

private fun Modifier.triangleRight(color: Color) = styleModifier {
    property("border-color", "transparent transparent transparent $color")
}

private val TRIANGLE_WIDTH = 5.px
// Note: This following constant is used to shift the triangle into view. For example, a "down" arrow is actually the
// top half of a border where only one of the three sides is not transparent. Imaging the following triangles were all
// jammed together into a single solid box (which I can't capture well in ascii art), where left, right, and bottom
// triangles are transparent:
//  ▼   <-- top part of border, height = 5.px -┐
// ► ◄  <-- left / right parts of border       ├ height = 10.px
//  ▲   <-- bottom part of border             -┘
// To expose the "down" triangle, we need to shift the whole height of the border box to have it peek out.
//
// Note2: We use a size 1px smaller that 2x width as otherwise sometimes the way pixels round on pages as you zoom out
// result in the triangle getting separated from the message box by a sliver due to, presumably, rounding errors. This
// -1 should be an unnoticeable difference at normal and zoomed in views but definitely seems to help zoomed out cases
// from separating.
private val TRIANGLE_WIDTH_2X = 9.px

val TooltipStyle by ComponentStyle.base(prefix = "silk-") {
    val palette = colorMode.toSilkPalette()

    Modifier
        .position(Position.Relative) // So arrow is positioned relative to tooltip area
        .backgroundColor(palette.tooltip.background)
        .color(palette.tooltip.color)
        .borderRadius(6.px)
}

val TooltipArrowStyle by ComponentStyle.base(prefix = "silk-") {
    Modifier
        .position(Position.Absolute)
        .borderWidth(TRIANGLE_WIDTH)
        .borderStyle(LineStyle.Solid)
}

val TopLeftTooltipArrowVariant = TooltipArrowStyle.addVariantBase("top-left") {
    Modifier
        .margin(left = TRIANGLE_WIDTH_2X, top = -TRIANGLE_WIDTH_2X)
        .top(0.px)
        .triangleDown(colorMode.toSilkPalette().color)
}

val TopTooltipArrowVariant = TooltipArrowStyle.addVariantBase("top") {
    Modifier
        .margin(left = -TRIANGLE_WIDTH, top = -TRIANGLE_WIDTH_2X)
        .left(50.percent)
        .top(0.px)
        .triangleDown(colorMode.toSilkPalette().color)
}

val TopRightTooltipArrowVariant = TooltipArrowStyle.addVariantBase("top-right") {
    Modifier
        .margin(right = TRIANGLE_WIDTH_2X, top = -TRIANGLE_WIDTH_2X)
        .right(0.px)
        .top(0.px)
        .triangleDown(colorMode.toSilkPalette().color)
}

val LeftTopTooltipArrowVariant = TooltipArrowStyle.addVariantBase("left-top") {
    Modifier
        .margin(top = TRIANGLE_WIDTH_2X, left = -TRIANGLE_WIDTH_2X)
        .left(0.px)
        .top(0.px)
        .triangleLeft(colorMode.toSilkPalette().color)
}

val LeftTooltipArrowVariant = TooltipArrowStyle.addVariantBase("left") {
    Modifier
        .margin(top = -TRIANGLE_WIDTH, left = -TRIANGLE_WIDTH_2X)
        .left(0.px)
        .top(50.percent)
        .triangleLeft(colorMode.toSilkPalette().color)
}

val LeftBottomTooltipArrowVariant = TooltipArrowStyle.addVariantBase("left-bottom") {
    Modifier
        .margin(bottom = TRIANGLE_WIDTH_2X, left = -TRIANGLE_WIDTH_2X)
        .left(0.px)
        .bottom(0.px)
        .triangleLeft(colorMode.toSilkPalette().color)
}

val RightTopTooltipArrowVariant = TooltipArrowStyle.addVariantBase("right-top") {
    Modifier
        .margin(top = TRIANGLE_WIDTH_2X, right = -TRIANGLE_WIDTH_2X)
        .right(0.px)
        .top(0.px)
        .triangleRight(colorMode.toSilkPalette().color)
}

val RightTooltipArrowVariant = TooltipArrowStyle.addVariantBase("right") {
    Modifier
        .margin(top = -TRIANGLE_WIDTH, right = -TRIANGLE_WIDTH_2X)
        .right(0.px)
        .top(50.percent)
        .triangleRight(colorMode.toSilkPalette().color)
}

val RightBottomTooltipArrowVariant = TooltipArrowStyle.addVariantBase("right-bottom") {
    Modifier
        .margin(bottom = TRIANGLE_WIDTH_2X, right = -TRIANGLE_WIDTH_2X)
        .right(0.px)
        .bottom(0.px)
        .triangleRight(colorMode.toSilkPalette().color)
}

val BottomLeftTooltipArrowVariant = TooltipArrowStyle.addVariantBase("bottom-left") {
    Modifier
        .margin(left = TRIANGLE_WIDTH_2X, bottom = -TRIANGLE_WIDTH_2X)
        .left(0.px)
        .bottom(0.px)
        .triangleUp(colorMode.toSilkPalette().color)
}

val BottomTooltipArrowVariant = TooltipArrowStyle.addVariantBase("bottom") {
    Modifier
        .margin(left = -TRIANGLE_WIDTH, bottom = -TRIANGLE_WIDTH_2X)
        .left(50.percent)
        .bottom(0.px)
        .triangleUp(colorMode.toSilkPalette().color)
}

val BottomRightTooltipArrowVariant = TooltipArrowStyle.addVariantBase("bottom-right") {
    Modifier
        .margin(right = TRIANGLE_WIDTH_2X, bottom = -TRIANGLE_WIDTH_2X)
        .right(0.px)
        .bottom(0.px)
        .triangleUp(colorMode.toSilkPalette().color)
}

val TooltipTextContainerStyle = ComponentStyle.base("tooltip-text") {
    Modifier.padding(5.px)
}

/**
 * A widget for displaying information inside a sort of chat bubble with an (optional) arrow on it.
 *
 * See also: [Popup], which shows information without any outer decoration.
 *
 * Note: For users who are only using silk widgets and not kobweb, then you must call [renderWithDeferred] yourself
 * first, as a parent method that this lives under. See the method for more details.
 */
@Composable
fun Tooltip(
    target: ElementTarget,
    modifier: Modifier = Modifier,
    placement: PopupPlacement = PopupPlacement.Bottom,
    hasArrow: Boolean = true,
    offsetPixels: Number = DEFAULT_POPUP_OFFSET_PX,
    placementTarget: ElementTarget? = null,
    variant: ComponentVariant? = null,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Popup(target, Modifier, placement, offsetPixels, placementTarget, ref = ref) {
        Box(
            TooltipStyle.toModifier(variant).then(modifier),
        ) {
            content()
            if (hasArrow) {
                Box(
                    // e.g. if tooltip is below the target, arrow points up
                    TooltipArrowStyle.toModifier(
                        when (placement) {
                            PopupPlacement.TopLeft -> BottomLeftTooltipArrowVariant
                            PopupPlacement.Top -> BottomTooltipArrowVariant
                            PopupPlacement.TopRight -> BottomRightTooltipArrowVariant
                            PopupPlacement.LeftTop -> RightTopTooltipArrowVariant
                            PopupPlacement.Left -> RightTooltipArrowVariant
                            PopupPlacement.LeftBottom -> RightBottomTooltipArrowVariant
                            PopupPlacement.RightTop -> LeftTopTooltipArrowVariant
                            PopupPlacement.Right -> LeftTooltipArrowVariant
                            PopupPlacement.RightBottom -> LeftBottomTooltipArrowVariant
                            PopupPlacement.BottomLeft -> TopLeftTooltipArrowVariant
                            PopupPlacement.Bottom -> TopTooltipArrowVariant
                            PopupPlacement.BottomRight -> TopRightTooltipArrowVariant
                        }
                    )
                )
            }
        }
    }
}

/**
 * A convenience [Tooltip] used for displaying some text message.
 *
 * You can use newlines in your text to split it across multiple lines.
 */
@Composable
fun Tooltip(
    target: ElementTarget,
    text: String,
    modifier: Modifier = Modifier,
    placement: PopupPlacement = PopupPlacement.Bottom,
    hasArrow: Boolean = true,
    offsetPixels: Number = DEFAULT_POPUP_OFFSET_PX,
    placementTarget: ElementTarget? = null,
    variant: ComponentVariant? = null,
    ref: ElementRefScope<HTMLElement>? = null,
) {
    Tooltip(target, modifier, placement, hasArrow, offsetPixels,  placementTarget, variant, ref) {
        Column(TooltipTextContainerStyle.toModifier()) {
            text.split("\n").forEach { line -> if (line.isNotEmpty()) SpanText(line) else Br() }
        }
    }
}