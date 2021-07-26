package helloworld.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun TestComposable(name: String = "World") {
    Div {
        Text("Hello ")
        Text(name)
    }
}