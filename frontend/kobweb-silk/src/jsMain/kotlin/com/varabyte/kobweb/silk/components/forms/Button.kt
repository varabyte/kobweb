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
import com.varabyte.kobweb.silk.components.ComponentModifier
import com.varabyte.kobweb.silk.components.then
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.shifted
import com.varabyte.kobweb.silk.theme.shapes.Rect
import com.varabyte.kobweb.silk.theme.shapes.clip
import org.jetbrains.compose.web.css.px

enum class ButtonState {
    /** The cursor is not over the component */
    DEFAULT,
    /** The cursor is over the component */
    HOVER,
    /** The cursor is pressing down on the component */
    PRESSED,
}

object Buttons {
    const val LEFT = 0.toShort()
    const val MIDDLE = 1.toShort()
    const val RIGHT = 2.toShort()
}

val ButtonKey = ComponentKey("silk-button")
object DefaultButtonModifier : ComponentModifier {
    @Composable
    override fun toModifier(data: Any?): Modifier {
        val state = data as ButtonState

        val modifier =
        when (state) {
            ButtonState.DEFAULT -> SilkTheme.palette.primary
            ButtonState.HOVER -> SilkTheme.palette.primary.shifted()
            ButtonState.PRESSED -> SilkTheme.palette.primary.shifted().shifted()
        }.let { color -> Modifier.background(color) }

        return modifier
            .clip(Rect(4.px))
            .styleModifier {
                // No selecting text within buttons
                userSelect(UserSelect.None)
            }
    }
}

/**
 * An area which provides a SilkTheme-aware background color.
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ComponentModifier? = null,
    content: @Composable () -> Unit
) {
    var inButton by remember { mutableStateOf(false) }
    var state by remember { mutableStateOf(ButtonState.DEFAULT) }

    Box(
        SilkTheme.componentModifiers[ButtonKey].then(variant).toModifier(state)
            .then(modifier)
            // Button text shouldn't be selectable
            .userSelect(UserSelect.None)
            .onMouseEnter {
                state = ButtonState.HOVER
                inButton = true
            }
            .onMouseLeave {
                state = ButtonState.DEFAULT
                inButton = false
            }
            .onMouseDown { evt ->
                if (evt.button == Buttons.LEFT) {
                    state = ButtonState.PRESSED
                }
            }
            .onMouseUp { evt ->
                if (evt.button == Buttons.LEFT && state == ButtonState.PRESSED) {
                    onClick()
                    state = if (inButton) { ButtonState.HOVER } else { ButtonState.DEFAULT }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}