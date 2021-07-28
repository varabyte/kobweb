package helloworld.pages

import androidx.compose.runtime.Composable
import helloworld.components.TestComposable
import nekt.core.Page
import nekt.core.components.Link
import nekt.ui.config.toggleColorMode
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

class HomePage : Page {
    @Composable
    override fun render() {
        Div {
            TestComposable("Test")
        }
        Div {
            Button(
                attrs = {
                    onClick {
                        toggleColorMode()
                    }
                }
            ) {
                Text("Toggle Color Mode")
            }
        }
        Div {
            Link("/about") {
                Text("ABOUT")
            }
        }
    }
}