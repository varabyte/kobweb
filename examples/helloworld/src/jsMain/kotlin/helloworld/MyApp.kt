package helloworld

import androidx.compose.runtime.Composable
import nekt.core.App
import nekt.core.DefaultApp
import nekt.ui.config.Theme
import nekt.ui.css.withTransitionDefaults
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

class MyApp : App by DefaultApp {

    @Composable
    override fun render(content: @Composable () -> Unit) {
        val palette = Theme.colors.getActivePalette()
        Theme {
            Div({
                style {
                    withTransitionDefaults("background-color", "color")
                    backgroundColor(palette.bg)
                    color(palette.fg)
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