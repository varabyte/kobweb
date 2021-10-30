package com.varabyte.kobweb.example.todo.components.widgets

import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Form
import org.jetbrains.compose.web.dom.Input

@Composable
fun TodoForm(placeholder: String, loading: Boolean, submitTodo: (String) -> Unit) {
    if (loading) {
        TodoCard {
            LoadingSpinner()
        }
    } else {
        var todo by remember { mutableStateOf("") }
        Form(attrs = {
            classes("todo", "todoContainer")
            onSubmit { evt ->
                evt.preventDefault()
                submitTodo(todo)
            }
        }) {
            Input(InputType.Text, attrs = {
                classes("todo", "todoText", "todoInput")
                placeholder(placeholder)
                name("todo")
                onChange { todo = it.value }
            })
        }
    }
}
