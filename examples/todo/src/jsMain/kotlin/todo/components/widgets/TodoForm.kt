package todo.components.widgets

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Form
import org.jetbrains.compose.web.dom.Input
import todo.components.styles.TodoContainerStyle
import todo.components.styles.TodoInputStyle
import todo.components.styles.TodoStyle
import todo.components.styles.TodoTextStyle

@Composable
fun TodoForm(placeholder: String, loading: Boolean, submitTodo: (String) -> Unit) {
    if (loading) {
        TodoCard {
            LoadingSpinner()
        }
    } else {
        var todo by remember { mutableStateOf("") }
        Form(attrs = listOf(TodoStyle, TodoContainerStyle).toModifier().asAttributeBuilder {
            onSubmit { evt ->
                evt.preventDefault()
                submitTodo(todo)
            }
        }) {
            Input(
                InputType.Text,
                attrs = listOf(TodoStyle, TodoTextStyle, TodoInputStyle).toModifier()
                    .asAttributeBuilder {
                        placeholder(placeholder)
                        name("todo")
                        onChange { todo = it.value }
                    }
            )
        }
    }
}