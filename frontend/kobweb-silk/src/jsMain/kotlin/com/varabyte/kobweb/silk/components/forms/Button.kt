package com.varabyte.kobweb.silk.components.forms

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.UserSelect
import com.varabyte.kobweb.compose.css.userSelect
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.background
import com.varabyte.kobweb.compose.ui.onMouseDown
import com.varabyte.kobweb.compose.ui.onMouseEnter
import com.varabyte.kobweb.compose.ui.onMouseLeave
import com.varabyte.kobweb.compose.ui.onMouseUp
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.userSelect
import com.varabyte.kobweb.silk.components.ComponentKey
import com.varabyte.kobweb.silk.components.ComponentStyle
import com.varabyte.kobweb.silk.components.ComponentVariant
import com.varabyte.kobweb.silk.components.CursorState
import com.varabyte.kobweb.silk.components.toModifier
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.shifted
import com.varabyte.kobweb.silk.theme.shapes.Rect
import com.varabyte.kobweb.silk.theme.shapes.clip
import org.jetbrains.compose.web.css.px

interface ButtonStyle : ComponentStyle<CursorState>
class DefaultButtonStyle : ButtonStyle {
    @Composable
    @ReadOnlyComposable
    override fun toModifier(state: CursorState): Modifier {
        var modifier: Modifier = Modifier

        when (state) {
            CursorState.DEFAULT -> SilkTheme.palette.primary
            CursorState.HOVER -> SilkTheme.palette.primary.shifted()
            CursorState.PRESSED -> SilkTheme.palette.primary.shifted().shifted()
        }.let { color -> modifier = modifier.background(color) }

        modifier = modifier
                .clip(Rect(4.px))
                .styleModifier {
                    // No selecting text within buttons
                    userSelect(UserSelect.None)
                }
        return modifier
    }
}

object ButtonKey : ComponentKey<ButtonStyle>
interface ButtonVariant : ComponentVariant<CursorState, ButtonStyle>

object Buttons {
    const val LEFT = 0.toShort()
    const val MIDDLE = 1.toShort()
    const val RIGHT = 2.toShort()
}

/**
 * An area which provides a SilkTheme-aware background color.
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant? = null,
    content: @Composable () -> Unit
) {
    var state by remember { mutableStateOf(CursorState.DEFAULT) }
    var inButton by remember { mutableStateOf(false) }
    Box(
        SilkTheme.componentStyles[ButtonKey].toModifier(state, variant)
            .then(modifier)
            // Button text shouldn't be selectable
            .userSelect(UserSelect.None)
            .onMouseEnter {
                state = CursorState.HOVER
                inButton = true
            }
            .onMouseLeave {
                state = CursorState.DEFAULT
                inButton = false
            }
            .onMouseDown { evt ->
                if (evt.button == Buttons.LEFT) {
                    state = CursorState.PRESSED
                }
            }
            .onMouseUp { evt ->
                if (evt.button == Buttons.LEFT && state == CursorState.PRESSED) {
                    onClick()
                    state = if (inButton) { CursorState.HOVER } else { CursorState.DEFAULT }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}