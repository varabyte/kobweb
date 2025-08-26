package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.silk.components.forms.TextInput
import com.varabyte.kobweb.silk.components.icons.fa.FaCheck
import com.varabyte.kobweb.silk.components.icons.lucide.LucideCamera
import com.varabyte.kobweb.silk.components.icons.lucide.LucideCheck
import com.varabyte.kobweb.silk.components.icons.lucide.LucideZoomIn
import com.varabyte.kobweb.silk.components.icons.mdi.MdiCheck
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px
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


    P()
    Text("Here are some icons:")
    LucideCamera(size = 10.em, color = Colors.Red)
    LucideZoomIn()
    LucideCheck(modifier = Modifier.size(20.px))



}
