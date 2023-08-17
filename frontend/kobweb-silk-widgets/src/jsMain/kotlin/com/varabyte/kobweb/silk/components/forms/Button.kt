package com.varabyte.kobweb.silk.components.forms

import androidx.compose.runtime.*
import androidx.compose.web.events.SyntheticMouseEvent
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.refScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.isBright
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.active
import com.varabyte.kobweb.silk.components.style.common.DisabledStyle
import com.varabyte.kobweb.silk.components.style.common.ariaDisabled
import com.varabyte.kobweb.silk.components.style.focusVisible
import com.varabyte.kobweb.silk.components.style.hover
import com.varabyte.kobweb.silk.components.style.not
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.ColorScheme
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import org.jetbrains.compose.web.dom.Button as JbButton

val ButtonBackgroundDefaultColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val ButtonBackgroundFocusColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val ButtonBackgroundHoverColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val ButtonBackgroundPressedColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val ButtonColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val ButtonFontSize by StyleVariable<CSSLengthValue>(prefix = "silk")
val ButtonHeight by StyleVariable<CSSLengthValue>(prefix = "silk")
val ButtonPaddingHorizontal by StyleVariable<CSSLengthValue>(prefix = "silk")

val ButtonStyle by ComponentStyle(prefix = "silk") {
    base {
        Modifier
            .color(ButtonColorVar.value())
            .backgroundColor(ButtonBackgroundDefaultColorVar.value())
            .lineHeight(1.2)
            .height(ButtonHeight.value())
            .minWidth(ButtonHeight.value()) // A button should get no more squashed than square / rectangular
            .fontSize(ButtonFontSize.value())
            .fontWeight(FontWeight.SemiBold)
            .whiteSpace(WhiteSpace.NoWrap)
            .padding(leftRight = ButtonPaddingHorizontal.value())
            .verticalAlign(VerticalAlign.Middle)
            .borderRadius(0.375.cssRem)
            .borderWidth(0.px)
            // For focus, we'll use a box shadow instead of an outline. Box shadow combines the general style of a
            // border (which appears outside the button, not inside it) while also not affecting the layout.
            .outline(0.px)
            .userSelect(UserSelect.None) // No selecting text within buttons
            .transition(CSSTransition("background-color", duration = 200.ms))
    }

    (hover + not(ariaDisabled)) {
        Modifier
            .backgroundColor(ButtonBackgroundHoverColorVar.value())
            .cursor(Cursor.Pointer)
    }

    (focusVisible + not(ariaDisabled)) {
        Modifier.boxShadow(spreadRadius = 0.1875.cssRem, color = ButtonBackgroundFocusColorVar.value())
    }

    (active + not(ariaDisabled)) {
        Modifier.backgroundColor(ButtonBackgroundPressedColorVar.value())
    }
}

interface ButtonSize {
    val fontSize: CSSLengthValue
    val height: CSSLengthValue
    val horizontalPadding: CSSLengthValue

    object XS : ButtonSize {
        override val fontSize = 0.75.cssRem
        override val height = 1.5.cssRem
        override val horizontalPadding = 0.5.cssRem
    }

    object SM : ButtonSize {
        override val fontSize = 0.875.cssRem
        override val height = 2.cssRem
        override val horizontalPadding = 0.75.cssRem
    }

    object MD : ButtonSize {
        override val fontSize = 1.cssRem
        override val height = 2.5.cssRem
        override val horizontalPadding = 1.cssRem
    }

    object LG : ButtonSize {
        override val fontSize = 1.125.cssRem
        override val height = 3.cssRem
        override val horizontalPadding = 1.5.cssRem
    }
}

fun ButtonSize.toModifier(): Modifier {
    return Modifier
        .setVariable(ButtonFontSize, fontSize)
        .setVariable(ButtonHeight, height)
        .setVariable(ButtonPaddingHorizontal, horizontalPadding)
}

/**
 * A button widget.
 */
@Composable
fun Button(
    onClick: (evt: SyntheticMouseEvent) -> Unit,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    size: ButtonSize = ButtonSize.MD,
    colorScheme: ColorScheme? = null,
    focusBorderColor: CSSColorValue? = null,
    enabled: Boolean = true,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable BoxScope.() -> Unit
) {
    var backingElement: HTMLButtonElement? by remember { mutableStateOf(null) }
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
                        ButtonColorVar, (if (isBrightColor) ColorMode.LIGHT else ColorMode.DARK).toSilkPalette().color
                    )
                    .setVariable(ButtonBackgroundDefaultColorVar, if (isDark) colorScheme._200 else colorScheme._500)
                    .setVariable(ButtonBackgroundHoverColorVar, if (isDark) colorScheme._300 else colorScheme._600)
                    .setVariable(ButtonBackgroundPressedColorVar, if (isDark) colorScheme._400 else colorScheme._700)

            }
            .thenIf(focusBorderColor != null) {
                Modifier.setVariable(ButtonBackgroundFocusColorVar, focusBorderColor!!)
            }
            .then(modifier)
            .thenIf(enabled) {
                Modifier
                    .onClick { evt ->
                        backingElement!!.focus()
                        onClick(evt)
                        evt.preventDefault()
                        evt.stopPropagation()
                    }
            }
            .toAttrs()
    ) {
        registerRefScope(
            refScope {
                ref { backingElement = it }
                add(ref)
            }
        )

        Box(contentAlignment = Alignment.Center, content = content)
    }
}
