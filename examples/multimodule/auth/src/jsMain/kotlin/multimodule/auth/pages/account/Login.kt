package multimodule.auth.pages.account

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.api
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.navigation.UpdateHistoryMode
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import multimodule.auth.model.auth.Account
import multimodule.auth.model.auth.LoginResponse
import multimodule.auth.model.auth.LoginState
import multimodule.core.components.layouts.PageLayout
import multimodule.core.components.sections.CenteredColumnContent
import multimodule.core.components.widgets.TextButton
import multimodule.core.components.widgets.LabeledTextInput
import multimodule.core.styles.ErrorTextStyle

@Page
@Composable
fun LoginPage() {
    PageLayout("Login") {
        CenteredColumnContent {
            val ctx = rememberPageContext()
            val coroutine = rememberCoroutineScope()
            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var errorText by remember { mutableStateOf("") }

            errorText = when {
                username.any { it.isWhitespace() } -> "Username cannot contain whitespace."
                password.any { it.isWhitespace() } -> "Password cannot contain whitespace."
                else -> errorText
            }

            fun isValid() = username.isNotEmpty() && username.none { it.isWhitespace() }
                    && password.isNotEmpty() && password.none { it.isWhitespace() }

            fun tryLogin() {
                if (!isValid()) return

                val account = Account(username, password)
                val accountBytes = Json.encodeToString(account).encodeToByteArray()
                coroutine.launch {
                    val response = window.api.post("/account/login", body = accountBytes)!!
                        .decodeToString().let { Json.decodeFromString(LoginResponse.serializer(), it) }

                    if (response.succeeded) {
                        LoginState.current = LoginState.LoggedIn(account)
                        ctx.router.navigateTo("/chat", UpdateHistoryMode.REPLACE)
                    } else {
                        errorText = "Login failed. Invalid username / password?"
                    }
                }
            }

            LabeledTextInput("Username", ref = { it.focus() }, onCommit = ::tryLogin) { errorText = ""; username = it }
            LabeledTextInput("Password", mask = true, onCommit = ::tryLogin) { errorText = ""; password = it }

            TextButton("Login", enabled = isValid(), onClick = { tryLogin() })

            if (errorText.isNotBlank()) {
                SpanText(errorText, ErrorTextStyle.toModifier())
            }
        }
    }
}