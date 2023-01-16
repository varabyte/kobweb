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
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.borderWidth
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.lineHeight
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.onKeyDown
import com.varabyte.kobweb.compose.ui.modifiers.outline
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.tabIndex
import com.varabyte.kobweb.compose.ui.modifiers.userSelect
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.active
import com.varabyte.kobweb.silk.components.style.common.DisabledStyle
import com.varabyte.kobweb.silk.components.style.common.ariaDisabled
import com.varabyte.kobweb.silk.components.style.focus
import com.varabyte.kobweb.silk.components.style.hover
import com.varabyte.kobweb.silk.components.style.not
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLElement
import org.jetbrains.compose.web.dom.Button as JbButton


val ButtonStyle = ComponentStyle("silk-button") {
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
            // By default, don't use outlines to indicate focused buttons - we'll use background color instead to
            // indicate focus
            .outline(0.px)
            .userSelect(UserSelect.None) // No selecting text within buttons
    }

    (hover + not(ariaDisabled)) {
        Modifier
            .backgroundColor(buttonColors.hover)
            .cursor(Cursor.Pointer)
    }

    (focus + not(ariaDisabled)) {
        Modifier.backgroundColor(buttonColors.hover)
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
    var backingElement: HTMLElement? = remember { null }

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
                    }
                    .onKeyDown { evt ->
                        if (evt.isComposing) return@onKeyDown
                        if (evt.key == "Enter" || evt.key == "Space") {
                            backingElement!!.click()
                            evt.preventDefault()
                        }
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
