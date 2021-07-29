package helloworld.components.sections

import androidx.compose.runtime.Composable
import nekt.core.components.Link
import nekt.ui.components.layout.*
import nekt.ui.config.NektTheme
import nekt.ui.config.toggleColorMode
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLAnchorElement

@Composable
fun NavHeader() {
    val palette = NektTheme.colors.getActivePalette()
    Flex(
        FlexParams(alignItems = AlignItems.Center, justifyContent = JustifyContent.FlexStart),
        attrs = {
            style {
                height(50.px)
                padding(10.px)
                // Intentionally invert the header colors
                backgroundColor(palette.fg)
                makeFlexItem(FlexItemParams(alignSelf = AlignSelf.Stretch))
            }
        }
    ) {
        val navLinkStyles: AttrBuilderContext<HTMLAnchorElement> = {
            style {
                color(palette.bg)
                textDecoration("initial")
                margin(5.px, 15.px)
            }
        }

        Link("/", navLinkStyles) { Text("HOME") }
        Link("/about", navLinkStyles) { Text("ABOUT") }
        Spacer()
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