package todo.components.widgets

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div

@Composable
fun TodoCard(onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    // Use "A" so the item supports a11y (you can tab on it and press enter to click it)
    A(href = "#", attrs = {
        tabIndex(0) // Make this item tabbable
        classes("todo", "todoContainer", "todoText")
        if (onClick != null) classes("todoClickable")

        onClick { evt ->
            evt.preventDefault() // We are using "A" for its a11y side effects but don't want its actual behavior
            onClick?.invoke()
        }
    }) {
        content()
    }
}