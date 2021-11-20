package com.varabyte.kobweb.silk.components.forms

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.UserSelect
import com.varabyte.kobweb.compose.css.cursor
import com.varabyte.kobweb.compose.css.userSelect
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.clickable
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.shapes.Rect
import com.varabyte.kobweb.silk.theme.shapes.clip
import org.jetbrains.compose.web.css.px

object Buttons {
    const val LEFT = 0.toShort()
    const val MIDDLE = 1.toShort()
    const val RIGHT = 2.toShort()
}

val ButtonStyle = ComponentStyle("silk-button") { colorMode ->
    val buttonColors = SilkTheme.palettes[colorMode].button

    base = Modifier.background(buttonColors.default).clip(Rect(4.px)).styleModifier {
        // No selecting text within buttons
        userSelect(UserSelect.None)
        property("role", "button")
    }
    hover = Modifier.background(buttonColors.hover).styleModifier {
        cursor(Cursor.Pointer)
    }
    active = Modifier.background(buttonColors.pressed)
}

/**
 * An area which provides a SilkTheme-aware background color.
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    content: @Composable () -> Unit
) {
    Box(
        ButtonStyle.toModifier(variant)
            .then(modifier)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}