package com.varabyte.kobweb.silk.components.forms

import androidx.compose.runtime.*
import androidx.compose.web.events.SyntheticMouseEvent
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.RowScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.graphics.isBright
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.ComponentKind
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.CssStyleVariant
import com.varabyte.kobweb.silk.style.common.DisabledStyle
import com.varabyte.kobweb.silk.style.selectors.active
import com.varabyte.kobweb.silk.style.selectors.ariaDisabled
import com.varabyte.kobweb.silk.style.selectors.focusVisible
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.selectors.not
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.style.vars.animation.TransitionDurationVars
import com.varabyte.kobweb.silk.style.vars.color.ColorVar
import com.varabyte.kobweb.silk.style.vars.color.FocusOutlineColorVar
import com.varabyte.kobweb.silk.style.vars.size.FontSizeVars
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.ColorScheme
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLButtonElement
import org.jetbrains.compose.web.dom.Button as JbButton

object ButtonVars {
    val BackgroundDefaultColor by StyleVariable<CSSColorValue>(prefix = "silk")
    val BackgroundFocusColor by StyleVariable(prefix = "silk", defaultFallback = FocusOutlineColorVar.value())
    val BackgroundHoverColor by StyleVariable<CSSColorValue>(prefix = "silk")
    val BackgroundPressedColor by StyleVariable<CSSColorValue>(prefix = "silk")
    val Color by StyleVariable(prefix = "silk", defaultFallback = ColorVar.value())
    val ColorTransitionDuration by StyleVariable(
        prefix = "silk",
        defaultFallback = TransitionDurationVars.Normal.value()
    )
    val FontSize by StyleVariable<CSSLengthNumericValue>(prefix = "silk")
    val Height by StyleVariable<CSSLengthNumericValue>(prefix = "silk")
    val PaddingHorizontal by StyleVariable<CSSLengthNumericValue>(prefix = "silk")
}

sealed interface ButtonKind : ComponentKind

val ButtonStyle = CssStyle<ButtonKind> {
    base {
        Modifier
            .color(ButtonVars.Color.value())
            .backgroundColor(ButtonVars.BackgroundDefaultColor.value())
            .lineHeight(1.2)
            .height(ButtonVars.Height.value())
            .minWidth(ButtonVars.Height.value()) // A button should get no more squashed than square / rectangular
            .fontSize(ButtonVars.FontSize.value())
            .fontWeight(FontWeight.SemiBold)
            .whiteSpace(WhiteSpace.NoWrap)
            .padding(leftRight = ButtonVars.PaddingHorizontal.value())
            .verticalAlign(VerticalAlign.Middle)
            .borderRadius(0.375.cssRem)
            .border { width(0.px) }
            .userSelect(UserSelect.None) // No selecting text within buttons
            .transition(Transition.of("background-color", duration = ButtonVars.ColorTransitionDuration.value()))
    }

    (hover + not(ariaDisabled)) {
        Modifier
            .backgroundColor(ButtonVars.BackgroundHoverColor.value())
            .cursor(Cursor.Pointer)
    }

    (focusVisible + not(ariaDisabled)) {
        // For focus, we use a box shadow instead of an outline. Box shadow combines the general style of a
        // border (which appears outside the button, not inside it) while also not affecting the layout.
        // However, box shadow is removed in Windows High Contrast mode, so we add an outline with a
        // transparent color, which is normally invisible, but becomes visible in High Contrast mode.
        Modifier
            .outline(2.px, LineStyle.Solid, Colors.Transparent)
            .boxShadow(spreadRadius = 0.1875.cssRem, color = ButtonVars.BackgroundFocusColor.value())
    }

    (active + not(ariaDisabled)) {
        Modifier.backgroundColor(ButtonVars.BackgroundPressedColor.value())
    }
}

class ButtonSize(
    fontSize: CSSLengthNumericValue,
    height: CSSLengthNumericValue,
    horizontalPadding: CSSLengthNumericValue,
) : CssStyle.Restricted.Base(
    Modifier
        .setVariable(ButtonVars.FontSize, fontSize)
        .setVariable(ButtonVars.Height, height)
        .setVariable(ButtonVars.PaddingHorizontal, horizontalPadding)
) {
    companion object {
        val XS = ButtonSize(FontSizeVars.XS.value(), 1.5.cssRem, 0.5.cssRem)
        val SM = ButtonSize(FontSizeVars.SM.value(), 2.cssRem, 0.75.cssRem)
        val MD = ButtonSize(FontSizeVars.MD.value(), 2.5.cssRem, 1.cssRem)
        val LG = ButtonSize(FontSizeVars.LG.value(), 3.cssRem, 1.5.cssRem)
    }
}

/**
 * A button widget.
 */
@Composable
fun Button(
    onClick: (evt: SyntheticMouseEvent) -> Unit,
    modifier: Modifier = Modifier,
    variant: CssStyleVariant<ButtonKind>? = null,
    type: ButtonType = ButtonType.Button,
    enabled: Boolean = true,
    size: ButtonSize = ButtonSize.MD,
    colorScheme: ColorScheme? = null,
    focusBorderColor: CSSColorValue? = null,
    ref: ElementRefScope<HTMLButtonElement>? = null,
    content: @Composable RowScope.() -> Unit
) {
    JbButton(
        attrs = ButtonStyle.toModifier(variant)
            .thenIf(!enabled, DisabledStyle.toModifier())
            .then(size.toModifier())
            .thenIf(colorScheme != null) {
                @Suppress("NAME_SHADOWING") val colorScheme = colorScheme!!
                val isDark = ColorMode.current.isDark
                val isBrightColor = (if (isDark) colorScheme._200 else colorScheme._500).isBright
                Modifier
                    .setVariable(
                        ButtonVars.Color, (if (isBrightColor) ColorMode.LIGHT else ColorMode.DARK).toPalette().color
                    )
                    .setVariable(ButtonVars.BackgroundDefaultColor, if (isDark) colorScheme._200 else colorScheme._500)
                    .setVariable(ButtonVars.BackgroundHoverColor, if (isDark) colorScheme._300 else colorScheme._600)
                    .setVariable(ButtonVars.BackgroundPressedColor, if (isDark) colorScheme._400 else colorScheme._700)

            }
            .setVariable(ButtonVars.BackgroundFocusColor, focusBorderColor)
            .then(modifier)
            .thenIf(enabled) {
                Modifier
                    .onClick { evt ->
                        onClick(evt)
                        evt.stopPropagation()
                    }
            }
            .toAttrs { type(type) }
    ) {
        registerRefScope(ref)

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}
