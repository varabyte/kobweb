package helloworld

import androidx.compose.runtime.Composable
import kobweb.core.App
import kobweb.core.DefaultApp
import kobweb.silk.config.SilkTheme
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

class MyApp : App by DefaultApp {
    @Composable
    override fun render(content: @Composable () -> Unit) {
        DefaultApp.render {
            SilkTheme {
                Div({
                    style {
                        minHeight(100.vh)
                        minWidth(100.vw)
                        lineHeight(15.px)
                    }
                }) {
                    content()
                }
            }
        }
    }
}