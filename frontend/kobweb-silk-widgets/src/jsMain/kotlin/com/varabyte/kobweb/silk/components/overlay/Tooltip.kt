package com.varabyte.kobweb.silk.components.overlay

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ElementTarget
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
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
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.*

val TooltipStyle = ComponentStyle.base("silk-tooltip") {
    val palette = colorMode.toSilkPalette()

    Modifier
        .backgroundColor(palette.tooltip.background)
        .color(palette.tooltip.color)
        .borderRadius(6.px)

}

val TooltipArrowStyle = ComponentStyle.base("silk-tooltip-arrow") {
    Modifier
        .position(Position.Absolute)
        .borderWidth(5.px)
        .borderStyle(LineStyle.Solid)
}

val BottomTooltipArrowVariant = TooltipArrowStyle.addVariantBase("bottom") {
    val palette = colorMode.toSilkPalette()

    Modifier
        .margin(left = (-5).px, bottom = (-10).px)
        .left(50.percent)
        .bottom(0.px)
        .styleModifier {
            property("border-color", "${palette.color} transparent transparent transparent")
        }
}

val LeftTooltipArrowVariant = TooltipArrowStyle.addVariantBase("left") {
    val palette = colorMode.toSilkPalette()

    Modifier
        .margin(top = (-5).px, left = (-10).px)
        .left(0.px)
        .top(50.percent)
        .styleModifier {
            property("border-color", "transparent ${palette.color} transparent transparent")
        }
}

val TopTooltipArrowVariant = TooltipArrowStyle.addVariantBase("up") {
    val palette = colorMode.toSilkPalette()

    Modifier
        .margin(left = (-5).px, top = (-10).px)
        .left(50.percent)
        .top(0.px)
        .styleModifier {
            property("border-color", "transparent transparent ${palette.color} transparent")
        }
}

val RightTooltipArrowVariant = TooltipArrowStyle.addVariantBase("right") {
    val palette = colorMode.toSilkPalette()

    Modifier
        .margin(top = (-5).px, right = (-10).px)
        .right(0.px)
        .top(50.percent)
        .styleModifier {
            property("border-color", "transparent transparent transparent ${palette.color}")
        }
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
    variant: ComponentVariant? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Popup(target, Modifier, placement, offsetPixels, null) {
        Box(
            TooltipStyle.toModifier(variant).then(modifier),
        ) {
            content()
            if (hasArrow) {
                Box(
                    // e.g. if tooltip is below the target, arrow points up
                    TooltipArrowStyle.toModifier(
                        when (placement) {
                            PopupPlacement.Top -> BottomTooltipArrowVariant
                            PopupPlacement.Left -> RightTooltipArrowVariant
                            PopupPlacement.Right -> LeftTooltipArrowVariant
                            PopupPlacement.Bottom -> TopTooltipArrowVariant
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
    variant: ComponentVariant? = null,
) {
    Tooltip(target, modifier, placement, hasArrow, offsetPixels, variant) {
        Column(Modifier.padding(5.px)) {
            text.split("\n").forEach { line -> SpanText(line) }
        }
    }
}
