package helloworld

import androidx.compose.runtime.Composable
import kobweb.core.App
import kobweb.core.DefaultApp
import kobweb.silk.components.layout.Surface
import kobweb.silk.theme.SilkTheme
import kobweb.silk.theme.shapes.Circle
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

class MyApp : App by DefaultApp {
    @Composable
    override fun render(content: @Composable () -> Unit) {
        DefaultApp.render {
            SilkTheme {
                Div({
                    style {
                        height(100.vh)
                        width(100.vw)
                    }
                }) {
                    Surface {
                        content()
                    }
                }
            }
        }
    }
}