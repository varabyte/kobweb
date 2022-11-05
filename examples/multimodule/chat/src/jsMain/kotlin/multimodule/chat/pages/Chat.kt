package multimodule.chat.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.api
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.style.*
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import multimodule.auth.components.sections.LoggedOutMessage
import multimodule.auth.model.auth.LoginState
import multimodule.chat.model.FetchRequest
import multimodule.chat.model.FetchResponse
import multimodule.chat.model.Message
import multimodule.chat.model.MessageEntry
import multimodule.core.G
import multimodule.core.components.layouts.PageLayout
import multimodule.core.components.sections.CenteredColumnContent
import multimodule.core.components.widgets.TextButton
import multimodule.core.components.widgets.TextInput
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

val ChatBoxStyle = ComponentStyle.base("chat-box") {
    Modifier
        .padding(5.px)
        .borderRadius(5.px)
        .borderStyle(LineStyle.Solid)
        .overflowY(Overflow.Auto)
}

private fun Message.toChatLine() = "${this.username}: ${this.text}"

@Page
@Composable
fun ChatPage() {
    PageLayout("Chat") {
        val account = (LoginState.current as? LoginState.LoggedIn)?.account ?: run {
            LoggedOutMessage()
            return@PageLayout
        }

        var messageEntries by remember { mutableStateOf(listOf<MessageEntry>()) }
        var localMessage by remember { mutableStateOf<Message?>(null) }
        val coroutineScope = rememberCoroutineScope()

        coroutineScope.launch {
            while (true) {
                val request = FetchRequest(messageEntries.lastOrNull()?.id)
                messageEntries = messageEntries + window.api.get("/chat/fetchmessages?request=${Json.encodeToString(FetchRequest.serializer(), request)}")!!
                    .decodeToString().let { Json.decodeFromString<FetchResponse>(it) }.messages
                localMessage = null
                delay(1000)
            }
        }

        CenteredColumnContent {
            Column(ChatBoxStyle.toModifier().height(80.percent).width(G.Ui.Width.Large).fontSize(G.Ui.Text.MediumSmall)) {
                messageEntries.forEach { entry ->
                    val message = entry.message
                    Text(message.toChatLine())
                    Br()
                }
                localMessage?.let { localMessage ->
                    Text(localMessage.toChatLine())
                }
            }
            Box(Modifier.width(G.Ui.Width.Large).height(60.px)) {
                var message by remember { mutableStateOf("") }

                fun sendMessage() {
                    val messageCopy = Message(account.username, message.trim())
                    localMessage = messageCopy
                    message = ""
                    coroutineScope.launch {
                        window.api.post("/chat/sendmessage",
                            body = Json
                                .encodeToString(Message.serializer(), messageCopy)
                                .encodeToByteArray()
                        )
                    }
                }
                TextInput(message, Modifier.width(70.percent).align(Alignment.BottomStart), ref = { it.focus() }, onCommit = ::sendMessage) { message = it }
                TextButton("Send", Modifier.width(20.percent).align(Alignment.BottomEnd), enabled = message.isNotBlank(), onClick = ::sendMessage)
            }
        }
    }
}