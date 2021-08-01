package helloworld.pages

import androidx.compose.runtime.Composable
import helloworld.components.layouts.Layout
import kobweb.core.Page
import org.jetbrains.compose.web.dom.Text

class AboutPage : Page {
    @Composable
    override fun render() {
        Layout("ABOUT") {
            Text("This is a skeleton app used to showcase a basic site made using Kobweb")
        }
    }
}