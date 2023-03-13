package multimodule.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.functions.url
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundImage
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.core.Page
import org.jetbrains.compose.web.dom.Text

@Page
@Composable
fun HomePage() {
    Box(
        Modifier
            .fillMaxSize()
            .backgroundImage(url("/cat.jpg")), // This image comes from the lib module
        contentAlignment = Alignment.Center
    ) {
        Text("This page comes from the app module")
    }
}