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
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.components.style.common.DisabledStyle
import com.varabyte.kobweb.silk.components.style.common.ariaDisabled
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import org.jetbrains.compose.web.dom.Button as JbButton


val ButtonStyle by ComponentStyle(prefix = "silk") {
    val palette = colorMode.toSilkPalette()
    val buttonColors = palette.button

    base {
        Modifier
            .color(palette.color)
            .backgroundColor(buttonColors.default)
            .lineHeight(1.2)
            .fontSize(16.px)
            .padding(topBottom = 8.px, leftRight = 16.px)
            .borderRadius(4.px)
            .borderWidth(0.px)
            // For focus, we'll use a box shadow instead of an outline. Box shadow combines the general style of a
            // border (which appears outside the button, not inside it) while also not affecting the layout.
            .outline(0.px)
            .userSelect(UserSelect.None) // No selecting text within buttons
    }

    (hover + not(ariaDisabled)) {
        Modifier
            .backgroundColor(buttonColors.hover)
            .cursor(Cursor.Pointer)
    }

    (focusVisible + not(ariaDisabled)) {
        Modifier.boxShadow(spreadRadius = 3.px, color = buttonColors.focus)
    }

    (active + not(ariaDisabled)) {
        Modifier.backgroundColor(buttonColors.pressed)
    }
}

/**
 * A button widget.
 */
@Composable
fun Button(
    onClick: (evt: SyntheticMouseEvent) -> Unit,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    enabled: Boolean = true,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable BoxScope.() -> Unit
) {
    var backingElement: HTMLButtonElement? by remember { mutableStateOf(null) }
    JbButton(
        attrs = ButtonStyle.toModifier(variant)
            .thenIf(!enabled, DisabledStyle.toModifier().tabIndex(-1))
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
