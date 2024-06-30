package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.TextInput
import com.varabyte.kobwebx.chrome.ai.TextSession
import com.varabyte.kobwebx.chrome.ai.ai
import js.promise.await
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import playground.components.layouts.PageLayout
import web.streams.ReadableStreamReadDoneResult
import web.streams.ReadableStreamReadValueResult

@Page
@Composable
fun Ai() {
    var response by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val nano = window.ai
    var session: TextSession? = null

    LaunchedEffect(Unit) {
        session = nano.createTextSession().await()
    }

    fun sendTextAndClear() {
        scope.launch {
            if (session == null) {
                console.error("Session is not initialized")
                return@launch
            }
            val stream = session!!.promptStreaming(text).getReader()
            text = ""
            while (true) {
                when (val result = stream.read().await()) {
                    is ReadableStreamReadDoneResult -> break
                    is ReadableStreamReadValueResult -> {
                        response = result.value
                    }
                }
            }
        }
    }

    PageLayout("Ai test") {
        Text("Please send some text to get a response from the AI")
        TextInput(text, onTextChange = { text = it }, onCommit = { sendTextAndClear() })
        P()
        Button(onClick = { sendTextAndClear() }, enabled = text.isNotBlank()) {
            Text("Send")
        }

        P()
        Text("Text from Ai")
        P()
        Text(response)
    }
}
