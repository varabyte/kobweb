package helloworld.pages

import androidx.compose.runtime.Composable
import nekt.core.Page
import nekt.core.components.Link
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

class AboutPage : Page {
    @Composable
    override fun render() {
        Div {
            Text("ABOUT")
        }
        Div {
            Link("/") {
                Text("GO BACK")
            }
        }
    }
}