package helloworld.components.layouts

import androidx.compose.runtime.Composable
import helloworld.components.sections.NavHeader
import kobweb.compose.foundation.layout.Column
import kobweb.compose.ui.fillMaxSize
import kobweb.silk.components.text.Text
import org.jetbrains.compose.common.ui.Alignment
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.web.dom.H1

@Composable
fun PageLayout(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NavHeader()
        H1 { Text(title) }
        content()
    }
}