package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.forms.TextInput
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import playground.components.layouts.PageLayout

//object MyButtonSize {
//    val XS = ButtonSize(1.13.cssRem, 1.13.cssRem, 0.5.cssRem)
//}

@Page
@Composable
fun HomePage() {
    PageLayout("Welcome to Kobweb!") {
        Text("Please enter your name")
        var name by remember { mutableStateOf("") }
        TextInput(name, onTextChanged = { name = it })
        P()
        Text("Hello ${name.takeIf { it.isNotBlank() } ?: "World"}!")
//        Button(
//            onClick = {},
//            size = MyButtonSize.XS
//        ) {
//            Text("Click me!")
//        }
    }
}
