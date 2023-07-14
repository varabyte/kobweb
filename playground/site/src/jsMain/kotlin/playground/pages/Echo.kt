package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.streams.ApiStream
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import playground.components.layouts.PageLayout

@Page
@Composable
fun EchoPage() {
    PageLayout("Echo test") {
        val stream = remember { ApiStream("echo") }
        var lastEchoedText by remember { mutableStateOf("") }
        LaunchedEffect(Unit) {
            stream.connect { lastEchoedText = it }
        }

        Text("Please send some text to get echoed")
        var text by remember { mutableStateOf("") }
        Input(
            InputType.Text,
            attrs = {
                value(text)
                onInput { e -> text = e.value }
            }
        )

        P()
        Button(onClick = {
            stream.send(text)
            text = ""
        }, enabled = text.isNotBlank()) {
            Text("Send")
        }

        P()
        Text("Text from server: $lastEchoedText")
    }
}
