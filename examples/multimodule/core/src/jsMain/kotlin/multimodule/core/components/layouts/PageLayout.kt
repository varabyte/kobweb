package multimodule.core.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import kotlinx.browser.document
import multimodule.core.components.sections.NavHeader
import org.jetbrains.compose.web.css.*

@Composable
fun PageLayout(title: String, content: @Composable () -> Unit) {
    LaunchedEffect(title) {
        document.title = title
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NavHeader()
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 10.px, left = 50.px, right = 50.px),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}