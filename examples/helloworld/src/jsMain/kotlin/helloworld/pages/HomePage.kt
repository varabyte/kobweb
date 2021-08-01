package helloworld.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import helloworld.components.layouts.Layout
import kobweb.core.Page
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

class HomePage : Page {
    @Composable
    override fun render() {
        Layout("Welcome to Kobweb!") {
            Text("Please enter your name")
            var name by remember { mutableStateOf("") }
            Input(
                InputType.Text,
                attrs = {
                    onInput { e -> name = e.value }
                }
            )
            P()
            Text("Hello ${name.takeIf { it.isNotBlank() } ?: "World"}!")
        }
    }
}