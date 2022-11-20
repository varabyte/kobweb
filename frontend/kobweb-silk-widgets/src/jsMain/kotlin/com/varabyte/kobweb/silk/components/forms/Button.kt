package com.varabyte.kobweb.silk.components.forms

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.clearFocus
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.theme.shapes.Rect
import com.varabyte.kobweb.silk.theme.shapes.clip
import com.varabyte.kobweb.silk.theme.toSilkPalette
import kotlinx.browser.document
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
            .fontSize(16.px)
            .padding(0.px)
            .clip(Rect(4.px))
            .borderWidth(0.px)
            .outline(0.px) // Don't outline focused buttons - we'll use background color instead to indicate focus
            // No selecting text within buttons
            .userSelect(UserSelect.None)
    }

    hover {
        Modifier
            .backgroundColor(buttonColors.hover)
            .cursor(Cursor.Pointer)
    }

    focus {
        Modifier.backgroundColor(buttonColors.hover)
    }

    active {
        Modifier.backgroundColor(buttonColors.pressed)
    }

    disabled {
        Modifier
            .backgroundColor(buttonColors.default)
            .cursor(Cursor.Default)
            .opacity(0.5)
    }
}

/**
 * An area which provides a SilkTheme-aware background color.
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    enabled: Boolean = true,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable BoxScope.() -> Unit
) {
    JbButton(
        attrs = ButtonStyle.toModifier(variant)
            .thenIf(!enabled, Modifier.disabled())
            .then(modifier)
            .onClick { evt ->
                if (enabled) {
                    document.activeElement?.clearFocus()
                    onClick()
                }
                evt.preventDefault()
            }
            .tabIndex(0) // Allow button to be tabbed to
            .onKeyDown { evt ->
                if (!enabled) return@onKeyDown
                if (evt.isComposing) return@onKeyDown
                if (evt.key == "Enter") {
                    onClick()
                    evt.preventDefault()
                }
            }.toAttrs()
    ) {
        registerRefScope(ref)
        Box(contentAlignment = Alignment.Center, content = content)
    }

}