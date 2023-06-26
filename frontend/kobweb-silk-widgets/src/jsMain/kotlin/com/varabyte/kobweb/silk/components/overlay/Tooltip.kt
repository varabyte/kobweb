package com.varabyte.kobweb.silk.components.overlay

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.ElementTarget
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.addVariantBase
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.defer.renderWithDeferred
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Br
import org.w3c.dom.HTMLElement

private fun Modifier.triangleUp(color: CSSColorValue) = styleModifier {
    property("border-color", "$color transparent transparent transparent")
}

private fun Modifier.triangleLeft(color: CSSColorValue) = styleModifier {
    property("border-color", "transparent $color transparent transparent")
}

private fun Modifier.triangleDown(color: CSSColorValue) = styleModifier {
    property("border-color", "transparent transparent $color transparent")
}

private fun Modifier.triangleRight(color: CSSColorValue) = styleModifier {
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

val TooltipBackgroundColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val TooltipColorVar by StyleVariable<CSSColorValue>(prefix = "silk")

val TooltipStyle by ComponentStyle.base(prefix = "silk") {
    Modifier
        .position(Position.Relative) // So arrow is positioned relative to tooltip area
        .backgroundColor(TooltipBackgroundColorVar.value())
        .color(TooltipColorVar.value())
        .borderRadius(6.px)
}

val TooltipArrowStyle by ComponentStyle.base(prefix = "silk") {
    Modifier
        .position(Position.Absolute)
        .borderWidth(TRIANGLE_WIDTH)
        .borderStyle(LineStyle.Solid)
}

val TopLeftTooltipArrowVariant by TooltipArrowStyle.addVariantBase {
    Modifier
        .margin(left = TRIANGLE_WIDTH_2X, top = -TRIANGLE_WIDTH_2X)
        .top(0.px)
        .triangleDown(TooltipBackgroundColorVar.value())
}

val TopTooltipArrowVariant by TooltipArrowStyle.addVariantBase {
    Modifier
        .margin(left = -TRIANGLE_WIDTH, top = -TRIANGLE_WIDTH_2X)
        .left(50.percent)
        .top(0.px)
        .triangleDown(TooltipBackgroundColorVar.value())
}

val TopRightTooltipArrowVariant by TooltipArrowStyle.addVariantBase {
    Modifier
        .margin(right = TRIANGLE_WIDTH_2X, top = -TRIANGLE_WIDTH_2X)
        .right(0.px)
        .top(0.px)
        .triangleDown(TooltipBackgroundColorVar.value())
}

val LeftTopTooltipArrowVariant by TooltipArrowStyle.addVariantBase {
    Modifier
        .margin(top = TRIANGLE_WIDTH_2X, left = -TRIANGLE_WIDTH_2X)
        .left(0.px)
        .top(0.px)
        .triangleLeft(TooltipBackgroundColorVar.value())
}

val LeftTooltipArrowVariant by TooltipArrowStyle.addVariantBase {
    Modifier
        .margin(top = -TRIANGLE_WIDTH, left = -TRIANGLE_WIDTH_2X)
        .left(0.px)
        .top(50.percent)
        .triangleLeft(TooltipBackgroundColorVar.value())
}

val LeftBottomTooltipArrowVariant by TooltipArrowStyle.addVariantBase {
    Modifier
        .margin(bottom = TRIANGLE_WIDTH_2X, left = -TRIANGLE_WIDTH_2X)
        .left(0.px)
        .bottom(0.px)
        .triangleLeft(TooltipBackgroundColorVar.value())
}

val RightTopTooltipArrowVariant by TooltipArrowStyle.addVariantBase {
    Modifier
        .margin(top = TRIANGLE_WIDTH_2X, right = -TRIANGLE_WIDTH_2X)
        .right(0.px)
        .top(0.px)
        .triangleRight(TooltipBackgroundColorVar.value())
}

val RightTooltipArrowVariant by TooltipArrowStyle.addVariantBase {
    Modifier
        .margin(top = -TRIANGLE_WIDTH, right = -TRIANGLE_WIDTH_2X)
        .right(0.px)
        .top(50.percent)
        .triangleRight(TooltipBackgroundColorVar.value())
}

val RightBottomTooltipArrowVariant by TooltipArrowStyle.addVariantBase {
    Modifier
        .margin(bottom = TRIANGLE_WIDTH_2X, right = -TRIANGLE_WIDTH_2X)
        .right(0.px)
        .bottom(0.px)
        .triangleRight(TooltipBackgroundColorVar.value())
}

val BottomLeftTooltipArrowVariant by TooltipArrowStyle.addVariantBase {
    Modifier
        .margin(left = TRIANGLE_WIDTH_2X, bottom = -TRIANGLE_WIDTH_2X)
        .left(0.px)
        .bottom(0.px)
        .triangleUp(TooltipBackgroundColorVar.value())
}

val BottomTooltipArrowVariant by TooltipArrowStyle.addVariantBase {
    Modifier
        .margin(left = -TRIANGLE_WIDTH, bottom = -TRIANGLE_WIDTH_2X)
        .left(50.percent)
        .bottom(0.px)
        .triangleUp(TooltipBackgroundColorVar.value())
}

val BottomRightTooltipArrowVariant by TooltipArrowStyle.addVariantBase {
    Modifier
        .margin(right = TRIANGLE_WIDTH_2X, bottom = -TRIANGLE_WIDTH_2X)
        .right(0.px)
        .bottom(0.px)
        .triangleUp(TooltipBackgroundColorVar.value())
}

val TooltipTextContainerStyle = ComponentStyle.base("tooltip-text") {
    Modifier.padding(5.px)
}

/**
 * A widget for displaying information inside a sort of chat bubble with an (optional) arrow on it.
 *
 * This method should be configurable enough for a majority of cases, but [AdvancedTooltip] is also provided for people
 * who need even more control.
 *
 * See also: [Popover], which shows information without any outer decoration.
 *
 * Note: For users who are only using silk widgets and not kobweb, then you must call [renderWithDeferred] yourself
 * first, as a parent method that this lives under. See the method for more details.
 *
 * @param keepOpenStrategy The strategy for how to keep the tooltip open. If nothing is specified, then the tooltip
 *   will close whenever the user moves the mouse away from the target element.
 */
@Composable
fun Tooltip(
    target: ElementTarget,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    placement: PopupPlacement = PopupPlacement.Bottom,
    hasArrow: Boolean = true,
    offsetPixels: Number = DEFAULT_POPUP_OFFSET_PX,
    placementTarget: ElementTarget? = null,
    showDelayMs: Int = 0,
    hideDelayMs: Int = 0,
    keepOpenStrategy: KeepPopupOpenStrategy? = null,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable PopupScope.() -> Unit,
) {
    val placementStrategy = remember(placement) { PopupPlacementStrategy.of(placement, offsetPixels) }

    AdvancedTooltip(
        target,
        modifier,
        hiddenModifier = Modifier,
        variant,
        hasArrow,
        showDelayMs,
        hideDelayMs,
        openCloseStrategy = null,
        placementTarget,
        placementStrategy,
        keepOpenStrategy,
        ref,
        content
    )
}

/**
 * A convenience [Tooltip] making it trivial to display some text message.
 *
 * You can use newlines in your text to split it across multiple lines.
 *
 * This method should be configurable enough for a majority of cases, but [AdvancedTooltip] is also provided for people
 * who need even more control.
 */
@Composable
fun Tooltip(
    target: ElementTarget,
    text: String,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    placement: PopupPlacement = PopupPlacement.Bottom,
    hasArrow: Boolean = true,
    offsetPixels: Number = DEFAULT_POPUP_OFFSET_PX,
    placementTarget: ElementTarget? = null,
    showDelayMs: Int = 0,
    hideDelayMs: Int = 0,
    keepOpenStrategy: KeepPopupOpenStrategy? = null,
    ref: ElementRefScope<HTMLElement>? = null,
) {
    val placementStrategy = remember(placement) { PopupPlacementStrategy.of(placement, offsetPixels) }

    AdvancedTooltip(
        target,
        text,
        modifier,
        hiddenModifier = Modifier,
        variant,
        hasArrow,
        showDelayMs,
        hideDelayMs,
        openCloseStrategy = null,
        placementTarget,
        placementStrategy,
        keepOpenStrategy,
        ref
    )
}

/**
 * A version of [Tooltip] that allows for more control over the tooltip's behavior.
 *
 * See also: [AdvancedPopover], which documents many of the parameters used here.
 */
@Composable
fun AdvancedTooltip(
    target: ElementTarget,
    modifier: Modifier = Modifier,
    hiddenModifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    hasArrow: Boolean = true,
    showDelayMs: Int = 0,
    hideDelayMs: Int = 0,
    openCloseStrategy: OpenClosePopupStrategy? = null,
    placementTarget: ElementTarget? = null,
    placementStrategy: PopupPlacementStrategy? = null,
    keepOpenStrategy: KeepPopupOpenStrategy? = null,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable PopupScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING") val keepOpenStrategy =
        remember(keepOpenStrategy) { keepOpenStrategy ?: KeepPopupOpenStrategy.never() }

    AdvancedPopover(
        target,
        TooltipStyle.toModifier(variant).then(modifier), hiddenModifier,
        variant,
        showDelayMs, hideDelayMs,
        openCloseStrategy,
        placementTarget,
        placementStrategy,
        keepOpenStrategy,
        ref
    ) {
        content()
        val placement = placement
        if (hasArrow && placement != null) {
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

/**
 * A version of [Tooltip] that allows for more control over the tooltip's behavior.
 *
 * See also: [AdvancedPopover], which documents many of the parameters used here.
 */
@Composable
fun AdvancedTooltip(
    target: ElementTarget,
    text: String,
    modifier: Modifier = Modifier,
    hiddenModifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    hasArrow: Boolean = true,
    showDelayMs: Int = 0,
    hideDelayMs: Int = 0,
    openCloseStrategy: OpenClosePopupStrategy? = null,
    placementTarget: ElementTarget? = null,
    placementStrategy: PopupPlacementStrategy? = null,
    keepOpenStrategy: KeepPopupOpenStrategy? = null,
    ref: ElementRefScope<HTMLElement>? = null,
) {
    AdvancedTooltip(
        target,
        modifier,
        hiddenModifier,
        variant,
        hasArrow,
        showDelayMs,
        hideDelayMs,
        openCloseStrategy,
        placementTarget,
        placementStrategy,
        keepOpenStrategy,
        ref
    ) {
        Column(TooltipTextContainerStyle.toModifier()) {
            text.split("\n").forEach { line -> if (line.isNotEmpty()) SpanText(line) else Br() }
        }
    }
}
