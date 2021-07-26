package helloworld.pages

import androidx.compose.runtime.Composable
import helloworld.components.TestComposable
import nekt.core.Page
import nekt.ui.toggleColorMode
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

class HomePage : Page(isIndex = true) {
    @Composable
    override fun render() {
        Div {
            TestComposable(slug.value)
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
    }
}