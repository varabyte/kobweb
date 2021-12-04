package todo

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import org.jetbrains.compose.web.css.*

const val BORDER_COLOR = "#eaeaea"

object TodoStyleSheet : StyleSheet() {
    init {
        "body" style {
            fontFamily(
                "-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto", "Oxygen", "Ubuntu",
                "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue", "sans-serif"
            )
        }

        "footer" style {
            width(100.percent)
            height(100.px)
            property("border-top", "1px solid $BORDER_COLOR")
            fontSize(1.5.cssRem)

            display(DisplayStyle.Flex)
            justifyContent(JustifyContent.Center)
            alignItems(AlignItems.Center)
        }
    }
}

@App
@Composable
fun MyApp(content: @Composable () -> Unit) {
    Style(TodoStyleSheet)
    SilkApp {
        Surface(Modifier.width(100.vw).height(100.vh)) {
            content()
        }
    }
}