package kobweb.silk.components.forms

import androidx.compose.runtime.*
import kobweb.compose.css.Cursor
import kobweb.compose.css.UserSelect
import kobweb.compose.ui.*
import kobweb.compose.ui.graphics.Color
import kobweb.silk.components.*
import kobweb.silk.theme.SilkPallete
import kobweb.silk.theme.colors.getColorMode
import kobweb.silk.theme.colors.shifted
import kobweb.silk.theme.shapes.Rect
import kobweb.silk.theme.shapes.Shape
import kobweb.silk.theme.shapes.clip
import org.jetbrains.compose.common.foundation.clickable
import org.jetbrains.compose.common.foundation.layout.Box
import org.jetbrains.compose.common.foundation.layout.fillMaxWidth
import org.jetbrains.compose.common.foundation.layout.width
import org.jetbrains.compose.common.internal.ActualModifier
import org.jetbrains.compose.common.internal.castOrCreate
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.padding
import org.jetbrains.compose.common.ui.unit.dp

enum class ButtonState : ComponentState {
    DEFAULT,
    HOVER,
    PRESSED,
}

abstract class ButtonStyle : ComponentStyle<ButtonState> {
    open val color: Color?
        @Composable
        get() = null

    open val hoverColor: Color?
        @Composable
        get() = null

    open val pressedColor: Color?
        @Composable
        get() = null

    open val shape: Shape?
        @Composable
        get() = null

    @Composable
    override fun modify(modifier: ActualModifier, state: ButtonState) {
        when (state) {
            ButtonState.DEFAULT -> color?.let {
                println("CHANGING COLOR TO: " + it)
                modifier.background(it)
            }
            ButtonState.HOVER -> hoverColor?.let { modifier.background(it) }
            ButtonState.PRESSED -> pressedColor?.let { modifier.background(it) }
        }

        shape?.let { modifier.clip(it) }
    }
}

object ButtonKey : ComponentKey<ButtonStyle>
class BaseButtonStyle : ButtonStyle() {
    override val color: Color
        @Composable
        get() {
            return SilkPallete.current.primary.also { println("BUTTON COLOR: " + it) }
        }

    override val hoverColor: Color
        @Composable
        get() = color.shifted()

    override val pressedColor: Color
        @Composable
        get() = color.shifted().shifted()

    override val shape: Shape
        @Composable
        get() = Rect(4.dp)
}

interface ButtonVariant : ComponentVariant<ButtonState, ButtonStyle>

/**
 * An area which provides a SilkTheme-aware background color.
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
        .minWidth(20.dp)
        .minHeight(20.dp)
        .padding(4.dp),
    content: @Composable () -> Unit
) {
    var state by remember { mutableStateOf(ButtonState.DEFAULT) }
    modifier.castOrCreate().apply {
        SilkComponentStyles.current.modify(ButtonKey, this, state)
    }
    Box(
        modifier
            // Text shouldn't be selectable
            .userSelect(UserSelect.None)
            .onMouseEnter { evt ->
                state = ButtonState.HOVER
            }
            .onMouseLeave {
                state = ButtonState.DEFAULT
            }
            .onMouseDown { evt ->
                if (evt.button == 0.toShort()) {
                    state = ButtonState.PRESSED
                }
            }
            .onMouseUp { evt ->
                if (evt.button == 0.toShort() && state == ButtonState.PRESSED) {
                    onClick()
                    state = ButtonState.DEFAULT
                }
            },
        content
    )
}
