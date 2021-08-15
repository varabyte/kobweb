package helloworld

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.core.DefaultApp
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.theme.DEFAULT_PALETTES
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.css.vw
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Div

class MyApp : App by DefaultApp {
    @Composable
    override fun render(content: @Composable () -> Unit) {
        DefaultApp.render {
            val colorMode by rememberColorMode()
            SilkTheme(DEFAULT_PALETTES[colorMode]) {
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