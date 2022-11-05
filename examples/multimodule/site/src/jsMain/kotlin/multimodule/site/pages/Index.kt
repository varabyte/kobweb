package multimodule.site.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import multimodule.auth.model.auth.LoginState
import multimodule.core.components.layouts.PageLayout
import multimodule.core.components.sections.CenteredColumnContent
import multimodule.core.components.widgets.TextButton

@Page
@Composable
fun HomePage() {
    PageLayout("Kobweb Chat") {
        val ctx = rememberPageContext()
        CenteredColumnContent {
            if (LoginState.current is LoginState.LoggedIn) {
                TextButton("Go to Chat") { ctx.router.navigateTo("/chat") }
            }
            TextButton("Create Account") { ctx.router.navigateTo("/account/create") }
            TextButton("Login") { ctx.router.navigateTo("/account/login") }
        }
    }
}