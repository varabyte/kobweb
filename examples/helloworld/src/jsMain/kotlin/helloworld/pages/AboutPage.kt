package helloworld.pages

import androidx.compose.runtime.Composable
import helloworld.components.layouts.PageLayout
import kobweb.core.Page
import kobweb.silk.components.navigation.Link
import kobweb.silk.components.text.Text
import org.jetbrains.compose.web.dom.P

class AboutPage : Page {
    @Composable
    override fun render() {
        PageLayout("ABOUT") {
            Text("This is a skeleton app used to showcase a basic site made using Kobweb")
            P()
            Link("/", "Go Home")
        }
    }
}