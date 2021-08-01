package helloworld.components.layouts

import androidx.compose.runtime.Composable
import helloworld.components.sections.NavHeader
import kobweb.silk.components.layout.Flex
import kobweb.silk.components.layout.FlexParams
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text

@Composable
fun Layout(title: String, content: @Composable () -> Unit) {
    Flex(
        FlexParams(direction = FlexDirection.Column, alignItems = AlignItems.Center),
    ) {
        NavHeader()
        H1 { Text(title) }
        content()
    }
}