package playground.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.rememberPageContext
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text
import playground.components.sections.NavHeader
import playground.utilities.getTitle
import playground.utilities.setTitle

@Composable
fun PageLayout(title: String, content: @Composable () -> Unit) {
    val ctx = rememberPageContext()
    LaunchedEffect(title) { ctx.setTitle(title) }
    PageLayout(ctx, content)
}

@Composable
fun PageLayout(ctx: PageContext, content: @Composable () -> Unit) {
    val title = ctx.getTitle() ?: ""
    LaunchedEffect(title) {
        document.title = title
    }

    Column(
        modifier = Modifier.fillMaxSize().textAlign(TextAlign.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NavHeader()
        H1 { Text(title) }
        content()
    }
}
