package todo.components.widgets

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.dom.A
import todo.components.styles.TodoClickableStyle
import todo.components.styles.TodoContainerStyle
import todo.components.styles.TodoStyle
import todo.components.styles.TodoTextStyle

@Composable
fun TodoCard(onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    val styles = mutableListOf(TodoStyle, TodoContainerStyle, TodoTextStyle)
    if (onClick != null) { styles.add(TodoClickableStyle) }

    // Use "A" so the item supports a11y (you can tab on it and press enter to click it)
     A(href = "#", attrs = styles.toModifier().asAttributeBuilder {
        tabIndex(0) // Make this item tabbable

        onClick { evt ->
            evt.preventDefault() // We are using "A" for its a11y side effects but don't want its actual behavior
            onClick?.invoke()
        }
    }) {
        content()
    }
}