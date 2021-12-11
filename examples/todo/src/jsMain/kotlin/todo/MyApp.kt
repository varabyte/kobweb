package todo

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.InitSilk
import com.varabyte.kobweb.silk.InitSilkContext
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import org.jetbrains.compose.web.css.*

const val BORDER_COLOR = "#eaeaea"

@InitSilk
fun initSiteStyles(ctx: InitSilkContext) {
    ctx.config.registerStyle("body") {
        base {
            Modifier.styleModifier {
                fontFamily(
                    "-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto", "Oxygen", "Ubuntu",
                    "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue", "sans-serif"
                )
            }
        }
    }

    ctx.config.registerStyle("footer") {
        base {
            Modifier.width(100.percent).height(100.px).fontSize(1.5.cssRem).styleModifier {
                property("border-top", "1px solid $BORDER_COLOR")

                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.Center)
                alignItems(AlignItems.Center)
            }
        }
    }
}

@App
@Composable
fun MyApp(content: @Composable () -> Unit) {
    SilkApp {
        Surface(Modifier.width(100.vw).height(100.vh)) {
            content()
        }
    }
}