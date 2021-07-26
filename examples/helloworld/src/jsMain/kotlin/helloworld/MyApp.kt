package helloworld

import androidx.compose.runtime.Composable
import nekt.core.App
import nekt.ui.ColorMode
import nekt.ui.Theme
import nekt.ui.getColorMode
import nekt.ui.withTransitionDefaults
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

class MyApp : App() {
    @Composable
    override fun render(content: @Composable () -> Unit) {
        val palette = when (getColorMode()) {
            ColorMode.LIGHT -> Theme.colors.light
            ColorMode.DARK -> Theme.colors.dark
        }
        Theme {
            Div({
                style {
                    withTransitionDefaults("background-color", "color")
                    backgroundColor(palette.background)
                    color(palette.primary)
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