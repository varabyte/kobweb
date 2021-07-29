package helloworld

import androidx.compose.runtime.Composable
import nekt.core.App
import nekt.core.DefaultApp
import nekt.ui.config.NektTheme
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.selectors.className
import org.jetbrains.compose.web.dom.Div

class MyApp : App by DefaultApp {
    @Composable
    override fun render(content: @Composable () -> Unit) {
        DefaultApp.render {
            NektTheme {
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