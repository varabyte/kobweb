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
import multimodule.auth.model.auth.CreateAccountResponse
import multimodule.auth.model.auth.LoginState
import multimodule.core.components.layouts.PageLayout
import multimodule.core.components.sections.CenteredColumnContent
import multimodule.core.components.widgets.LabeledTextInput
import multimodule.core.components.widgets.TextButton
import multimodule.core.styles.ErrorTextStyle

@Page
@Composable
fun CreateAccountPage() {
    PageLayout("Create Account") {
        CenteredColumnContent {
            val ctx = rememberPageContext()
            val coroutine = rememberCoroutineScope()
            var username by remember { mutableStateOf("") }
            var password1 by remember { mutableStateOf("") }
            var password2 by remember { mutableStateOf("") }
            var errorText by remember { mutableStateOf("") }

            errorText = when {
                username.any { it.isWhitespace() } -> "Username cannot contain whitespace."
                password1.any { it.isWhitespace() } -> "Password cannot contain whitespace."
                password1 != password2 -> "Passwords don't match."
                else -> errorText
            }

            fun isValid() = username.isNotEmpty() && password1.isNotEmpty() && errorText.isEmpty()

            fun tryCreate() {
                if (!isValid()) return

                val account = Account(username, password1)
                val accountBytes = Json.encodeToString(account).encodeToByteArray()
                coroutine.launch {
                    val response = window.api.post("/account/create", body = accountBytes)!!
                        .decodeToString().let { Json.decodeFromString(CreateAccountResponse.serializer(), it) }

                    if (response.succeeded) {
                        LoginState.current = LoginState.LoggedIn(account)
                        ctx.router.navigateTo("/chat", UpdateHistoryMode.REPLACE)
                    } else {
                        errorText = "Could not create account. Username is already taken."
                    }
                }
            }

            LabeledTextInput("Username", ref = { it.focus() }, onCommit = ::tryCreate) { errorText = ""; username = it }
            LabeledTextInput("Password", mask = true, onCommit = ::tryCreate) { errorText = ""; password1 = it }
            LabeledTextInput("Confirm Password", mask = true, onCommit = ::tryCreate) { errorText = ""; password2 = it }

            TextButton("Create Account", enabled = isValid(), onClick = ::tryCreate)

            if (errorText.isNotBlank()) {
                SpanText(errorText, ErrorTextStyle.toModifier())
            }
        }
    }
}