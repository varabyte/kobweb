package multimodule.auth.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.navigation.UpdateHistoryMode
import multimodule.core.components.sections.CenteredColumnContent
import multimodule.core.components.widgets.TextButton
import org.jetbrains.compose.web.dom.*

@Composable
fun LoggedOutMessage() {
    CenteredColumnContent {
        val ctx = rememberPageContext()
        Text("You are not logged in and cannot access this page.")
        TextButton("Go Home") {
            ctx.router.navigateTo("/", UpdateHistoryMode.REPLACE)
        }
    }
}