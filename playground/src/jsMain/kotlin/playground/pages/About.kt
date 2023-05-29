package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import playground.components.layouts.PageLayout
import playground.components.widgets.GoHomeLink
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Page
@Composable
fun AboutPage() {
    PageLayout("ABOUT") {
        Text("This is a skeleton app used to showcase a basic site made using Kobweb")
        P()
        GoHomeLink()
    }
}