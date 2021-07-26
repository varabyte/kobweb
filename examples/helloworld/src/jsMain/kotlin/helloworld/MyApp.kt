package helloworld

import androidx.compose.runtime.Composable
import nekt.core.App
import nekt.ui.Theme
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

class MyApp : App() {
    @Composable
    override fun render(content: @Composable () -> Unit) {
        Theme {
            Div({
                style {
                    backgroundColor(Theme.colors.dark.background)
                    color(Theme.colors.dark.primary)
                    display(DisplayStyle.Flex)
                    flexDirection(FlexDirection.Column)
                    minHeight(100.vh)
                }
            }) {
                content()
            }
        }
    }
}