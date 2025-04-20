package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.silk.components.forms.TextInput
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import playground.components.layouts.PageLayoutData

@InitRoute
fun initHomePage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Welcome to Kobweb!"))
}

@Page
@Composable
fun HomePage() {
    Text("Please enter your name")
    var name by remember { mutableStateOf("") }
    TextInput(name, onTextChange = { name = it })
    P()
    Text("Hello ${name.takeIf { it.isNotBlank() } ?: "World"}!")
}
